package com.example.ifme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class NodeSelectActivity extends AppCompatActivity {

    static final int REQUEST_RETROSPECT = 1; // 회고 화면을 열 때 쓰는 요청 번호

    ViewPager2 vpNodeCards;
    LinearLayout layoutIndicator;
    Button btnPrimary, btnCompare;

    ArrayList<NodeCard> cards;
    NodeCardAdapter adapter;

    int selectedIndex = -1;         // 선택한 카드 번호 (-1 = 아직 선택 안 함)
    boolean reviewWritten = false;  // 회고를 썼는지

    // 작성한 회고 내용 (회고 수정하기 때 다시 보여주려고 저장)
    boolean[] savedChips;
    String savedCustom, savedGain, savedHardship, savedLoss;
    int savedSatisfaction = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_select);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvWorldTitle = findViewById(R.id.tv_world_title);
        vpNodeCards = findViewById(R.id.vp_node_cards);
        layoutIndicator = findViewById(R.id.layout_indicator);
        btnPrimary = findViewById(R.id.btn_primary);
        btnCompare = findViewById(R.id.btn_compare);

        tvWorldTitle.setText("(세계 제목)"); // TODO: 실제 세계 제목 연결

        // 예시 카드 만들기 (TODO: 나중에 실제 데이터로 바꾸기)
        cards = new ArrayList<NodeCard>();
        cards.add(new NodeCard(1, "취업하기",
                new String[] { "안정", "성장", "인정" },
                "졸업 후 돈을 바로 벌 수 있다. 회사에서 실무 경험을 쌓으면서 커리어를 빨리 시작할 수 있을 것 같다.",
                "원하는 직무가 아닐 수도 있다.",
                "공부를 더 할 수 있는 시간",
                7));
        cards.add(new NodeCard(2, "대학원 진학하기",
                new String[] { "성장", "전문성", "인정" },
                "전문성을 쌓을 수 있다.",
                "졸업이 늦어지고 그동안 수입이 없다.",
                "바로 돈을 벌 수 있는 기회",
                6));

        setupViewPager();
        setupIndicator();
        updateButtons(0);

        // 뒤로 가기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 왼쪽 버튼: 선택 / 회고 작성하기 / 회고 수정하기
        btnPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = vpNodeCards.getCurrentItem();

                if (selectedIndex == -1) {
                    // 아직 선택 안 함 → 지금 보는 카드를 선택
                    selectedIndex = current;
                    adapter.setSelectedIndex(current);
                    updateButtons(current);
                } else if (current == selectedIndex) {
                    // 선택한 카드 → 회고 작성(수정) 화면 열기
                    openRetrospect();
                }
            }
        });

        // 오른쪽 버튼: 비교하기
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 비교 화면(CompareActivity) 연결
                Toast.makeText(getApplicationContext(), "비교 화면은 준비 중이에요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 카드를 옆으로 넘기는 화면 설정
    void setupViewPager() {
        adapter = new NodeCardAdapter(cards);
        vpNodeCards.setAdapter(adapter);
        vpNodeCards.setOffscreenPageLimit(1);
        vpNodeCards.setPageTransformer(new MarginPageTransformer(dpToPx(16))); // 카드 사이 간격

        // 옆 카드가 살짝 보이게 양옆에 여백 주기
        RecyclerView innerList = (RecyclerView) vpNodeCards.getChildAt(0);
        innerList.setPadding(dpToPx(48), 0, dpToPx(48), 0);
        innerList.setClipToPadding(false);
        innerList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // 카드를 넘길 때마다 실행
        vpNodeCards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
                updateButtons(position);
            }
        });
    }

    // 회고 작성 화면 열기 (값을 넘기고, 결과를 돌려받음)
    void openRetrospect() {
        NodeCard selected = cards.get(selectedIndex);

        Intent intent = new Intent(getApplicationContext(), RetrospectActivity.class);
        intent.putExtra("vsTitle", cards.get(0).title + " VS " + cards.get(1).title);
        intent.putExtra("nodeTitle", selected.title);
        intent.putExtra("pastExpect", selected.expect);
        intent.putExtra("pastWorry", selected.worry);
        intent.putExtra("pastGiveUp", selected.giveUp);

        // 이미 회고를 썼으면 → 전에 쓴 내용도 같이 보내기 (수정하기)
        if (reviewWritten) {
            intent.putExtra("chipSelected", savedChips);
            intent.putExtra("customValue", savedCustom);
            intent.putExtra("gainText", savedGain);
            intent.putExtra("hardshipText", savedHardship);
            intent.putExtra("lossText", savedLoss);
            intent.putExtra("satisfaction", savedSatisfaction);
        }

        startActivityForResult(intent, REQUEST_RETROSPECT);
    }

    // 회고 화면에서 "작성 완료"를 누르고 돌아오면 실행
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RETROSPECT && resultCode == RESULT_OK && data != null) {
            reviewWritten = true;

            // 돌려받은 회고 내용 저장
            savedChips = data.getBooleanArrayExtra("chipSelected");
            savedCustom = data.getStringExtra("customValue");
            savedGain = data.getStringExtra("gainText");
            savedHardship = data.getStringExtra("hardshipText");
            savedLoss = data.getStringExtra("lossText");
            savedSatisfaction = data.getIntExtra("satisfaction", 7);

            updateButtons(vpNodeCards.getCurrentItem());
        }
    }

    // 상태에 따라 버튼 글자와 색 바꾸기
    void updateButtons(int position) {
        if (selectedIndex == -1) {
            // 아직 선택 안 함
            setButton(btnPrimary, "선택", true);
            setButton(btnCompare, "비교하기", false);
        } else if (position == selectedIndex) {
            // 내가 선택한 카드를 보는 중
            if (reviewWritten) {
                setButton(btnPrimary, "회고 수정하기", true);
            } else {
                setButton(btnPrimary, "회고 작성하기", true);
            }
            setButton(btnCompare, "비교하기", reviewWritten);
        } else {
            // 선택하지 않은 카드를 보는 중
            setButton(btnPrimary, "회고 작성하기", false);
            setButton(btnCompare, "비교하기", reviewWritten);
        }
    }

    // 버튼 하나를 활성(흰색) 또는 비활성(회색)으로
    void setButton(Button button, String text, boolean active) {
        button.setText(text);
        button.setEnabled(active);
        if (active) {
            button.setBackgroundResource(R.drawable.bg_node_btn_select);
            button.setTextColor(0xFF111111);
        } else {
            button.setBackgroundResource(R.drawable.bg_node_btn_compare);
            button.setTextColor(0xFF8A8A8A);
        }
    }

    // 아래쪽 점 표시 만들기
    void setupIndicator() {
        layoutIndicator.removeAllViews();
        for (int i = 0; i < cards.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
            params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_node_indicator);
            layoutIndicator.addView(dot);
        }
        updateIndicator(0);
    }

    // 지금 보는 카드의 점만 길쭉하게
    void updateIndicator(int selectedPosition) {
        for (int i = 0; i < layoutIndicator.getChildCount(); i++) {
            View dot = layoutIndicator.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            if (i == selectedPosition) {
                params.width = dpToPx(40);
            } else {
                params.width = dpToPx(6);
            }
            dot.setLayoutParams(params);
        }
    }

    // dp 단위를 픽셀로 바꾸기
    int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}