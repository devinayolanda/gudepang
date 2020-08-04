package com.example.user.korokoro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Score extends Activity{
    TextView tx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.score);

        tx = (TextView) findViewById(R.id.textView7);
        tx.setText(String.valueOf(StartGame.score));
    }

    public void back(View view) {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        finish();
    }
}
