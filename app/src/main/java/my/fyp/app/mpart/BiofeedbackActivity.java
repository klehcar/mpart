package my.fyp.app.mpart;


        import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

        import android.Manifest;
        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.SurfaceTexture;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.os.Handler;
        import android.os.Looper;
        import android.os.Message;
        import android.os.Vibrator;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.Surface;
        import android.view.TextureView;
        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.AnimationUtils;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.core.app.ActivityCompat;

        import com.google.android.material.snackbar.Snackbar;

        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Locale;


public class BiofeedbackActivity extends AppCompatActivity implements FilterBottomSheetDialog.BottomSheetListener{
    private ImageView imageView;
    private TextView guideTxt;
    private Button startButton;
    private ImageView filter;
    private ImageView back;
    private ImageView bg1;

    private int inhale;
    private int exhale;
    private int hold;
    private boolean timer;

    private boolean run;

    private boolean isRunning;
    private float f;

    private int selectedTimer;
    private CountDownTimer cd;
    private CountDownTimer cdMain;
    private int remainingTime;

    ConstraintLayout layout;

    MediaPlayer player;
    Vibrator vibrator;

    //for camera
    private BioAnalyzer analyzer;

    private final int REQUEST_CODE_CAMERA = 0;
    public static final int MESSAGE_UPDATE_REALTIME = 1;
    public static final int MESSAGE_UPDATE_FINAL = 2;
    public static final int MESSAGE_CAMERA_NOT_AVAILABLE = 3;
    public static final int MESSAGE_METER = 4;

    private static final int MENU_INDEX_NEW_MEASUREMENT = 0;
    private static final int MENU_INDEX_EXPORT_RESULT = 1;
    private static final int MENU_INDEX_EXPORT_DETAILS = 2;

    private boolean justShared = false;

    ImageView needle;


    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            super.handleMessage(msg);

            if (msg.what ==  MESSAGE_UPDATE_REALTIME) {
                ((TextView) findViewById(R.id.measureText)).setText(msg.obj.toString());
            }

