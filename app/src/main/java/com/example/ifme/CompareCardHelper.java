package com.example.ifme;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

/**
 * 비교 카드(view_compare_card.xml) 한 장에 데이터를 채우는 클래스.
 * 왼쪽, 오른쪽 카드 모두 이 메서드 하나로 채운다.
 */
public class CompareCardHelper {
    private static final int MAX_LINES = 4;

    /**
     * @param card         include로 넣은 카드 (findViewById(R.id.compareCardLeft) 등)
     * @param subtitle     부제, 노드 이름 (예: 취업하기)
     * @param title        제목 (예상 / 현실 / 평행세계)
     * @param expects      기대하는 점 목록
     * @param concerns     걱정하는 점 목록
     * @param satisfaction 만족도 (0~10)
     */
    public static void bind(View card, String subtitle, String title,
                            List<String> expects, List<String> concerns, int satisfaction) {
        ((TextView) card.findViewById(R.id.compareCardNodeName)).setText(subtitle);
        ((TextView) card.findViewById(R.id.compareCardType)).setText(title);
        ((TextView) card.findViewById(R.id.compareCardSatisfaction))
                .setText("만족도 " + satisfaction + "/10");

        fillList(card.findViewById(R.id.compareCardExpectList), expects);
        fillList(card.findViewById(R.id.compareCardConcernList), concerns);
    }

    private static void fillList(LinearLayout container, List<String> items) {
        container.removeAllViews();

        int count = Math.min(items.size(), MAX_LINES);
        for (int i = 0; i < count; i++) {
            TextView line = new TextView(container.getContext());
            line.setText("•  " + items.get(i));
            line.setTextColor(0xFFFFFFFF);
            line.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            line.setTypeface(ResourcesCompat.getFont(container.getContext(), R.font.suite_medium));
            line.setMaxLines(1);
            line.setEllipsize(TextUtils.TruncateAt.END);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = dp(container, 2);
            container.addView(line, params);
        }
    }

    private static int dp(View view, int value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }
}
