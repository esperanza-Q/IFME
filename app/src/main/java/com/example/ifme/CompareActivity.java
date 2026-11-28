package com.example.ifme;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 비교하기 화면.
 * 탭 0: 예상 vs 현실 (내가 고른 노드의 예상 / 그 노드의 회고)
 * 탭 1: 현실 vs 평행세계 (내가 고른 노드의 회고 / 고르지 않은 노드의 예상)
 * 화면은 하나이고, 탭을 누르면 같은 카드와 벤다이어그램에 들어가는 데이터만 바뀐다.
 * 지금은 가짜 데이터. Room 연결 후 loadDummyData() 부분만 실제 조회로 바꾸면 된다.
 */
public class CompareActivity extends AppCompatActivity {

    private static final int TAB_EXPECT = 0;
    private static final int TAB_PARALLEL = 1;

    private TextView tabExpect, tabParallel;
    private View cardLeft, cardRight;
    private TextView vennTitle, vennLeftOnly, vennBoth, vennRightOnly;
    private TextView legendLeft, legendRight;

    // ===== 가짜 데이터 (Room 연결 전까지 사용) =====
    private String chosenNodeName, otherNodeName;
    private List<String> expectGood, expectBad, expectValues;     // 선택 노드의 예상
    private int expectSatisfaction;
    private List<String> realGood, realBad, realValues;           // 선택 노드의 회고
    private int realSatisfaction;
    private List<String> parallelGood, parallelBad, parallelValues; // 미선택 노드의 예상
    private int parallelSatisfaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compare);

        // 검은 바탕이라 상태바, 내비게이션 바 아이콘을 흰색으로
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightNavigationBars(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        // 상태바, 내비게이션 바와 겹치지 않게 여백
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.compareRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tabExpect = findViewById(R.id.compareTabExpect);
        tabParallel = findViewById(R.id.compareTabParallel);
        cardLeft = findViewById(R.id.compareCardLeft);
        cardRight = findViewById(R.id.compareCardRight);
        vennTitle = findViewById(R.id.compareVennTitle);
        vennLeftOnly = findViewById(R.id.compareVennLeftOnly);
        vennBoth = findViewById(R.id.compareVennBoth);
        vennRightOnly = findViewById(R.id.compareVennRightOnly);
        legendLeft = findViewById(R.id.compareLegendLeft);
        legendRight = findViewById(R.id.compareLegendRight);

        findViewById(R.id.compareBtnBack).setOnClickListener(v -> finish());
        findViewById(R.id.compareBtnBackToTree).setOnClickListener(v -> finish());

        tabExpect.setOnClickListener(v -> showTab(TAB_EXPECT));
        tabParallel.setOnClickListener(v -> showTab(TAB_PARALLEL));

        loadDummyData();
        showTab(TAB_EXPECT);
    }
    private void showTab(int tab) {
        boolean isExpect = (tab == TAB_EXPECT);

        tabExpect.setBackgroundResource(isExpect ? R.drawable.bg_compare_tab_selected : 0);
        tabParallel.setBackgroundResource(isExpect ? 0 : R.drawable.bg_compare_tab_selected);
        tabExpect.setTextColor(isExpect ? 0xFFFFFFFF : 0xFF8F96F2);
        tabParallel.setTextColor(isExpect ? 0xFF8F96F2 : 0xFFFFFFFF);

        if (isExpect) {
            CompareCardHelper.bind(cardLeft, chosenNodeName, "예상",
                    expectGood, expectBad, expectSatisfaction);
            CompareCardHelper.bind(cardRight, chosenNodeName, "현실",
                    realGood, realBad, realSatisfaction);
            vennTitle.setText("지향 가치 vs 실제 가치");
            legendLeft.setText("지향 가치");
            legendRight.setText("실제로 얻은 가치");
            fillVenn(expectValues, realValues);
        } else {
            CompareCardHelper.bind(cardLeft, chosenNodeName, "현실",
                    realGood, realBad, realSatisfaction);
            CompareCardHelper.bind(cardRight, otherNodeName, "평행세계",
                    parallelGood, parallelBad, parallelSatisfaction);
            vennTitle.setText("현실 가치 vs 평행세계 가치");
            legendLeft.setText("현실 가치");
            legendRight.setText("평행세계 가치");
            fillVenn(realValues, parallelValues);
        }
    }

    private void fillVenn(List<String> left, List<String> right) {
        List<String> leftOnly = new ArrayList<>();
        List<String> both = new ArrayList<>();
        List<String> rightOnly = new ArrayList<>();

        for (String value : left) {
            if (right.contains(value)) both.add(value);
            else leftOnly.add(value);
        }
        for (String value : right) {
            if (!left.contains(value)) rightOnly.add(value);
        }

        vennLeftOnly.setText(TextUtils.join("\n", leftOnly));
        vennBoth.setText(TextUtils.join("\n", both));
        vennRightOnly.setText(TextUtils.join("\n", rightOnly));
    }

    private void loadDummyData() {
        chosenNodeName = "취업하기";
        otherNodeName = "대학원가기";

        expectGood = Arrays.asList("빠른 수입 시작", "취업 불안 해소", "빠르게 경제적 독립", "어쩌구");
        expectBad = Arrays.asList("초봉 낮을 수 있음", "진로 탐색 부족", "적성 확인 부족", "저쩌구");
        expectValues = Arrays.asList("전문성", "안정성", "도전");
        expectSatisfaction = 7;

        realGood = Arrays.asList("취업 불안 해소", "빠른 수입 시작", "빠르게 경제적 독립", "어쩌구");
        realBad = Arrays.asList("초봉 낮을 수 있음", "진로 탐색 부족", "적성 확인 부족", "저쩌구");
        realValues = Arrays.asList("안정성", "도전");
        realSatisfaction = 7;

        parallelGood = Arrays.asList("빠르게 경제적 독립", "빠른 수입 시작", "취업 불안 해소", "어쩌구");
        parallelBad = Arrays.asList("초봉 낮을 수 있음", "진로 탐색 부족", "적성 확인 부족", "저쩌구");
        parallelValues = Arrays.asList("성장", "자유", "의미");
        parallelSatisfaction = 7;
    }
}
