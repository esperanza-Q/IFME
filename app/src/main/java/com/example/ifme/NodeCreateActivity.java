package com.example.ifme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class NodeCreateActivity extends AppCompatActivity {

    private final Set<String> selectedValues = new LinkedHashSet<>();
    private final ArrayList<String> customValues = new ArrayList<>();
    private final ArrayList<TextView> valueButtons = new ArrayList<>();
    private int satisfaction = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_node_create);

        // 기존 TreeActivity가 전달하는 부모 노드 정보를 유지함.
        String parentTitle = getIntent().getStringExtra("parentNodeTitle");

        String parentId = getIntent().getStringExtra("parentNodeId");

        TextView parentView = findViewById(R.id.tvParentNodeTitle);
        // 단독 실행 테스트일 때만 Figma 예시 제목을 보여줌. 실제 연동 시 부모 노드 제목을 사용함.
        parentView.setText(parentTitle == null || parentTitle.trim().isEmpty()
                ? "취업하기" : parentTitle);

        // 시스템 상태바와 내비게이션 영역에 UI가 가려지지 않도록 처리함.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        EditText etTitle = findViewById(R.id.etNodeTitle);
        EditText etExpected = findViewById(R.id.etExpected);
        EditText etConcern = findViewById(R.id.etConcern);
        EditText etCustomValue = findViewById(R.id.etCustomValue);
        TextView tvCustomValues = findViewById(R.id.tvCustomValues);
        GridLayout grid = findViewById(R.id.gridValues);
        SatisfactionSliderView satisfactionSlider = findViewById(R.id.satisfactionSlider);
        View customValueArea = findViewById(R.id.customValueArea);

        // 디자인의 가치 선택 버튼을 2행 5열로 생성함.
        String[] choices = {"안정", "성장", "전문성", "도전", "인정",
                "자유", "관계", "여유", "의미", "기타"};
        for (String value : choices) {
            TextView chip = new TextView(this);
            chip.setText(value);
            chip.setTextSize(14);
            chip.setTextColor(Color.WHITE);
            chip.setGravity(Gravity.CENTER);
            chip.setBackground(chipBackground(false));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = dp(37);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dp(3), dp(2), dp(3), dp(2));
            grid.addView(chip, params);
            valueButtons.add(chip);
            chip.setOnClickListener(v -> {
                if (!selectedValues.add(value)) selectedValues.remove(value);
                boolean selected = selectedValues.contains(value);
                chip.setBackground(chipBackground(selected));

                if ("기타".equals(value)) {
                    customValueArea.setVisibility(
                            selected ? View.VISIBLE : View.GONE
                    );

                    if (selected) {
                        etCustomValue.requestFocus();

                        etCustomValue.post(() -> {
                            InputMethodManager imm =
                                    (InputMethodManager) getSystemService(
                                            Context.INPUT_METHOD_SERVICE
                                    );

                            if (imm != null) {
                                imm.showSoftInput(
                                        etCustomValue,
                                        InputMethodManager.SHOW_IMPLICIT
                                );
                            }
                        });
                    } else {
                        etCustomValue.clearFocus();
                    }
                }

            });
        }

        // 직접 입력한 가치는 중복 없이 목록에 추가함.
        findViewById(R.id.btnAddValue).setOnClickListener(v -> {
            String value = etCustomValue.getText().toString().trim();
            if (value.isEmpty()) {
                etCustomValue.setError("가치를 입력하세요");
                return;
            }
            if (!customValues.contains(value)) customValues.add(value);
            tvCustomValues.setVisibility(View.VISIBLE);
            tvCustomValues.setText("추가한 가치: " + android.text.TextUtils.join(", ", customValues));
            etCustomValue.setText("");
        });

        // Figma 형태의 슬라이더: 원형 핸들과 Lv. 숫자가 함께 이동함.
        satisfactionSlider.setLevel(7);
        satisfactionSlider.setOnLevelChangeListener(level -> satisfaction = level);

        // 입력 결과는 Activity 결과로 반환함. TreeActivity 연동은 다음 단계에서 진행함.
        findViewById(R.id.btnComplete).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                etTitle.setError("노드 이름을 입력하세요");
                etTitle.requestFocus();
                return;
            }
            ArrayList<String> allValues = new ArrayList<>(selectedValues);
            for (String custom : customValues) {
                if (!allValues.contains(custom)) allValues.add(custom);
            }
            Intent result = new Intent();
            result.putExtra("parentNodeId", parentId);
            result.putExtra("nodeTitle", title);
            result.putStringArrayListExtra("nodeValues", allValues);
            result.putExtra("nodeExpected", etExpected.getText().toString().trim());
            result.putExtra("nodeConcern", etConcern.getText().toString().trim());
            result.putExtra("nodeSatisfaction", satisfaction);
            setResult(RESULT_OK, result);
            Toast.makeText(this, "노드 정보 입력 완료", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private GradientDrawable chipBackground(boolean selected) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(selected ? "#5D7AFA" : "#05101D"));
        shape.setCornerRadius(dp(50));
        shape.setStroke(dp(1), Color.parseColor("#5D7AFA"));
        return shape;
    }

    private int dp(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
