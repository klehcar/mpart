package my.fyp.app.mpart;


        import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

        import android.Manifest;
        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.animation.PropertyValuesHolder;
        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.SurfaceTexture;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.os.Handler;
        import android.os.Looper;
        import android.os.Message;
        import android.os.VibrationEffect;
        import android.os.Vibrator;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.Surface;
        import android.view.TextureView;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.core.app.ActivityCompat;

        import com.github.florent37.viewanimator.AnimationListener;
        import com.github.florent37.viewanimator.ViewAnimator;
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




//    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//    private static final int REQUEST_CODE_VIBRATE = 111;
    //final long[] pattern = {0,1000};

    // #1 for Camera
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


                if(pos == 2){
                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("translationX", 80f);
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(needle, pvhX);
                    animator.setDuration(2000);
                    animator.start();
                    Log.d(TAG, "needle move right");

                }
                if(pos == 3){
                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("translationX", -2f);
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(needle, pvhX);
                    animator.setDuration(2000);
                    animator.start();
                    Log.d(TAG, "needle move left");

                }



//                String meterMSG = msg.obj.toString();
//                int newBPM = Integer.parseInt(meterMSG);
//                Log.d(TAG, "METER : " + newBPM);
//
//
//                if (newBPM >= 80) {
//                    ((ImageView) findViewById(R.id.triangle1)).setVisibility(View.VISIBLE);
//                    ((ImageView) findViewById(R.id.triangle2)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle3)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle4)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle5)).setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "POS 1" );
//                } else if (newBPM < 80 && newBPM >= 75) {
//                    ((ImageView) findViewById(R.id.triangle2)).setVisibility(View.VISIBLE);
//                    ((ImageView) findViewById(R.id.triangle1)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle3)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle4)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle5)).setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "POS 2" );
//                } else if (newBPM < 75 && newBPM >= 70) {
//                    ((ImageView) findViewById(R.id.triangle3)).setVisibility(View.VISIBLE);
//                    ((ImageView) findViewById(R.id.triangle2)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle1)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle4)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle5)).setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "POS 3" );
//                } else if (newBPM < 70 && newBPM >= 65) {
//                    ((ImageView) findViewById(R.id.triangle4)).setVisibility(View.VISIBLE);
//                    ((ImageView) findViewById(R.id.triangle2)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle3)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle1)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle5)).setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "POS 4" );
//                } else if(newBPM<65){
//                    ((ImageView) findViewById(R.id.triangle5)).setVisibility(View.VISIBLE);
//                    ((ImageView) findViewById(R.id.triangle2)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle3)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle4)).setVisibility(View.INVISIBLE);
//                    ((ImageView) findViewById(R.id.triangle1)).setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "POS 5" );
//                }
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

        //stopPlayer();
        //((TextView)findViewById(R.id.measureTitle)).setText("Measuring Complete");

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


        //rachel
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


    // #2 for Breathing Guide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biofeedback);


        ////////////////////////////////// #1 for camera
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);

//        ((TextView)findViewById(R.id.measureText)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.calm)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.meter)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.cover)).setVisibility(View.VISIBLE);


        ///////////////////////////// #2 for breathing guide
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.VIBRATE},
//                REQUEST_CODE_VIBRATE);



        //in miliseconds 1000ms = 1s
//        inhale=5000;
//        hold=0;
//        exhale=5000;
        inhale=4000;
        hold=7000;
        exhale=8000;

        if(getIntent().getStringExtra("inhale")!=null){
            inhale = Integer.parseInt(getIntent().getStringExtra("inhale"));
        }
        if(getIntent().getStringExtra("exhale")!=null){
            exhale =Integer.parseInt( getIntent().getStringExtra("exhale"));
        }
        if(getIntent().getStringExtra("hold")!=null){
            hold = Integer.parseInt(getIntent().getStringExtra("hold"));
        }

        selectedTimer = 1000*76;  //initializing timer with 1 minute 16 seconds : 4 cycles
        run=true;
        isRunning=false;

        bg1 = findViewById(R.id.circleAnimate);
        filter = findViewById(R.id.slidersIcon);
        back = findViewById(R.id.backIcon);
        imageView = findViewById(R.id.lotusImage); //not using
        guideTxt = findViewById(R.id.guideTxt);
        //countTxt = findViewById(R.id.countTxt);
        layout = findViewById(R.id.constraintLayout);


        f = 1f;

        startIntroAnimation(); // delete later



        startButton = findViewById(R.id.startButton);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAnimation();
//
//            }
//
//
//        });



//        ViewAnimator
//                .animate(imageView)
//                .translationY(-1000, 0)
//                .alpha(0,1)
//                //.andAnimate(text)
//                .dp().translationX(-20, 0)
//                .decelerate()
//                .duration(2000)
//                .thenAnimate(imageView)
//                .scale(1f, 0.5f, 1f)
//                .rotation(270)
//                .repeatCount(3) //starts from 0
//                .accelerate()
//                .duration(2000) //in miliseconds 1000ms = 1s
//                .start();

        // Botttom sheet dialog
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheetDialog d = new FilterBottomSheetDialog();
                d.show(getSupportFragmentManager(),"exampleBottomSheet");
            }
        });

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

                    Intent in = new Intent(BiofeedbackActivity.this, activity_completed.class);


                    vibrator.cancel();
                    startActivity(in);
                    if (player!=null){
                        player.stop();
                        player.reset();
                        player.release();
                        player=null;
                    }
