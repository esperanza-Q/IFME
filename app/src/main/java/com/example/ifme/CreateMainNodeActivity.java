package com.example.ifme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CreateMainNodeActivity extends AppCompatActivity {

    private static final int MAX_VALUE_SELECTION = 3;

    private String choice1;
    private String choice2;

    private int currentStep = 1;

    // UI Views
    private ScrollView scrollMainNode;
    private View progressStep3;
    private TextView tvChoiceHeader;
    private LinearLayout layoutCustomChipsContainer;
    private EditText etCustomValue;
    private EditText etExpect;
    private EditText etWorry;
    private EditText etGiveUp;
    private SeekBar seekBarSatisfaction;
    private TextView tvSatisfactionLevel;
    private Button btnMainNodeNext;

    private final List<TextView> defaultChipViews = new ArrayList<>();
    private final Set<String> selectedValues = new LinkedHashSet<>();
    private final List<String> customValuesList = new ArrayList<>();

    // Step 1 & Step 2 화면 전환 시 입력값 유지용 임시 데이터
    private final List<String> savedValues1 = new ArrayList<>();
    private final List<String> savedCustom1 = new ArrayList<>();
    private String savedExpect1 = "";
    private String savedWorry1 = "";
    private String savedGiveUp1 = "";
    private int savedSatisfaction1 = 7;

    private final List<String> savedValues2 = new ArrayList<>();
    private final List<String> savedCustom2 = new ArrayList<>();
    private String savedExpect2 = "";
    private String savedWorry2 = "";
    private String savedGiveUp2 = "";
    private int savedSatisfaction2 = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_main_node);

        readIntentData();
        setupSystemBars();
        initViews();
        setupChips();
        setupSeekBar();
        setupListeners();
        renderStep(1, false);
    }

    private void readIntentData() {
        Intent intent = getIntent();
        choice1 = intent.getStringExtra("choice1");
        choice2 = intent.getStringExtra("choice2");

        if (choice1 == null || choice1.isEmpty()) choice1 = "선택지1";
        if (choice2 == null || choice2.isEmpty()) choice2 = "선택지2";
    }

    private void setupSystemBars() {
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        View rootLayout = findViewById(R.id.root_create_main_node);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void initViews() {
        scrollMainNode = findViewById(R.id.scroll_main_node);
        progressStep3 = findViewById(R.id.progress_step_3);
        tvChoiceHeader = findViewById(R.id.tv_choice_header);
        layoutCustomChipsContainer = findViewById(R.id.layout_custom_chips_container);
        etCustomValue = findViewById(R.id.et_custom_value);
        etExpect = findViewById(R.id.et_expect);
        etWorry = findViewById(R.id.et_worry);
        etGiveUp = findViewById(R.id.et_give_up);
        seekBarSatisfaction = findViewById(R.id.seekbar_satisfaction);
        tvSatisfactionLevel = findViewById(R.id.tv_satisfaction_level);
        btnMainNodeNext = findViewById(R.id.btn_main_node_next);

        int[] chipIds = {
                R.id.chip_stability, R.id.chip_growth, R.id.chip_expertise,
                R.id.chip_challenge, R.id.chip_recognition, R.id.chip_freedom,
                R.id.chip_relationship, R.id.chip_leisure, R.id.chip_meaning, R.id.chip_etc
        };
        for (int id : chipIds) {
            defaultChipViews.add(findViewById(id));
        }
    }

    private void setupChips() {
        for (TextView chip : defaultChipViews) {
            chip.setOnClickListener(v -> toggleValueSelection(chip.getText().toString()));
        }

        Button btnAddValue = findViewById(R.id.btn_add_value);
        btnAddValue.setOnClickListener(v -> addCustomValue());
    }

    private void toggleValueSelection(String value) {
        if (selectedValues.contains(value)) {
            selectedValues.remove(value);
        } else {
            if (selectedValues.size() >= MAX_VALUE_SELECTION) {
                Toast.makeText(this, "가치는 최대 3개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedValues.add(value);
        }
        refreshChipSelections();
    }

    private void addCustomValue() {
        String newValue = etCustomValue.getText().toString().trim();
        if (newValue.isEmpty()) {
            Toast.makeText(this, "추가할 가치를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (TextView defaultChip : defaultChipViews) {
            if (defaultChip.getText().toString().equals(newValue)) {
                etCustomValue.setText("");
                if (!selectedValues.contains(newValue)) {
                    toggleValueSelection(newValue);
                } else {
                    Toast.makeText(this, "이미 선택된 가치입니다.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        if (customValuesList.contains(newValue)) {
            etCustomValue.setText("");
            if (!selectedValues.contains(newValue)) {
                toggleValueSelection(newValue);
            } else {
                Toast.makeText(this, "이미 존재하는 가치입니다.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        customValuesList.add(newValue);
        etCustomValue.setText("");

        if (selectedValues.size() < MAX_VALUE_SELECTION) {
            selectedValues.add(newValue);
        } else {
            Toast.makeText(this, "가치가 추가되었습니다. (최대 3개 선택 가능)", Toast.LENGTH_SHORT).show();
        }

        rebuildCustomChipsUI();
        refreshChipSelections();
    }

    private void rebuildCustomChipsUI() {
        layoutCustomChipsContainer.removeAllViews();
        if (customValuesList.isEmpty()) {
            layoutCustomChipsContainer.setVisibility(View.GONE);
            return;
        }

        layoutCustomChipsContainer.setVisibility(View.VISIBLE);
        Typeface suiteRegular = ResourcesCompat.getFont(this, R.font.suite_regular);
        int marginTopPx = dpToPx(8);
        int marginEndPx = dpToPx(6);
        int chipHeightPx = dpToPx(35);
        int padHorizontalPx = dpToPx(16);

        LinearLayout currentRow = null;
        for (int i = 0; i < customValuesList.size(); i++) {
            if (i % 4 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rowParams.topMargin = marginTopPx;
                currentRow.setLayoutParams(rowParams);
                layoutCustomChipsContainer.addView(currentRow);
            }

            String val = customValuesList.get(i);
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    chipHeightPx
            );
            chipParams.setMarginEnd(marginEndPx);            chip.setLayoutParams(chipParams);
            chip.setMinimumWidth(dpToPx(59));
            chip.setPadding(padHorizontalPx, 0, padHorizontalPx, 0);
            chip.setGravity(Gravity.CENTER);
            chip.setText(val);
            chip.setTextSize(14f);
            chip.setTextColor(Color.BLACK);
            chip.setTypeface(suiteRegular, Typeface.BOLD);
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.selector_value_chip));
            chip.setSelected(selectedValues.contains(val));
            chip.setOnClickListener(v -> toggleValueSelection(val));

            if (currentRow != null) {
                currentRow.addView(chip);
            }
        }
    }

    private void refreshChipSelections() {
        for (TextView chip : defaultChipViews) {
            chip.setSelected(selectedValues.contains(chip.getText().toString()));
        }

        for (int i = 0; i < layoutCustomChipsContainer.getChildCount(); i++) {
            View rowView = layoutCustomChipsContainer.getChildAt(i);
            if (rowView instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) rowView;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof TextView) {
                        TextView tv = (TextView) child;
                        tv.setSelected(selectedValues.contains(tv.getText().toString()));
                    }
                }
            }
        }
    }

    private void setupSeekBar() {
        seekBarSatisfaction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSatisfactionLabel(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSatisfaction.post(() -> updateSatisfactionLabel(seekBarSatisfaction.getProgress() + 1));
    }

    private void updateSatisfactionLabel(int level) {
        tvSatisfactionLevel.setText("Lv." + level);
        seekBarSatisfaction.post(() -> {
            int availableWidth = seekBarSatisfaction.getWidth()
                    - seekBarSatisfaction.getPaddingLeft()
                    - seekBarSatisfaction.getPaddingRight();
            float ratio = (float) seekBarSatisfaction.getProgress() / seekBarSatisfaction.getMax();
            float thumbX = seekBarSatisfaction.getPaddingLeft() + (availableWidth * ratio);
            float labelX = thumbX - (tvSatisfactionLevel.getWidth() / 2f);

            float maxLabelX = seekBarSatisfaction.getWidth() - tvSatisfactionLevel.getWidth();
            labelX = Math.max(0f, Math.min(labelX, maxLabelX));
            tvSatisfactionLevel.setTranslationX(labelX);
        });
    }

    private void setupListeners() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> handleBackNavigation());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackNavigation();
            }
        });

        btnMainNodeNext.setOnClickListener(v -> handleNextButtonClick());

        LinearLayout navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(v -> moveToHome());

        LinearLayout navMy = findViewById(R.id.nav_my);
        navMy.setOnClickListener(v ->
                Toast.makeText(CreateMainNodeActivity.this, "마이 화면은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleBackNavigation() {
        if (currentStep == 2) {
            saveCurrentStepDraft(2);
            renderStep(1, true);
        } else {
            finish();
        }
    }

    private void handleNextButtonClick() {
        if (!validateCurrentStep()) {
            return;
        }

        if (currentStep == 1) {
            saveCurrentStepDraft(1);
            renderStep(2, true);
        } else {
            saveCurrentStepDraft(2);
            completeWorldCreation();
        }
    }

    private boolean validateCurrentStep() {
        if (selectedValues.isEmpty()) {
            Toast.makeText(this, "중요하게 생각하는 가치를 1개 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etExpect.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "기대하는 점을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etExpect.requestFocus();
            return false;
        }

        if (etWorry.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "걱정되는 점을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etWorry.requestFocus();
            return false;
        }

        if (etGiveUp.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "포기해야 할 것을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etGiveUp.requestFocus();
            return false;
        }

        return true;
    }

    private void saveCurrentStepDraft(int step) {
        if (step == 1) {
            savedValues1.clear();
            savedValues1.addAll(selectedValues);
            savedCustom1.clear();
            savedCustom1.addAll(customValuesList);
            savedExpect1 = etExpect.getText().toString().trim();
            savedWorry1 = etWorry.getText().toString().trim();
            savedGiveUp1 = etGiveUp.getText().toString().trim();
            savedSatisfaction1 = seekBarSatisfaction.getProgress() + 1;
        } else {
            savedValues2.clear();
            savedValues2.addAll(selectedValues);
            savedCustom2.clear();
            savedCustom2.addAll(customValuesList);
            savedExpect2 = etExpect.getText().toString().trim();
            savedWorry2 = etWorry.getText().toString().trim();
            savedGiveUp2 = etGiveUp.getText().toString().trim();
            savedSatisfaction2 = seekBarSatisfaction.getProgress() + 1;
        }
    }

    private void renderStep(int step, boolean scrollToTop) {
        currentStep = step;
        etCustomValue.setText("");

        if (step == 1) {
            progressStep3.setBackgroundColor(Color.parseColor("#E8EAF2"));
            tvChoiceHeader.setText("선택지 1. " + choice1);

            etExpect.setHint("ex) 실무 경험과 포트폴리오를 쌓고, 첫 월급으로 경제적으로 독립하고 싶어요.\n같이 일하는 선배들에게 배우면서 개발자로서 빠르게 성장할 수 있을 것 같아요.");
            etWorry.setHint("ex) 첫 직장이 나랑 안 맞으면 어쩌나 걱정돼요. 업무량이 많아서 야근이 잦아지거나, 신입이라 적응하는 동안 스트레스를 많이 받을 것 같아요.");
            etGiveUp.setHint("ex) 대학원 진학이나 더 깊이 공부할 시간을 포기해야 해요. 방학, 여행, 자유로운 시간처럼 학생일 때만 누릴 수 있는 여유도 줄어들 것 같아요.");

            selectedValues.clear();
            selectedValues.addAll(savedValues1);
            customValuesList.clear();
            customValuesList.addAll(savedCustom1);

            etExpect.setText(savedExpect1);
            etWorry.setText(savedWorry1);
            etGiveUp.setText(savedGiveUp1);
            seekBarSatisfaction.setProgress(savedSatisfaction1 - 1);
            updateSatisfactionLabel(savedSatisfaction1);

            btnMainNodeNext.setText("선택지 2로 넘어가기");
        } else {
            progressStep3.setBackgroundColor(Color.parseColor("#5363BE"));
            tvChoiceHeader.setText("선택지 2. " + choice2);

            etExpect.setHint("ex) 교수님과 연구실 동료들에게 배우면서 전문성을 쌓고, 논문을 한편이라도 완성해 보고 싶어요. 졸업할 때는 한 분야에서 \"이건 내가 잘 안다\"고 말할 수 있게 되길 기대해요.");
            etWorry.setHint("ex) 연구가 생각만큼 잘 안 풀리거나 교수님과 맞지 않으면 어떡하나 걱정돼요. 졸업이 늦어질 수도 있고, 등록금과 생활비 부담도 신경 쓰여요.");
            etGiveUp.setHint("ex) 2-3년 동안 빨리 취업해서 쌓을 수 있는 사회 경력과 안정적인 수입을 포기해야 해요. 친구들이 먼저 자리 잡는 모습을 보면서 불안해질 수도 있어요.");

            selectedValues.clear();
            selectedValues.addAll(savedValues2);
            customValuesList.clear();
            customValuesList.addAll(savedCustom2);

            etExpect.setText(savedExpect2);
            etWorry.setText(savedWorry2);
            etGiveUp.setText(savedGiveUp2);
            seekBarSatisfaction.setProgress(savedSatisfaction2 - 1);
            updateSatisfactionLabel(savedSatisfaction2);

            btnMainNodeNext.setText("세계 생성하기");
        }

        rebuildCustomChipsUI();
        refreshChipSelections();

        if (scrollToTop) {
            scrollMainNode.post(() -> scrollMainNode.smoothScrollTo(0, 0));
        }
    }

    private void completeWorldCreation() {
        Toast.makeText(this, "새로운 세계가 생성되었습니다!", Toast.LENGTH_SHORT).show();
        moveToHome();
    }

    private void moveToHome() {
        Intent intent = new Intent(CreateMainNodeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}