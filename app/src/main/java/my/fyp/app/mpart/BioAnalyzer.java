package my.fyp.app.mpart;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

class BioAnalyzer {

    private final Activity activity;

//    private final ChartDrawer chartDrawer;

    private MeasureStore store;
    private MeasureStore storeBPM;
    private MeasureStore storeCount;

    private final int measurementInterval = 45;
//    private final int measurementLength = 15000; // ensure the number of data points is the power of two
private final int measurementLength = 1000*400;
    private final int clipLength = 3500;

    private int detectedValleys = 0;
    private int ticksPassed = 0;

    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();

    private CountDownTimer timer;

    private final Handler mainHandler;

    MediaPlayer player;

    BioAnalyzer(Activity activity, TextureView graphTextureView, Handler mainHandler) {
        this.activity = activity;
//        this.chartDrawer = new ChartDrawer(graphTextureView);
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
                        // most important!

                        String currentValue = String.format(
                                Locale.getDefault(),
                                activity.getResources().getQuantityString(R.plurals.measurement_output_template, detectedValleys),
                                (valleys.size() == 1)
                                        ? (60f * (detectedValleys) / (Math.max(1, (measurementLength - millisUntilFinished - clipLength) / 1000f)))
                                        : (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))),
                                detectedValleys,
                                1f * (measurementLength - millisUntilFinished - clipLength) / 1000f);

//                        sendMessage(BiofeedbackActivity.MESSAGE_UPDATE_REALTIME, currentValue);
                        String startMSG = "Press the START button to begin breathing session";
                        sendMessage(BiofeedbackActivity.MESSAGE_UPDATE_REALTIME, startMSG);
                        Log.d(TAG, "REALTIME:" + currentValue);




                        // needle animation update :)
                        // working v1
//                        BPM = (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))) ;
//
//                        int meterValue = (int)BPM;
//                        storeBPM.add(meterValue);
//                        Log.d(TAG, "BPM: " + Integer.toString(meterValue));
//
//                        sendMessage(BiofeedbackActivity.MESSAGE_METER, meterValue);

                        //v2
                        BPM = (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))) ;
                        l.add(BPM);

//                        int meterValue = (int)BPM;
//                        storeBPM.add(meterValue);
//                        Log.d(TAG, "BPM msg: " + Integer.toString(meterValue));
//                        count++;
//                        storeCount.add(count);

                        float newBPM = l.get(l.size()-1); //last element
                        float prevBPM = l.get(l.size()-2); //second last element
                        int countSize = l.size();

                        float diff = newBPM - prevBPM;
                        int duration = (int) (1f * (measurementLength - millisUntilFinished - clipLength) / 1000f);
                        Log.d(TAG,"Timing: "+ duration);

                        int position = posList.get(posList.size()-1);

                        if(duration % 15 ==0) {
                            //if increase bpm, pos -2
                            //if decrease bpm, pos + 2
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
                                Log.d(TAG, "calmer");
                            } else if (diff > 2 && position>=2) {
                                if (position % 2 == 0) {
                                    position = position - 1;
                                }
                                else{
                                    position = position - 2;
                                }

                                posList.add(position);
                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
                                Log.d(TAG,"Position sent: "+ position);
                                Log.d(TAG, "redder");
                            }
                        }


//                        if(duration % 12 ==0) {
//                            if (diff < 0) { //BPM decrease,calmer
//                                position = 2; //move right
//                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                Log.d(TAG, "needle msg right");
//                                Log.d(TAG, "needle msg diff: " + diff);
//                            } else if (diff > 5) { //BPM increase by more than 10bpm, excited
//                                position = 3; //move left
//                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                Log.d(TAG, "needle msg left");
//                                Log.d(TAG, "needle msg diff: " + diff);
//                            }
//                        }






//                        int j;
//                        for(int i=0; i<9999 ; i++)
//                        while(flag){
//                            // starting point
//                            if(i>0){
//                                value[i] = meterValue;
//                                diff = value[i] - value[i-1];
//                                if(diff<0){ //BPM decrease,calmer
//                                    position = 2; //move right
//                                    sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                    Log.d(TAG, "needle msg right");
//                                    Log.d(TAG, "needle msg diff: " + diff);
//                                }
//                                else if(diff>10){ //BPM increase by more than 10bpm, excited
//                                    position = 3; //move left
//                                    sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                    Log.d(TAG, "needle msg left");
//                                    Log.d(TAG, "needle msg diff: " + diff);
//                                }
//
//                            }
//
//                            //if BPM decrease, needle move right by 10px
//                            //if BPM increase by more than 10bpm, needle move left by 2px
//                            //calculate difference in currBPM and prevBPM
//                            else{
//                                value[0] = meterValue;
//                                position = 1; //initial pos
//                                sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                Log.d(TAG, "needle msg initial");
//                            }
//
//                        }






//                        if(initialCount == 0){
//                            initialBPM = Integer.valueOf(currentValue);
//                            initialCount++;
//                            //display pink
//                        }
//
//                        else if(initialCount>0){
//                            int newBPM = Integer.valueOf(currentValue);
//                            float change = ((newBPM - initialBPM) * 100) / initialBPM;
//                            float absChange = Math.abs(change);
//
//                            if(chang e < 0 && absChange <= 4 ){
//                                //display light blue+
//                            }
//                        }






                    }