//                    player.pause();
//                    player.reset();
//                    player.release();
//                    player = null;
                    Log.d(TAG, "stopPlayer");
                    finish();
                    //Animatoo.animateFade(BiofeedbackActivity.this);

                    cdMain.cancel();
                    timer=false;
                    isRunning=false;
                    cd.cancel();
                    Log.d(TAG, "MA stop running");

                }
                else{
                    ////// breathing session starts //////
                    ((TextView)findViewById(R.id.measureText)).setVisibility(View.INVISIBLE);
                    ((ImageView)findViewById(R.id.cover)).setVisibility(View.INVISIBLE);
                    ((TextView)findViewById(R.id.calm)).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.meter)).setVisibility(View.VISIBLE);

                    filter.setVisibility(View.INVISIBLE); // hide filter
                    back.setVisibility(View.INVISIBLE); // hide back chevron
                    //layout.setBackgroundColor(Color.parseColor("#F9F6F3"));

                    isRunning=true;
                    timer=true;
                    startButton.setText("STOP");
                    int extrahold =0;


                    cdMain = new CountDownTimer(selectedTimer,inhale+hold+exhale+extrahold) {

                        @Override
                        public void onTick(long l) {

                            cd = new CountDownTimer(inhale, inhale) {
                                public void onTick(long millisUntilFinished) {
                                    startButton.setText("STOP");

                                    guideTxt.setText("Inhale");
                                    f = 3f;
                                    //performAnimation(bg2, f, inhale, hold);
                                    performAnimation(bg1, f - 0.5f, inhale, hold);
                                    if (player == null){
                                        player = MediaPlayer.create(getApplicationContext(), R.raw.inhalesound);
                                    }
                                    player.start();
                                    Log.d(TAG, "MA inhale running");


                                    //vibration
                                    //Vibrate(10000);

//                                    vibrator.vibrate(1000);
                                }



                                public void onFinish() {
                                    timer = false;

                                    cd = new CountDownTimer(7000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            remainingTime = (int)(millisUntilFinished / 1000);

                                            if(remainingTime==7){
                                                guideTxt.setText("Hold");
                                            }
                                            else{
                                                guideTxt.setText(String.valueOf(remainingTime+1));

                                            }

                                            stopPlayer();
                                            //vibrator.cancel();
                                            Log.d(TAG, "MA hold running");


                                        }



                                        public void onFinish() {
                                            cd = new CountDownTimer(exhale, exhale) {
                                                public void onTick(long millisUntilFinished) {
                                                    f = 1f;
                                                    guideTxt.setText("Exhale");

                                                    //performAnimation(bg2, f, exhale, hold);
                                                    performAnimation(bg1, f, exhale, hold);
                                                    if (player == null){
                                                        player = MediaPlayer.create(getApplicationContext(), R.raw.exhalesound);
                                                    }
                                                    player.start();
                                                    Log.d(TAG, "MA: exhale running");
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

                        @Override
                        public void onFinish() {

                            try {
                                wait(1000);
                                stopPlayer();
                            }
                            catch (Exception e){}
                            Intent in = new Intent(BiofeedbackActivity.this,activity_completed.class);
                            stopPlayer();
                            startActivity(in);
                            //Animatoo.animateFade(BiofeedbackActivity.this);
                            finish();

                        }
                    }.start();

                }

            }





        });



        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheetDialog d = new FilterBottomSheetDialog();
                d.show(getSupportFragmentManager(),"exampleBottomSheet");
            }
        });





    }

    private void Vibrate(long millisecond){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            ((Vibrator)getSystemService(VIBRATOR_SERVICE))
                    .vibrate(VibrationEffect.createOneShot(millisecond,VibrationEffect.DEFAULT_AMPLITUDE));

        }
        else{
            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(millisecond);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_VIBRATE) {
//            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                Snackbar.make(
//                        findViewById(R.id.constraintLayout),
//                        getString(R.string.cameraPermissionRequired),
//                        Snackbar.LENGTH_LONG
//                ).show();
//            }
//        }
//    }



    private void startIntroAnimation(){
        ViewAnimator
                .animate(guideTxt)
                .scale(0, 1)
                .duration(1500)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        guideTxt.setText("Breathe");
                    }
                })
                .start();
    }

    private void startAnimation(){
        ViewAnimator
                .animate(imageView)
                .alpha(0, 1)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        guideTxt.setText("Inhale..Exhale");
                    }
                })
                .decelerate()
                .duration(1000)
                .thenAnimate(imageView)
                .scale(0.02f, 1.5f, 0.02f)
                .rotation(360)
                .repeatCount(5)
                .accelerate()
                .duration(5000)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        guideTxt.setText("Good Job");
                        imageView.setScaleX(1.0f);
                        imageView.setScaleY(1.0f);


                        new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                //put code to show ticking...1...2..3..

                            }

                            @Override
                            public void onFinish() {
                                startActivity( new Intent(getApplicationContext(), BiofeedbackActivity.class));
                                finish();

                            }
                        }.start();
                    }
                })
                .start();

    }

    void performAnimation(ImageView im, float f, int timer, int hold) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(im, "scaleX", f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(im, "scaleY", f);
        scaleDownX.setDuration(timer);
        scaleDownY.setDuration(timer);

        AnimatorSet scaleDown = new AnimatorSet();


        scaleDown.play(scaleDownX).with(scaleDownY);
        //scaleDown.setStartDelay(hold);
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