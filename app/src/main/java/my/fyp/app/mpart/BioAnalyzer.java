package my.fyp.app.mpart;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.TextureView;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

class BioAnalyzer {

    private final Activity activity;
    private MeasureStore store;
    private MeasureStore storeBPM;
    private MeasureStore storeCount;

    private final int measurementInterval = 45;
    private final int measurementLength = 1000*400; // ensure the number of data points is the power of two
    private final int clipLength = 3500;

    private int detectedValleys = 0;
    private int ticksPassed = 0;

    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();

    private CountDownTimer timer;

    private final Handler mainHandler;

    MediaPlayer player;

    BioAnalyzer(Activity activity, TextureView graphTextureView, Handler mainHandler) {
        this.activity = activity;
        this.mainHandler = mainHandler;
    }



    private boolean detectValley() {
        final int valleyDetectionWindowSize = 13;
        CopyOnWriteArrayList<Measurement<Integer>> subList = store.getLastStdValues(valleyDetectionWindowSize);
        if (subList.size() < valleyDetectionWindowSize) {
            return false;
        } else {
            Integer referenceValue = subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f)).measurement;

            for (Measurement<Integer> measurement : subList) {
                if (measurement.measurement < referenceValue) return false;
            }

            // filter out consecutive measurements due to too high measurement rate
            return (!subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f)).measurement.equals(
                    subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f) - 1).measurement));
        }
    }

    void measurePulse(TextureView textureView, CameraService cameraService) {

        // 20 times a second, get the amount of red on the picture.
        // detect local minimums, calculate pulse.

        store = new MeasureStore();
        storeBPM = new MeasureStore();
        storeCount = new MeasureStore();
        storeBPM.add(0);
        storeBPM.add(0);
        CopyOnWriteArrayList<Float> l = new CopyOnWriteArrayList<Float>();
        l.add(0f);
        l.add(0f);
        CopyOnWriteArrayList<Integer> posList = new CopyOnWriteArrayList<Integer>();
        posList.add(0);

        detectedValleys = 0;

        timer = new CountDownTimer(measurementLength, measurementInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                // skip the first measurements, which are broken by exposure metering
                if (clipLength > (++ticksPassed * measurementInterval)) return;

                Thread thread = new Thread(() -> {
                    Bitmap currentBitmap = textureView.getBitmap();
                    int pixelCount = textureView.getWidth() * textureView.getHeight();
                    int measurement = 0;
                    int[] pixels = new int[pixelCount];
                    int initialCount = 0;
                    float BPM = 0;
                    int count=0;


                    currentBitmap.getPixels(pixels, 0, textureView.getWidth(), 0, 0, textureView.getWidth(), textureView.getHeight());

                    // extract the red component
                    // https://developer.android.com/reference/android/graphics/Color.html#decoding
                    for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
                        measurement += (pixels[pixelIndex] >> 16) & 0xff;
                    }
                    // max int is 2^31 (2147483647) , so width and height can be at most 2^11,
                    // as 2^8 * 2^11 * 2^11 = 2^30, just below the limit

                    store.add(measurement);

                    if (detectValley()) {
                        detectedValleys = detectedValleys + 1;
                        valleys.add(store.getLastTimestamp().getTime());
                        // in 13 seconds (13000 milliseconds), I expect 15 valleys. that would be a pulse of 15 / 130000 * 60 * 1000 = 69

                        String currentValue = String.format(
                                Locale.getDefault(),
                                activity.getResources().getQuantityString(R.plurals.measurement_output_template, detectedValleys),
                                (valleys.size() == 1)
                                        ? (60f * (detectedValleys) / (Math.max(1, (measurementLength - millisUntilFinished - clipLength) / 1000f)))
                                        : (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))),
                                detectedValleys,
                                1f * (measurementLength - millisUntilFinished - clipLength) / 1000f);

                        String startMSG = "Press the START button to begin breathing session";
                        sendMessage(BiofeedbackActivity.MESSAGE_UPDATE_REALTIME, startMSG);
                        Log.d(TAG, "Realtime " + currentValue);


                        // update needle position

                        BPM = (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))) ;
                        l.add(BPM);

                        float newBPM = l.get(l.size()-1); //last element
                        float prevBPM = l.get(l.size()-2); //second last element
                        int countSize = l.size();

                        float diff = newBPM - prevBPM;
                        int duration = Math.round(1f * (measurementLength - millisUntilFinished - clipLength) / 1000f);
                        Log.d(TAG,"Timing: "+ duration);
                        Log.d(TAG,"Change in BPM: "+ diff);

                        int position = posList.get(posList.size()-1);

                        // measure heart rate (bpm) every 20 seconds
                        if(detectedValleys % 20 ==0) {
                            //if increase bpm, move needle to the right
                            if (diff < 0) {
                                if (position % 2 == 0) {
                                    position = position + 2;
                                }
                                else{
                                    position = position + 1;
                                }

                                posList.add(position);
                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
                                Log.d(TAG,"Position sent: "+ position);
                                Log.d(TAG, "calmer - move towards blue");

                            //if decrease bpm, move needle to the left
                            } else if (diff > 0.05 && position>=2) {
                                if (position % 2 == 0) {
                                    position = position - 1;
                                }
                                else{
                                    position = position - 2;
                                }

                                posList.add(position);
                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
                                Log.d(TAG,"Position sent: "+ position);
                                Log.d(TAG, "move towards pink");
                            }
                        }



                    }

                });
                thread.start();


            }

            @Override
            public void onFinish() {

                CopyOnWriteArrayList<Measurement<Float>> stdValues = store.getStdValues();

                // clip the interval to the first till the last one - on this interval, there were detectedValleys - 1 periods

                // If the camera only provided a static image, there are no valleys in the signal.
                // A camera not available error is shown, which is the most likely cause.
                if (valleys.size() == 0) {
                    mainHandler.sendMessage(Message.obtain(
                            mainHandler,
                            RecordActivity.MESSAGE_CAMERA_NOT_AVAILABLE,
                            "No valleys detected - there may be an issue when accessing the camera."));
                    return;
                }

                String currentValue = String.format(
                        Locale.getDefault(),
                        activity.getResources().getQuantityString(R.plurals.measurement_output_template, detectedValleys - 1),
                        60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f)),
                        detectedValleys - 1,
                        1f * (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f);


                sendMessage(RecordActivity.MESSAGE_UPDATE_REALTIME, currentValue);

                StringBuilder returnValueSb = new StringBuilder();
                returnValueSb.append(currentValue);
                returnValueSb.append(activity.getString(R.string.row_separator));


                float breatheRate = (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))) /4;

                String currentValue2 = String.format(
                        Locale.getDefault(),
                        activity.getResources().getQuantityString(R.plurals.breathRate_output_template, detectedValleys - 1),
                        (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f)))/4);

                sendMessage(RecordActivity.MESSAGE_UPDATE_REALTIME, returnValueSb.toString());


                StringBuilder returnValueSb2 = new StringBuilder();
                returnValueSb2.append(currentValue2);
                returnValueSb2.append(activity.getString(R.string.row_separator));

                sendMessage(RecordActivity.MESSAGE_UPDATE_FINAL,currentValue2);

                cameraService.stop();
            }
        };

        timer.start();
    }

    void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    void sendMessage(int what, Object message) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mainHandler.sendMessage(msg);
    }
}
