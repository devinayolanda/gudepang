package com.example.user.korokoro;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static Integer highscore, check = 0;
    ImageView pla, scans, exit;
    boolean checkStart;
    Firebase fbScore, fa;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        pla = (ImageView) findViewById(R.id.imageView7);
        scans = (ImageView) findViewById(R.id.imageView2);
        exit = (ImageView) findViewById(R.id.imageView8);
        pla.setEnabled(true);
        scans.setEnabled(true);
        exit.setEnabled(true);

        fbScore = new Firebase("https://proyek-ppm.firebaseio.com/data/score");
        fa = new Firebase("https://proyek-ppm.firebaseio.com/data/gude");

        checkStart=true;
//        highscore=9999;

        //sound
        mediaplayer = mediaplayer.create(getApplicationContext(), R.raw.op);
        if(check == 0) mediaplayer.start();
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ||  keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }

    public void start(View view) {
        //character
        final String[] gudeChara = new String[1];
        fa.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                gudeChara[0] = dataSnapshot.getValue(String.class);
                String[] gudeResult = gudeChara[0].split(",");
                StartGame.len = gudeResult.length;
                StartGame.drawables = new int[gudeResult.length];
                for (int i=0; i<gudeResult.length; i++) {
                    String mDrawableName = "gude" + gudeResult[i];
                    int res = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
                    StartGame.drawables[i] = res;
                }
            }

            public void onCancelled(FirebaseError firebaseError) {}
        });

        //high score
//        if(highscore == 9999) {
//            pla.setEnabled(false);
//            scans.setEnabled(false);
//            exit.setEnabled(false);
//        }

        fbScore.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                highscore = dataSnapshot.getValue(int.class);

                pla.setEnabled(true);
                scans.setEnabled(true);
                exit.setEnabled(true);
                if(checkStart) ganti();
            }

            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void ganti(){
        if(mediaplayer.isPlaying()) mediaplayer.stop();
        Intent in = new Intent(this, StartGame.class);
        startActivity(in);
        checkStart = false;
        finish();
    }

    public void exit(View view) {
        if(mediaplayer.isPlaying()) mediaplayer.stop();
        finish();
    }

    public void scan(View view) {
        if(mediaplayer.isPlaying()) mediaplayer.stop();
        Intent in = new Intent(this, Scan.class);
        startActivity(in);
        checkStart = false;
        finish();
    }
}
