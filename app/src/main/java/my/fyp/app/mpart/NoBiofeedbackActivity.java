package my.fyp.app.mpart;


import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;


public class NoBiofeedbackActivity extends AppCompatActivity implements FilterBottomSheetDialog.BottomSheetListener{
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_biofeedback);


        //in miliseconds 1000ms = 1s
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

        selectedTimer = 1000*304;  //initializing timer
        run=true;
        isRunning=false;

        bg1 = findViewById(R.id.circleAnimate);
        filter = findViewById(R.id.slidersIcon);
        back = findViewById(R.id.backIcon);
        guideTxt = findViewById(R.id.guideTxt);
        layout = findViewById(R.id.constraintLayout);


        f = 1f;


        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });


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

                    Intent in = new Intent(NoBiofeedbackActivity.this, CompletedActivity.class);


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
                    Log.d(TAG, "AV stop running");

                }
                else{
                    ////// breathing session starts //////

//                    filter.setVisibility(View.INVISIBLE); // hide filter
                    back.setVisibility(View.INVISIBLE); // hide back chevron

                    isRunning=true;
                    timer=true;
                    startButton.setText("Stop");
                    int extrahold =0;


                    cdMain = new CountDownTimer(selectedTimer,inhale+hold+exhale+extrahold) {

                        @Override
                        public void onTick(long l) {

                            cd = new CountDownTimer(inhale, inhale) {
                                public void onTick(long millisUntilFinished) {
                                    startButton.setText("Stop");

                                    guideTxt.setText("Inhale");
                                    f = 3f;
                                    //performAnimation(bg2, f, inhale, hold);
                                    performAnimation(bg1, f - 0.5f, inhale, hold);
                                    if (player == null){
                                        player = MediaPlayer.create(getApplicationContext(), R.raw.inhalesound);
                                    }
                                    player.start();
                                    Log.d(TAG, "AV inhale running");


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

                                            Log.d(TAG, "AV hold running");


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
                                                    Log.d(TAG, "AV exhale running");
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
                            Intent in = new Intent(NoBiofeedbackActivity.this, CompletedActivity.class);
                            stopPlayer();
                            startActivity(in);
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


    //        STOP sound during hold breath
    private void stopPlayer() {
        if (player != null){
            player.release();
            player = null;
        }
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
                                startActivity( new Intent(getApplicationContext(), NoBiofeedbackActivity.class));
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