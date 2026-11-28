package com.example.ifme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isMoved = false;

    private final Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            moveToHome();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // 상단 상태바 색상을 랜딩 상단 보라색과 통일
        getWindow().setStatusBarColor(Color.parseColor("#4460D1"));

        // 4초(4000ms) 후 자동으로 홈 화면 이동 (1000 = 1초)
        handler.postDelayed(moveRunnable, 4000);

        // 화면 터치 시 즉시 홈 화면 이동
        View rootView = findViewById(R.id.landing_root);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome();
            }
        });
    }

    private void moveToHome() {
        if (isMoved) return;
        isMoved = true;

        handler.removeCallbacks(moveRunnable);
        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(moveRunnable);
    }
}