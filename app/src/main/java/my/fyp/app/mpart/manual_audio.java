package my.fyp.app.mpart;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;


public class manual_audio extends AppCompatActivity implements FilterBottomSheetDialog.BottomSheetListener{
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

    ConstraintLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_audio);

        //in miliseconds 1000ms = 1s
        inhale=5000;
        hold=0;
        exhale=5000;

        if(getIntent().getStringExtra("inhale")!=null){
            inhale = Integer.parseInt(getIntent().getStringExtra("inhale"));
        }
        if(getIntent().getStringExtra("exhale")!=null){
            exhale =Integer.parseInt( getIntent().getStringExtra("exhale"));
        }
        if(getIntent().getStringExtra("hold")!=null){
            hold = Integer.parseInt(getIntent().getStringExtra("hold"));
        }

        selectedTimer = 1000*60;  //initiliazing timer with 1 minute
        run=true;
        isRunning=false;

        bg1 = findViewById(R.id.circleAnimate);
        filter = findViewById(R.id.slidersIcon);
        back = findViewById(R.id.backIcon);
        imageView = findViewById(R.id.lotusImage); //not using
        guideTxt = findViewById(R.id.guideTxt);
        layout = findViewById(R.id.constraintLayout);


        f = 1f;

        startIntroAnimation(); // delete later



        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });



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
                    Intent in = new Intent(manual_audio.this, activity_completed.class);
                    startActivity(in);
                    finish();
                    //Animatoo.animateFade(manual_audio.this);

                    cdMain.cancel();
                    timer=false;
                    isRunning=false;
//                    cd.cancel();
                }
                else{
                    ////// breathing session starts //////

                    filter.setVisibility(View.INVISIBLE); // hide filter
                    back.setVisibility(View.INVISIBLE); // hide back chevron
                    layout.setBackgroundColor(Color.parseColor("#F9F6F3"));

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
                                }



                                public void onFinish() {
                                    timer = false;

                                    cd = new CountDownTimer(hold, hold) {
                                        public void onTick(long millisUntilFinished) {
                                            guideTxt.setText("Hold");
                                        }



                                        public void onFinish() {
                                            cd = new CountDownTimer(exhale, exhale) {
                                                public void onTick(long millisUntilFinished) {
                                                    f = 1f;
                                                    guideTxt.setText("Exhale");

                                                    //performAnimation(bg2, f, exhale, hold);
                                                    performAnimation(bg1, f, exhale, hold);
                                                }



                                                public void onFinish() {

                                                    timer = false;
                                                }
                                            }.start();
                                        }
                                    }.start();



                                }
                            }.start();
                        }

                        @Override
                        public void onFinish() {

                            try {
                                wait(1000);
                            }
                            catch (Exception e){}
                            Intent in = new Intent(manual_audio.this,activity_completed.class);
                            startActivity(in);
                            //Animatoo.animateFade(MainActivity.this);
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
                                startActivity( new Intent(getApplicationContext(), manual_audio.class));
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