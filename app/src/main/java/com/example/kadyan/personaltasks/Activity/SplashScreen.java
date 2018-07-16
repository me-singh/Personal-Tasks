package com.example.kadyan.personaltasks.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kadyan.personaltasks.R;

public class SplashScreen extends AppCompatActivity {

    private static final Long SPLASH_DELAY = 500L;
    private Handler mDelayHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isFinishing()) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        mDelayHandler.postDelayed(runnable, SPLASH_DELAY);
    }

    private void initViews() {
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelayHandler.removeCallbacks(runnable);
    }
}
