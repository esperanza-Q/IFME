package com.example.ifme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateMainNodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String worldTitle = intent.getStringExtra("worldTitle");
        String choice1 = intent.getStringExtra("choice1");
        String choice2 = intent.getStringExtra("choice2");
        int periodValue = intent.getIntExtra("periodValue", 0);
        String periodUnit = intent.getStringExtra("periodUnit");

        TextView tv = new TextView(this);
        tv.setPadding(60, 100, 60, 60);
        tv.setText("전달받은 데이터 확인\n\n"
                + "• 세계 제목: " + worldTitle + "\n"
                + "• 선택지1: " + choice1 + "\n"
                + "• 선택지2: " + choice2 + "\n"
                + "• 예상 기간: " + periodValue + periodUnit);
        setContentView(tv);

        Toast.makeText(this, "메인 노드 작성 화면으로 이동했습니다.", Toast.LENGTH_SHORT).show();
    }
}