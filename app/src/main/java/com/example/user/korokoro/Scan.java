package com.example.user.korokoro;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Scan extends Activity{
    ImageView chara;
    int results;
    boolean scan = false;
    Firebase fb;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.scan);

        IntentIntegrator it = new IntentIntegrator(this);
        it.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        it.setBeepEnabled(true);
        it.setPrompt("");
        it.initiateScan();
        Firebase.setAndroidContext(this);

        MainActivity.check = 1;
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ||  keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
            finish();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                finish();
            } else {
                try {
                    results = Integer.parseInt(result.getContents());
                    results = results % 11 + 1;
                    String mDrawableName = "gude" + results;
                    int res = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
                    chara = (ImageView) findViewById(R.id.imageView6);
                    //chara.setImageResource(drawables[results]);
                    chara.setImageResource(res);

                    //sound effect
                    mediaplayer = mediaplayer.create(getApplicationContext(), R.raw.scan);
                    mediaplayer.start();

                    onStar();
                }catch(Exception e) {
                    Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onStar(){
        final String[] gudeChara = new String[1];
        fb = new Firebase("https://proyek-ppm.firebaseio.com/data/gude");
        fb.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!scan){
                    gudeChara[0] = dataSnapshot.getValue(String.class);
                    String[] gudeResult = gudeChara[0].split(",");
                    boolean exists = false;
                    for (int i=0; i<gudeResult.length; i++){
                        if(gudeResult[i].equalsIgnoreCase(String.valueOf(results))){
                            exists = true;
                            break;
                        }

                    }
                    if (!exists){
                        String last = "";
                        last = TextUtils.join(",", gudeResult);
                        last = last + "," + results;
                        fb.setValue(last);
                    }
                    scan = true;
                }
            }

            //@Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void back(View view) {
        mediaplayer.stop();
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        finish();
    }
}
