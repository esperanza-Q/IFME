package com.example.ifme;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeSelectActivity extends AppCompatActivity {

    private ViewPager2 vpNodeCards;
    private LinearLayout layoutIndicator;
    private List<NodeCard> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_select);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvWorldTitle = findViewById(R.id.tv_world_title);
        vpNodeCards = findViewById(R.id.vp_node_cards);
        layoutIndicator = findViewById(R.id.layout_indicator);
        View btnSelect = findViewById(R.id.btn_select);
        View btnCompare = findViewById(R.id.btn_compare);

        btnBack.setOnClickListener(v -> finish());
        tvWorldTitle.setText("(세계 제목)"); // TODO: 실제 세계 제목 연결

        cards = createSampleCards();
        setupViewPager();
        setupIndicator();

        btnSelect.setOnClickListener(v -> {
            NodeCard current = cards.get(vpNodeCards.getCurrentItem());
            Toast.makeText(this, current.title + " 선택", Toast.LENGTH_SHORT).show();
            // TODO: 선택 후 동작은 팀에서 정해서 연결
        });

        btnCompare.setOnClickListener(v ->
                Toast.makeText(this, "비교하기는 준비 중이에요", Toast.LENGTH_SHORT).show());
    }

    private void setupViewPager() {
        vpNodeCards.setAdapter(new NodeCardAdapter(cards));
        vpNodeCards.setOffscreenPageLimit(1);
        vpNodeCards.setPageTransformer(new MarginPageTransformer(dpToPx(16)));

        RecyclerView innerList = (RecyclerView) vpNodeCards.getChildAt(0);
        int side = dpToPx(48);
        innerList.setPadding(side, 0, side, 0);
        innerList.setClipToPadding(false);
        innerList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        vpNodeCards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
            }
        });
    }

    private void setupIndicator() {
        layoutIndicator.removeAllViews();
        for (int i = 0; i < cards.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
            params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_node_indicator);
            layoutIndicator.addView(dot);
        }
        updateIndicator(0);
    }

    private void updateIndicator(int selectedPosition) {
        for (int i = 0; i < layoutIndicator.getChildCount(); i++) {
            View dot = layoutIndicator.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            params.width = dpToPx(i == selectedPosition ? 40 : 6);
            dot.setLayoutParams(params);
        }
    }

    // TODO: 나중에 실제 데이터로 바꾸기 (지금은 예시)
    private List<NodeCard> createSampleCards() {
        List<NodeCard> list = new ArrayList<>();
        list.add(new NodeCard(1, "취업하기", false,
                Arrays.asList("안정", "성장", "인정"),
                "졸업 후 돈을 바로 벌 수 있다.", "", "", 7));
        list.add(new NodeCard(2, "대학원 진학하기", false,
                Arrays.asList("안정", "성장", "인정"),
                "졸업 후 돈을 바로 벌 수 있다.", "", "", 7));
        return list;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}