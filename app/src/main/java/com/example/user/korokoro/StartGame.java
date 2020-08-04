package com.example.user.korokoro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;

public class StartGame extends Activity {
    int a, b;
    boolean checkTime;
    public static int score;

    CountDownTimer start;

    Queue<Integer> queue = new ArrayDeque<>();
    Random r = new Random();
    TextView tx3, textView, tx5;
    ImageView iv4, iv5, iv9, iv1;
    MediaPlayer mediaplayer, backs;

    ImageView iv[] = new ImageView[4];
    public static int[] drawables;
    public static int len;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startgame);

        Firebase.setAndroidContext(this);
        //score
        tx3 = (TextView) findViewById(R.id.textView3);
        tx3.setText("0");

        //highscore
        tx5 = (TextView) findViewById(R.id.textView5);
        tx5.setText(String.valueOf(MainActivity.highscore));

        //set background
        ConstraintLayout rl=(ConstraintLayout) findViewById(R.id.relativelayout1);
        int bg = r.nextInt(3) + 1;
        int res = getResources().getIdentifier("back" + bg, "drawable", getPackageName());
        rl.setBackgroundResource(res);

        //set base
        bg = r.nextInt(2)+1;
        res = getResources().getIdentifier("alas"+bg, "drawable", getPackageName());
        iv9 = (ImageView) findViewById(R.id.imageView9);
        iv9.setImageResource(res);

        //set character
        a = r.nextInt(len);
        b = r.nextInt(len);
        while(a == b){
            b = r.nextInt(len);
        }

        //stack first character
        int[] arrRand = {a,b};
        for (int i=1; i<=3; i++) {
            int q = r.nextInt(arrRand.length);
            queue.add(arrRand[q]);
            res = getResources().getIdentifier("image" + i, "id", getPackageName());
            iv[i] = (ImageView) findViewById(res);
            iv[i].setImageResource(drawables[arrRand[q]]);
        }

        //set gif
        iv1 = (ImageView) findViewById(R.id.imageView);
        iv1.setImageResource(R.drawable.nempel);

        //button
        iv4 = (ImageView) findViewById(R.id.imageView4);
        iv4.setImageResource(drawables[a]);
        iv5 = (ImageView) findViewById(R.id.imageView5);
        iv5.setImageResource(drawables[b]);

        final Intent ne = new Intent(this, Score.class);
        final Firebase fbScore = new Firebase("https://proyek-ppm.firebaseio.com/data/score");

        //timer
        checkTime = false;
        textView = (TextView) findViewById(R.id.textView);
        start = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf(millisUntilFinished / 1000 - 1));
            }

            public void onFinish() {
                //ke score
                textView.setText("FINISH!!");
                checkTime = true;
                iv4.setEnabled(false);
                iv5.setEnabled(false);

                fbScore.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int sc = dataSnapshot.getValue(int.class);
                        score = Integer.parseInt(String.valueOf(tx3.getText()));
                        if (sc < score) {
                            fbScore.setValue(score);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });

                CountDownTimer starts = new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        iv1.setImageResource(R.drawable.loading);
                        iv1.setVisibility(View.VISIBLE);
                    }
                    public void onFinish() {
                        startActivity(ne);
                        finish();
                    }
                }.start();
            }
        }.start();

        //music
        backs = backs.create(getApplicationContext(), R.raw.gudebacksound);
        backs.start();

        MainActivity.check=1;
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ||  keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
            backs.stop();
            start.cancel();
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
            finish();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }

    //update pic in queue
    public void update(){
        Queue<Integer> temp = new ArrayDeque<>();
        Iterator<Integer> it = queue.iterator();
        while(it.hasNext()){
            temp.add(it.next());
        }
        for(int i=1; i<=3; i++){
            int res = getResources().getIdentifier("image" + i, "id", getPackageName());
            iv[i] = (ImageView) findViewById(res);
            iv[i].setImageResource(drawables[temp.peek()]);
            if(i > 1){
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 300);
                animation.setDuration(50);
                animation.setRepeatCount(0);
                iv[i].startAnimation(animation);
            }
            temp.remove();
        }
        while(!temp.isEmpty()){
            temp.remove();
        }
    }

    // back button
    public void back(View view) {
        backs.stop();
        start.cancel();
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        finish();
    }


    // left button
    public void butKiri(View view) {
        mediaplayer = mediaplayer.create(getApplicationContext(), R.raw.effect1);
        if(a == queue.peek()){
            //set sound effect
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
             }
            mediaplayer.start();

            //set left animation
            TranslateAnimation animation = new TranslateAnimation(0, -1000, 0, 0);
            animation.setDuration(80);
            animation.setRepeatCount(0);
            iv[1].setImageResource(drawables[a]);
            iv[1].startAnimation(animation);

            queue.remove();

            int [] arrRand = {a,b};
            int q = r.nextInt(arrRand.length);
            queue.add(arrRand[q]);

            int as = Integer.parseInt(String.valueOf(tx3.getText()));
            as += 1;
            tx3.setText(String.valueOf(as));

            update();
        } else {
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }

            //set gif
            new CountDownTimer(2000, 1000){
                ImageView rl = (ImageView) findViewById(R.id.imageView);
                @Override
                public void onTick(long millisUntilFinished) {
                    rl.setVisibility(View.VISIBLE);
                    Glide.with(StartGame.this)
                            .load(R.drawable.nempel)
                            .asGif()
                            .placeholder(R.drawable.nempel)
                            .crossFade()
                            .into(rl);
                    iv4.setEnabled(false);
                    iv5.setEnabled(false);
                }

                public  void onFinish(){
                    rl.setVisibility(View.GONE);
                    if(!checkTime) {
                        iv4.setEnabled(true);
                        iv5.setEnabled(true);
                    }
                }
            }.start();

            //set vibrate
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }

    }

    // right button
    public void butKanan(View view) {
        mediaplayer = mediaplayer.create(getApplicationContext(), R.raw.effect2);
        if(b == queue.peek()){
            //set sound effect
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }
            mediaplayer.start();

            //set right animation
            TranslateAnimation animation = new TranslateAnimation(0, 1000, 0, 0);
            animation.setDuration(80);
            animation.setRepeatCount(0);
            iv[1].setImageResource(drawables[b]);
            iv[1].startAnimation(animation);

            queue.remove();

            int [] arrRand = {a,b};
            int q = r.nextInt(arrRand.length);
            queue.add(arrRand[q]);

            int as = Integer.parseInt(String.valueOf(tx3.getText()));
            as += 1;
            tx3.setText(String.valueOf(as));

            update();
        } else {
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }

            //set gif
            new CountDownTimer(2000, 1000){
                ImageView rl = (ImageView) findViewById(R.id.imageView);
                @Override
                public void onTick(long millisUntilFinished) {
                    rl.setVisibility(View.VISIBLE);
                    Glide.with(StartGame.this)
                            .load(R.drawable.nempel)
                            .asGif()
                            .placeholder(R.drawable.nempel)
                            .crossFade()
                            .into(rl);
                    rl.setVisibility(View.VISIBLE);
                    iv4.setEnabled(false);
                    iv5.setEnabled(false);
                }

                public  void onFinish(){
                    rl.setVisibility(View.GONE);
                    if(!checkTime) {
                        iv4.setEnabled(true);
                        iv5.setEnabled(true);
                    }
                }
            }.start();

            //set vibrate
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }
}