//                    Thread meterthread = new Thread(() -> {
//
//
////                        CopyOnWriteArrayList<Measurement<Float>> counter = storeCount.getStdValues();
////                        CopyOnWriteArrayList<Measurement<Float>> meter = storeBPM.getStdValues();
////                        int countSize = meter.size();
////                        Log.d(TAG, "BPM count: " + countSize);
//
//
////                            Log.d(TAG, "send positionnn");
//
//
////                            float newBPM = meter.get(meter.size() - 1).measurement;
////                            float prevBPM = meter.get(meter.size() - 2).measurement;
//                        int position;
//                            float newBPM = l.get(l.size()-1);
//                            float prevBPM = l.get(l.size()-2);
//                        int countSize = l.size();
//
//                            float diff = newBPM - prevBPM;
//
//                            if(countSize / 200 ==0) {
//                                if (diff < 0) { //BPM decrease,calmer
//                                    position = 2; //move right
//                                    sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                    Log.d(TAG, "needle msg right");
//                                    Log.d(TAG, "needle msg diff: " + diff);
//                                } else if (diff > 5) { //BPM increase by more than 10bpm, excited
//                                    position = 3; //move left
//                                    sendMessage(BiofeedbackActivity.MESSAGE_METER, position);
//                                    Log.d(TAG, "needle msg left");
//                                    Log.d(TAG, "needle msg diff: " + diff);
//                                }
//                            }
//
//                    }); meterthread.start();

                    //meter
//                    Thread meterthread = new Thread(() -> {
//
//                        CopyOnWriteArrayList<Measurement<Float>> meter = storeBPM.getStdValues();
//                        for(Measurement<Float> dataPoint :meter){
//                            float newBPM = dataPoint.measurement;
//                            sendMessage(BiofeedbackActivity.MESSAGE_METER, newBPM);
//
//                        }
//
//                    }); meterthread.start();


                    // draw the chart on a separate thread.
//                    Thread chartDrawerThread = new Thread(() -> chartDrawer.draw(store.getStdValues()));
//                    chartDrawerThread.start();
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
                            MainActivity.MESSAGE_CAMERA_NOT_AVAILABLE,
                            "No valleys detected - there may be an issue when accessing the camera."));
                    return;
                }

//                String statusUpdate2 = "Measuring Complete";
//                sendMessage(MainActivity.MESSAGE_UPDATE_TITLE, statusUpdate2);

                String currentValue = String.format(
                        Locale.getDefault(),
                        activity.getResources().getQuantityString(R.plurals.measurement_output_template, detectedValleys - 1),
                        60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f)),
                        detectedValleys - 1,
                        1f * (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f);


                sendMessage(MainActivity.MESSAGE_UPDATE_REALTIME, currentValue);

                StringBuilder returnValueSb = new StringBuilder();
                returnValueSb.append(currentValue);
                returnValueSb.append(activity.getString(R.string.row_separator));

                //rachel
                float breatheRate = (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))) /4;

                String currentValue2 = String.format(
                        Locale.getDefault(),
                        activity.getResources().getQuantityString(R.plurals.breathRate_output_template, detectedValleys - 1),
                        (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f)))/4);

                sendMessage(MainActivity.MESSAGE_UPDATE_REALTIME, returnValueSb.toString());


                StringBuilder returnValueSb2 = new StringBuilder();
                returnValueSb2.append(currentValue2);
                returnValueSb2.append(activity.getString(R.string.row_separator));

                //added for measureTitle
                //((TextView)activity.findViewById(R.id.measureTitle)).setText("Measuring Complete");





                // look for "drops" of 0.15 - 0.75 in the value
                // a drop may take 2-3 ticks.
                // int dropCount = 0;
                // for (int stdValueIdx = 4; stdValueIdx < stdValues.size(); stdValueIdx++) {
                //     if (((stdValues.get(stdValueIdx - 2).measurement - stdValues.get(stdValueIdx).measurement) > dropHeight) &&
                //             !((stdValues.get(stdValueIdx - 3).measurement - stdValues.get(stdValueIdx - 1).measurement) > dropHeight) &&
                //            !((stdValues.get(stdValueIdx - 4).measurement - stdValues.get(stdValueIdx - 2).measurement) > dropHeight)
                //    ) {
                //        dropCount++;
                //    }
                // }

                // returnValueSb.append(activity.getString(R.string.detected_pulse));
                // returnValueSb.append(activity.getString(R.string.separator));
                // returnValueSb.append((float) dropCount / ((float) (measurementLength - clipLength) / 1000f / 60f));
                // returnValueSb.append(activity.getString(R.string.row_separator));
//// commented out timeline
//                returnValueSb.append(activity.getString(R.string.raw_values));
//                returnValueSb.append(activity.getString(R.string.row_separator));
//
//
//                for (int stdValueIdx = 0; stdValueIdx < stdValues.size(); stdValueIdx++) {
//                    // stdValues.forEach((value) -> { // would require API level 24 instead of 21.
//                    Measurement<Float> value = stdValues.get(stdValueIdx);
//                    String timeStampString =
//                            new SimpleDateFormat(
//                                    activity.getString(R.string.dateFormatGranular),
//                                    Locale.getDefault()
//                            ).format(value.timestamp);
//                    returnValueSb.append(timeStampString);
//                    returnValueSb.append(activity.getString(R.string.separator));
//                    returnValueSb.append(value.measurement);
//                    returnValueSb.append(activity.getString(R.string.row_separator));
//                }
//
//                returnValueSb.append(activity.getString(R.string.output_detected_peaks_header));
//                returnValueSb.append(activity.getString(R.string.row_separator));
//
//                // add detected valleys location
//                for (long tick : valleys) {
//                    returnValueSb.append(tick);
//                    returnValueSb.append(activity.getString(R.string.row_separator));
//                }

                sendMessage(MainActivity.MESSAGE_UPDATE_FINAL,currentValue2);
                //sendMessage(MainActivity.MESSAGE_UPDATE_FINAL, returnValueSb.toString());


                //rachel
                //sendMessage(MainActivity.MESSAGE_UPDATE_FINAL, returnValueSb2.toString());

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
