package com.example.ifme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MypageActivity extends AppCompatActivity  {

    private ProfileStorage profileStorage;
    private TextView mypageProfileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mypage);
        FooterHelper.setup(this, FooterHelper.TAB_MYPAGE);

        // 하단 내비게이션 바의 회색 막 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        // 상태바 아이콘을 검은색으로
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // 화면 내용이 상태바, 하단 내비게이션 바와 겹치지 않게 여백 주기
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mypageRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ImageButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnReport = (Button) findViewById(R.id.mypageBtnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        profileStorage = new ProfileStorage(this);
        mypageProfileContent = findViewById(R.id.mypageProfileContent);

        ImageButton btnEdit = findViewById(R.id.mypageBtnEdit);
        btnEdit.setOnClickListener(v -> showEditDialog());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        String name = profileStorage.getName();
        String email = profileStorage.getEmail();

        mypageProfileContent.setText(
                "이름  |  " + (name.isEmpty() ? "-" : name) + "\n" +
                        "이메일  |  " + (email.isEmpty() ? "-" : email) + "\n" +
                        "기록 히스토리  |  " + profileStorage.getStartDate() + " ~");
    }

    private void showEditDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile_edit);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etName = dialog.findViewById(R.id.dialogEditName);
        EditText etEmail = dialog.findViewById(R.id.dialogEditEmail);
        Button btnSave = dialog.findViewById(R.id.dialogBtnSave);

        etName.setText(profileStorage.getName());
        etEmail.setText(profileStorage.getEmail());

        btnSave.setOnClickListener(v -> {
            profileStorage.saveProfile(
                    etName.getText().toString().trim(),
                    etEmail.getText().toString().trim());
            loadProfile();
            dialog.dismiss();
        });

        dialog.show();
    }
}