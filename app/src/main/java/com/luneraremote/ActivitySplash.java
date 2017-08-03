package com.luneraremote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by shvet on 15/07/2017,LuneraRemote
 */

public class ActivitySplash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        Log.e("ActivitySplash", "Activity");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, 1000);
    }
}
