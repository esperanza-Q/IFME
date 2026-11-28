package com.example.ifme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RetrospectActivity extends AppCompatActivity {

    // STEP 1 가치 칩 10개 (마지막 10번째가 "기타")
    Integer[] chipIds = { R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4, R.id.chip5,
            R.id.chip6, R.id.chip7, R.id.chip8, R.id.chip9, R.id.chip10 };
    TextView[] chips = new TextView[10];
    boolean[] chipSelected = new boolean[10];
    int selectedCount = 0;
    static final int ETC_INDEX = 9; // "기타" 칩 번호

    // 기타 직접 입력
    LinearLayout layoutCustomValue;
    EditText etCustomValue;
    String customValue = "";

    EditText etGain, etHardship, etLoss;
    SeekBar sbSatisfaction;
    TextView tvLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrospect);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvVsTitle = findViewById(R.id.tv_vs_title);
        TextView tvNodeTitle = findViewById(R.id.tv_node_title);
        layoutCustomValue = findViewById(R.id.layout_custom_value);
        etCustomValue = findViewById(R.id.et_custom_value);
        Button btnAddValue = findViewById(R.id.btn_add_value);
        etGain = findViewById(R.id.et_gain);
        etHardship = findViewById(R.id.et_hardship);
        etLoss = findViewById(R.id.et_loss);
        sbSatisfaction = findViewById(R.id.sb_satisfaction);
        tvLevel = findViewById(R.id.tv_level);
        Button btnDone = findViewById(R.id.btn_done);

        // 칩 10개 찾기 + 클릭 설정
        for (int i = 0; i < chipIds.length; i++) {
            final int index = i;
            chips[i] = findViewById(chipIds[i]);
            chips[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleChip(index);
                }
            });
        }

        // 기타 → "추가" 버튼
        btnAddValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etCustomValue.getText().toString().trim();
                if (text.length() == 0) {
                    Toast.makeText(getApplicationContext(), "가치를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                customValue = text;
                chips[ETC_INDEX].setText(text);          // "기타" 칩이 입력한 글자로 바뀜
                layoutCustomValue.setVisibility(View.GONE);
            }
        });

        // 노드 선택 화면에서 넘겨준 값 받기
        Intent intent = getIntent();
        String vsTitle = intent.getStringExtra("vsTitle");
        String nodeTitle = intent.getStringExtra("nodeTitle");
        String pastExpect = intent.getStringExtra("pastExpect");
        String pastWorry = intent.getStringExtra("pastWorry");
        String pastGiveUp = intent.getStringExtra("pastGiveUp");

        if (vsTitle != null) tvVsTitle.setText(vsTitle);
        if (nodeTitle != null) tvNodeTitle.setText(nodeTitle);

        // STEP 2~4: 과거 답변 + 더보기
        setupPastAnswer((TextView) findViewById(R.id.tv_past_gain), (TextView) findViewById(R.id.tv_more_gain), pastExpect);
        setupPastAnswer((TextView) findViewById(R.id.tv_past_hardship), (TextView) findViewById(R.id.tv_more_hardship), pastWorry);
        setupPastAnswer((TextView) findViewById(R.id.tv_past_loss), (TextView) findViewById(R.id.tv_more_loss), pastGiveUp);

        // 회고 수정하기로 들어온 경우: 전에 썼던 내용 채우기
        boolean[] savedChips = intent.getBooleanArrayExtra("chipSelected");
        if (savedChips != null) {
            for (int i = 0; i < savedChips.length; i++) {
                if (savedChips[i]) {
                    toggleChip(i);
                }
            }
        }
        String savedCustom = intent.getStringExtra("customValue");
        if (savedCustom != null && savedCustom.length() > 0 && chipSelected[ETC_INDEX]) {
            customValue = savedCustom;
            chips[ETC_INDEX].setText(savedCustom);
            etCustomValue.setText(savedCustom);
            layoutCustomValue.setVisibility(View.GONE);
        }

        String savedGain = intent.getStringExtra("gainText");
        String savedHardship = intent.getStringExtra("hardshipText");
        String savedLoss = intent.getStringExtra("lossText");
        if (savedGain != null) etGain.setText(savedGain);
        if (savedHardship != null) etHardship.setText(savedHardship);
        if (savedLoss != null) etLoss.setText(savedLoss);

        int savedSatisfaction = intent.getIntExtra("satisfaction", 7);
        sbSatisfaction.setProgress(savedSatisfaction);
        tvLevel.setText("Lv." + savedSatisfaction);

        // STEP 5: 만족도 슬라이더 (Lv.0 ~ Lv.10)
        sbSatisfaction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevel.setText("Lv." + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 뒤로 가기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 작성 완료 → 쓴 내용을 노드 선택 화면으로 돌려주기
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기타를 골랐는데 입력을 안 했으면 막기
                if (chipSelected[ETC_INDEX] && customValue.length() == 0) {
                    Toast.makeText(getApplicationContext(), "기타 가치를 입력하고 추가를 눌러주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: 회고 내용 서버에 저장
                Toast.makeText(getApplicationContext(), "회고가 저장되었어요", Toast.LENGTH_SHORT).show();

                Intent outIntent = new Intent();
                outIntent.putExtra("chipSelected", chipSelected);
                outIntent.putExtra("customValue", customValue);
                outIntent.putExtra("gainText", etGain.getText().toString());
                outIntent.putExtra("hardshipText", etHardship.getText().toString());
                outIntent.putExtra("lossText", etLoss.getText().toString());
                outIntent.putExtra("satisfaction", sbSatisfaction.getProgress());
                setResult(RESULT_OK, outIntent);
                finish();
            }
        });
    }

    // 칩 하나를 선택하거나 해제
    void toggleChip(int index) {
        if (chipSelected[index]) {
            // 해제
            chipSelected[index] = false;
            selectedCount--;
            chips[index].setBackgroundResource(R.drawable.bg_retro_chip_off);

            if (index == ETC_INDEX) {
                // 기타 해제 → 입력칸 숨기고 원래대로
                layoutCustomValue.setVisibility(View.GONE);
                customValue = "";
                etCustomValue.setText("");
                chips[ETC_INDEX].setText("기타");
            }
        } else {
            // 선택
            if (selectedCount >= 3) {
                Toast.makeText(getApplicationContext(), "가치는 최대 3개까지 선택할 수 있어요", Toast.LENGTH_SHORT).show();
                return;
            }
            chipSelected[index] = true;
            selectedCount++;
            chips[index].setBackgroundResource(R.drawable.bg_retro_chip_on);

            if (index == ETC_INDEX) {
                // 기타 선택 → 입력칸 보이기
                layoutCustomValue.setVisibility(View.VISIBLE);
            }
        }
    }

    // 과거 답변 보여주기 + 더보기를 누르면 펼치기/접기
    void setupPastAnswer(final TextView tvPast, final TextView tvMore, String answer) {
        if (answer == null || answer.length() == 0) {
            tvPast.setText("작성한 답변이 없어요");
            tvMore.setVisibility(View.GONE);
            return;
        }

        tvPast.setText(answer);

        // 글이 짧으면 더보기 숨기기
        if (answer.length() <= 30) {
            tvMore.setVisibility(View.GONE);
        }

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvPast.getMaxLines() == 2) {
                    tvPast.setMaxLines(100);   // 전체 펼치기
                    tvMore.setText("접기");
                } else {
                    tvPast.setMaxLines(2);     // 다시 2줄로
                    tvMore.setText("...더보기");
                }
            }
        });
    }
}