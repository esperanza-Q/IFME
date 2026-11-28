package com.example.ifme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateWorldActivity extends AppCompatActivity {

    private EditText etWorldTitle;
    private EditText etChoice1;
    private EditText etChoice2;
    private EditText etPeriodValue;
    private Spinner spinnerPeriodUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_world);

        setupSystemBars();
        initViews();
        setupPeriodSpinner();
        setupListeners();
    }

    private void setupSystemBars() {
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        View rootLayout = findViewById(R.id.root_create_world);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void initViews() {
        etWorldTitle = findViewById(R.id.et_world_title);
        etChoice1 = findViewById(R.id.et_choice1);
        etChoice2 = findViewById(R.id.et_choice2);
        etPeriodValue = findViewById(R.id.et_period_value);
        spinnerPeriodUnit = findViewById(R.id.spinner_period_unit);
    }

    private void setupPeriodSpinner() {
        String[] units = {"단위", "일", "주", "개월", "년"};
        Typeface suiteRegular = ResourcesCompat.getFont(this, R.font.suite_regular);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                units
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextSize(13f);
                if (suiteRegular != null) {
                    tv.setTypeface(suiteRegular);
                }
                tv.setTextColor(position == 0 ? Color.parseColor("#9E9E9E") : Color.parseColor("#222222"));
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextSize(14f);
                tv.setPadding(36, 28, 36, 28);
                if (suiteRegular != null) {
                    tv.setTypeface(suiteRegular);
                }
                tv.setTextColor(position == 0 ? Color.parseColor("#9E9E9E") : Color.parseColor("#222222"));
                return tv;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriodUnit.setAdapter(adapter);
    }

    private void setupListeners() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        Button btnNextMainNode = findViewById(R.id.btn_next_main_node);
        btnNextMainNode.setOnClickListener(v -> validateAndMoveNext());

        LinearLayout navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(CreateWorldActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        LinearLayout navMy = findViewById(R.id.nav_my);
        navMy.setOnClickListener(v ->
                Toast.makeText(CreateWorldActivity.this, "마이 화면은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        );
    }

    private void validateAndMoveNext() {
        String worldTitle = etWorldTitle.getText().toString().trim();
        String choice1 = etChoice1.getText().toString().trim();
        String choice2 = etChoice2.getText().toString().trim();
        String periodValueStr = etPeriodValue.getText().toString().trim();
        int unitPosition = spinnerPeriodUnit.getSelectedItemPosition();

        if (worldTitle.isEmpty()) {
            Toast.makeText(this, "세계 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etWorldTitle.requestFocus();
            return;
        }

        if (choice1.isEmpty()) {
            Toast.makeText(this, "선택지1을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etChoice1.requestFocus();
            return;
        }

        if (choice2.isEmpty()) {
            Toast.makeText(this, "선택지2를 입력해주세요.", Toast.LENGTH_SHORT).show();
            etChoice2.requestFocus();
            return;
        }

        if (periodValueStr.isEmpty()) {
            Toast.makeText(this, "예상 기간 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
            etPeriodValue.requestFocus();
            return;
        }

        int periodValue = Integer.parseInt(periodValueStr);
        if (periodValue <= 0) {
            Toast.makeText(this, "예상 기간은 1 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            etPeriodValue.requestFocus();
            return;
        }

        if (unitPosition == 0) {
            Toast.makeText(this, "예상 기간 단위를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String periodUnit = spinnerPeriodUnit.getSelectedItem().toString();

        Intent intent = new Intent(CreateWorldActivity.this, CreateMainNodeActivity.class);
        intent.putExtra("worldTitle", worldTitle);
        intent.putExtra("choice1", choice1);
        intent.putExtra("choice2", choice2);
        intent.putExtra("periodValue", periodValue);
        intent.putExtra("periodUnit", periodUnit);
        startActivity(intent);
    }
}