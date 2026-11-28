package com.example.ifme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 상단 상태바를 흰색 배경 + 어두운 아이콘으로 맞춤
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // 1. 상단 'If : Me' 로고 그라데이션 (#151E43 -> #2F4291) 적용
        TextView tvLogo = findViewById(R.id.tv_home_logo);
        TextPaint paint = tvLogo.getPaint();
        float width = paint.measureText(tvLogo.getText().toString());
        Shader textShader = new LinearGradient(0, 0, width, tvLogo.getTextSize(),
                new int[]{
                        Color.parseColor("#151E43"),
                        Color.parseColor("#2F4291")
                }, null, Shader.TileMode.CLAMP);
        tvLogo.getPaint().setShader(textShader);

        // 2. 메인 질문 중 '갈림길' 부분만 포인트 컬러(#3652C0) 적용
        TextView tvHeadline = findViewById(R.id.tv_home_headline);
        String headlineText = "오늘은 어떤\n갈림길에 서 있나요?";
        SpannableString headlineSpan = new SpannableString(headlineText);
        int start = headlineText.indexOf("갈림길");
        headlineSpan.setSpan(
                new ForegroundColorSpan(Color.parseColor("#3652C0")),
                start,
                start + "갈림길".length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tvHeadline.setText(headlineSpan);

        // 3. 서브 설명 문구 중 'If:Me' 부분 Bold 적용
        TextView tvDesc = findViewById(R.id.tv_create_desc);
        String descText = "고민 중인 두 가지 선택지로\nIf:Me와 평행세계를 시작해 보세요!";
        SpannableString descSpan = new SpannableString(descText);
        int descStart = descText.indexOf("If:Me");
        descSpan.setSpan(
                new StyleSpan(Typeface.BOLD),
                descStart,
                descStart + "If:Me".length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tvDesc.setText(descSpan);

        // 4. '새 세계 만들기' 카드 클릭 이벤트
        LinearLayout cardCreateWorld = findViewById(R.id.card_create_world);
        cardCreateWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateWorldActivity.class);
                startActivity(intent);
            }
        });

        FooterHelper.setup(this, FooterHelper.TAB_HOME);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button compareBtn = (Button) findViewById(R.id.compareBtn);
        compareBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
                startActivity(intent);
            }
        });

    }


}