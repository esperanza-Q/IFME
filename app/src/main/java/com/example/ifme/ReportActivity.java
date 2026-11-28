package com.example.ifme;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        FooterHelper.setup(this, FooterHelper.TAB_MYPAGE);

        // 하단 내비게이션 바의 회색 막 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        // 상태바 아이콘을 검은색으로
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // 화면 내용이 상태바, 하단 내비게이션 바와 겹치지 않게 여백 주기
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportRoot), (v, insets) -> {
//            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
//            return insets;
//        });
    }
}