            if (msg.what == MESSAGE_UPDATE_FINAL) {
                ((TextView) findViewById(R.id.measureText3)).setText(msg.obj.toString());
            }

            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString());

                ((TextView) findViewById(R.id.measureText)).setText(
                        R.string.camera_not_found
                );
                analyzer.stop();
            }

            if (msg.what ==  MESSAGE_METER) {
                String meterMSG = msg.obj.toString();
                int pos = Integer.parseInt(meterMSG);
                needle = findViewById(R.id.needle);

                if(pos==1){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left1");
                }
                if(pos==2){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide2);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right2");
                }
                if(pos==3){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide3);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left3");
                }
                if(pos==4){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide4);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right4");
                }
                if(pos==5){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide5);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left5");
                }
                if(pos==6){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide6);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right6");
                }
                if(pos==7){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide7);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left7");
                }
                if(pos==8){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide8);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right8");
                }
                if(pos==9){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide9);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left9");
                }
                if(pos==10){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide10);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 10");
                }
                if(pos==11){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide11);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left 11");
                }
                if(pos==12){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide12);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 12");
                }
                if(pos==13){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide13);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left 13");
                }
                if(pos==14){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide14);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 14");
                }
                if(pos==15){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide15);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left 15");
                }
                if(pos==16){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide16);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 16");
                }
                if(pos==17){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide17);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left 17");
                }
                if(pos==18){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide18);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 18");
                }
                if(pos==19){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide19);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"left 19");
                }
                if(pos==20){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide20);
                    needle.startAnimation(animSlide);
                    Log.d(TAG,"right 20");
                }
                Log.d(TAG, "needle message executed");
            }
        }

    };

    private final CameraService cameraService = new CameraService(this, mainHandler);

    @Override
    protected void onResume() {
        super.onResume();

        analyzer = new BioAnalyzer(this, findViewById(R.id.graphTextureViewB), mainHandler);

        TextureView cameraTextureView = findViewById(R.id.textureViewB);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        // justShared is set if one clicks the share button.
        if ((previewSurfaceTexture != null) && !justShared) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);

            // show warning when there is no flash
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.noFlashWarning),
                        Snackbar.LENGTH_LONG
                ).show();
            }

            // hide the new measurement item while another one is in progress in order to wait
            // for the previous one to finish
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraService.stop();

        if (analyzer != null) analyzer.stop();
        analyzer = new BioAnalyzer(this, findViewById(R.id.graphTextureViewB), mainHandler);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.cameraPermissionRequired),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("MENU", "menu is being prepared");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void onClickNewMeasurement(MenuItem item) {
        analyzer = new BioAnalyzer(this, findViewById(R.id.graphTextureViewB), mainHandler);

        // clear prior results
        char[] empty = new char[0];
        ((TextView) findViewById(R.id.measureText3)).setText(empty, 0, 0);
        ((TextView) findViewById(R.id.measureText)).setText(empty, 0, 0);

        // hide the new measurement item while another one is in progress in order to wait
        // for the previous one to finish
        // Exporting results cannot be done, either, as it would read from the already cleared UI.

        ((Button) findViewById(R.id.menuButton)).setVisibility(View.INVISIBLE);

        TextureView cameraTextureView = findViewById(R.id.textureViewB);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        if (previewSurfaceTexture != null) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);


        }
    }

    public void onClickExportResult(MenuItem item) {
        final Intent intent = getTextIntent((String) ((TextView) findViewById(R.id.measureText)).getText());
        justShared = true;
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));
    }

    public void onClickExportDetails(MenuItem item) {
        final Intent intent = getTextIntent(((TextView) findViewById(R.id.measureText3)).getText().toString());
        justShared = true;
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));
    }

    private Intent getTextIntent(String intentText) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                String.format(
                        getString(R.string.output_header_template),
                        new SimpleDateFormat(
                                getString(R.string.dateFormat),
                                Locale.getDefault()
                        ).format(new Date())
                ));
        intent.putExtra(Intent.EXTRA_TEXT, intentText);
        return intent;
    }

    private void stopPlayer() {
        if (player != null){
            player.release();
            player = null;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biofeedback);


        //////////////////////////////////  for camera
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);


        ((ImageView)findViewById(R.id.cover)).setVisibility(View.VISIBLE);


        ///////////////////////////// for breathing guide
        inhale=4000;
        hold=7000;
        exhale=8000;


        selectedTimer = 1000*304;  //initializing timer with 304 seconds
        run=true;
        isRunning=false;

        bg1 = findViewById(R.id.circleAnimate);
        filter = findViewById(R.id.slidersIcon);
        back = findViewById(R.id.backIcon);
        guideTxt = findViewById(R.id.guideTxt);
        //countTxt = findViewById(R.id.countTxt);
        layout = findViewById(R.id.constraintLayout);

        f = 1f;

        startButton = findViewById(R.id.startButton);


        // back to menu
        back.setOnClickListener(v -> {
            Intent intent = new Intent (this, MainMenu.class);
            startActivity(intent);
        });

        if(inhale==0){inhale=4000;}
        if(exhale==0){exhale=4000;}
        if(hold==0){hold=2000;}
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning) {
                    // STOP breathing session forcefully ////

                    Intent in = new Intent(BiofeedbackActivity.this, CompletedActivity.class);

                    startActivity(in);
                    if (player!=null){
                        player.stop();
                        player.reset();
                        player.release();
                        player=null;
                    }

                    Log.d(TAG, "stopPlayer");
                    finish();

                    cdMain.cancel();
                    timer=false;
                    isRunning=false;
                    cd.cancel();
                    Log.d(TAG, "AVB stop running");

                }
                else{
                    ////// breathing session starts //////
                    ((TextView)findViewById(R.id.measureText)).setVisibility(View.INVISIBLE);
                    ((ImageView)findViewById(R.id.cover)).setVisibility(View.INVISIBLE);
                    back.setVisibility(View.INVISIBLE); // hide back chevron

                    isRunning=true;
                    timer=true;
                    startButton.setText("Stop");
                    int extrahold =0;


                    cdMain = new CountDownTimer(selectedTimer,inhale+hold+exhale+extrahold) {

                        @Override
                        public void onTick(long l) {
                            runOnUiThread(new Runnable() {
                            public void run() {

                            cd = new CountDownTimer(inhale, inhale) {
                                public void onTick(long millisUntilFinished) {
                                    startButton.setText("Stop");

                                    guideTxt.setText("Inhale");
                                    f = 3f;
                                    performAnimation(bg1, f - 0.5f, inhale, hold);
                                    if (player == null){
                                        player = MediaPlayer.create(getApplicationContext(), R.raw.inhalesound);
                                    }
                                    player.start();
                                    Log.d(TAG, "AVB inhale running");

                                }



                                public void onFinish() {
                                    timer = false;
                                    stopPlayer();

                                    cd = new CountDownTimer(7000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            remainingTime = (int)(millisUntilFinished / 1000);

                                            if(remainingTime==7){
                                                guideTxt.setText("Hold");
                                            }
                                            else{
                                                guideTxt.setText(String.valueOf(remainingTime+1));

                                            }

                                            if (player == null){
                                                player = MediaPlayer.create(getApplicationContext(), R.raw.clockticking);
                                            }
                                            player.start();
                                            Log.d(TAG, "AVB hold running");


                                        }



                                        public void onFinish() {
                                            stopPlayer();

                                            cd = new CountDownTimer(exhale, exhale) {
                                                public void onTick(long millisUntilFinished) {
                                                    f = 1f;
                                                    guideTxt.setText("Exhale");

                                                    performAnimation(bg1, f, exhale, hold);
                                                    if (player == null){
                                                        player = MediaPlayer.create(getApplicationContext(), R.raw.exhalesound);
                                                    }
                                                    player.start();
                                                    Log.d(TAG, "AVB exhale running");
                                                }


                                                public void onFinish() {

                                                    timer = false;
                                                }
                                            }.start();
                                        }

                                    }.start();



                                }
                            }.start();
                            stopPlayer();
                            }
                        });
                        } //onTick ends

                        @Override
                        public void onFinish() {

                            try {
                                wait(1000);
                                stopPlayer();
                            }
                            catch (Exception e){}
                            Intent in = new Intent(BiofeedbackActivity.this, CompletedActivity.class);
                            stopPlayer();
                            startActivity(in);
                            finish();

                        }
                    }.start();

                }

            }

        });

    }


    void performAnimation(ImageView im, float f, int timer, int hold) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(im, "scaleX", f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(im, "scaleY", f);
        scaleDownX.setDuration(timer);
        scaleDownY.setDuration(timer);

        AnimatorSet scaleDown = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

    }

    @Override
    public void onButtonClicked(int inhale, int exhale) {
        this.inhale = inhale;
        this.exhale = exhale;
        this.hold = hold;

    }

    @Override
    public void onInhaleProgressChanged(int inhale) {
        if (inhale!=0){
            this.inhale=inhale;
        }

    }

    @Override
    public void onExhaleProgressChanged(int exhale) {
        if (exhale!=0){
            this.exhale=exhale;
        }

    }


}