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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextView tvLogo = findViewById(R.id.tv_home_logo);
        TextPaint paint = tvLogo.getPaint();
        float width = paint.measureText(tvLogo.getText().toString());
        Shader textShader = new LinearGradient(0, 0, width, tvLogo.getTextSize(),
                new int[]{
                        Color.parseColor("#151E43"),
                        Color.parseColor("#2F4291")
                }, null, Shader.TileMode.CLAMP);
        tvLogo.getPaint().setShader(textShader);

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

        LinearLayout cardCreateWorld = findViewById(R.id.card_create_world);
        cardCreateWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateWorldActivity.class);
                startActivity(intent);
            }
        });
    }
}