package com.example.ifme;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private static final int[] VALUE_BAR_COLORS = {
            0xFF7C6CF7, 0xFF5B3FE6, 0xFF5236C4, 0xFF42309A
    };

    private static class ReportData {
        String typeTitle, typeDesc;
        List<String> tags;
        String aiComment;

        float expectAvg, realAvg;

        String satisfactionDesc;

        Map<String, Integer> valueCounts;

        int worryTotal, worryHit;

        int reselectSame, reselectTotal;
    }
    private final Map<String, ReportData> reports = new LinkedHashMap<>();
    private String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        FooterHelper.setup(this, FooterHelper.TAB_MYPAGE);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightNavigationBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        View footerContainer = findViewById(R.id.reportFooterContainer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            footerContainer.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        findViewById(R.id.reportBtnBack).setOnClickListener(v -> finish());
        findViewById(R.id.reportMonthBox).setOnClickListener(this::showMonthMenu);

        loadDummyData();

        if (!reports.isEmpty()) {
            showReport(reports.keySet().iterator().next());
        }
    }

    private void showMonthMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        Menu menu = popup.getMenu();
        for (String month : reports.keySet()) {
            MenuItem item = menu.add(0, Menu.NONE, Menu.NONE, month);
            item.setChecked(month.equals(currentMonth));
        }
        menu.setGroupCheckable(0, true, true);

        popup.setOnMenuItemClickListener(item -> {
            showReport(item.getTitle().toString());
            return true;
        });
        popup.show();
    }

    private void showReport(String month) {
        ReportData data = reports.get(month);
        if (data == null) return;
        currentMonth = month;

        ((TextView) findViewById(R.id.reportMonthSelect)).setText(month);

        ((TextView) findViewById(R.id.reportTypeTitle)).setText(data.typeTitle);
        ((TextView) findViewById(R.id.reportTypeDesc)).setText(data.typeDesc);
        fillTags(findViewById(R.id.reportTagList), data.tags);

        TableLayout satisfactionTable = findViewById(R.id.reportSatisfactionTable);
        satisfactionTable.removeAllViews();
        addBarRow(satisfactionTable, "예상", String.valueOf(data.expectAvg),
                Math.round(data.expectAvg * 10), 0xFFC9BFF5, 0xFF8C8FE0, 0xFFFFFFFF);
        addBarRow(satisfactionTable, "실제", String.valueOf(data.realAvg),
                Math.round(data.realAvg * 10), 0xFF9CB5F5, 0xFF8C8FE0, 0xFFFFFFFF);
        ((TextView) findViewById(R.id.reportSatisfactionDesc)).setText(
                HtmlCompat.fromHtml(data.satisfactionDesc, HtmlCompat.FROM_HTML_MODE_LEGACY));

        TableLayout valueTable = findViewById(R.id.reportValueTable);
        valueTable.removeAllViews();
        int maxCount = 1;
        for (int c : data.valueCounts.values()) maxCount = Math.max(maxCount, c);
        int rank = 0;
        for (Map.Entry<String, Integer> e : data.valueCounts.entrySet()) {
            if (rank >= VALUE_BAR_COLORS.length) break;
            addBarRow(valueTable, e.getKey(), e.getValue() + "회",
                    e.getValue() * 100 / maxCount, VALUE_BAR_COLORS[rank],
                    0xFFFFFFFF, 0xFF9A9DE6);
            rank++;
        }

        fillWorryDots(findViewById(R.id.reportWorryDots), data.worryTotal, data.worryHit);
        ((TextView) findViewById(R.id.reportWorryDesc)).setText(HtmlCompat.fromHtml(
                "걱정 " + data.worryTotal + "개 중<br><b>" + data.worryHit + "개</b>만 실제로 일어났어요",
                HtmlCompat.FROM_HTML_MODE_LEGACY));

        ((TextView) findViewById(R.id.reportReselectSame)).setText(String.valueOf(data.reselectSame));
        ((TextView) findViewById(R.id.reportReselectTotal)).setText("/ " + data.reselectTotal);
        ((TextView) findViewById(R.id.reportReselectDesc)).setText(HtmlCompat.fromHtml(
                "회고에서 <b>같은 선택</b>을<br>하겠다고 답했어요",
                HtmlCompat.FROM_HTML_MODE_LEGACY));

        ((TextView) findViewById(R.id.reportAiComment)).setText(data.aiComment);
    }

    private void fillTags(LinearLayout container, List<String> tags) {
        container.removeAllViews();

        int count = Math.min(tags.size(), 3);
        for (int i = 0; i < count; i++) {
            TextView tag = new TextView(this);
            tag.setText("#" + tags.get(i));
            tag.setTextColor(0xFFFFFFFF);
            tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tag.setTypeface(ResourcesCompat.getFont(this, R.font.suite_medium));
            tag.setGravity(Gravity.CENTER);
            tag.setBackgroundResource(R.drawable.bg_report_tag);
            tag.setPadding(dp(12), dp(5), dp(12), dp(5));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) params.setMarginStart(dp(8));
            container.addView(tag, params);
        }
    }

    private void addBarRow(TableLayout table, String label, String valueText, int progress,
                           int barColor, int labelColor, int valueColor) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextColor(labelColor);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        labelView.setTypeface(ResourcesCompat.getFont(this, R.font.suite_medium));
        labelView.setPadding(0, 0, dp(14), 0);

        int p = Math.max(0, Math.min(100, progress));

        LinearLayout track = new LinearLayout(this);
        track.setOrientation(LinearLayout.HORIZONTAL);
        track.setBackground(roundRect(0xFF33343F));

        View fill = new View(this);
        fill.setBackground(roundRect(barColor));
        track.addView(fill, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, p));

        View rest = new View(this);
        track.addView(rest, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 100 - p));

        TextView valueView = new TextView(this);
        valueView.setText(valueText);
        valueView.setTextColor(valueColor);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        valueView.setTypeface(ResourcesCompat.getFont(this, R.font.suite_medium));
        valueView.setPadding(dp(12), 0, 0, 0);

        row.addView(labelView);
        row.addView(track, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, dp(10)));
        row.addView(valueView);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        if (table.getChildCount() > 0) rowParams.topMargin = dp(10);
        table.addView(row, rowParams);
    }

    private GradientDrawable roundRect(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(5));
        return shape;
    }

    private void fillWorryDots(GridLayout grid, int total, int hit) {
        grid.removeAllViews();
        for (int i = 0; i < total; i++) {
            View dot = new View(this);
            dot.setBackgroundResource(i < hit
                    ? R.drawable.bg_report_dot_filled
                    : R.drawable.bg_report_dot_empty);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dp(24);
            params.height = dp(24);
            params.setMargins(0, 0, dp(6), dp(6));
            grid.addView(dot, params);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void loadDummyData() {
        ReportData oct = new ReportData();
        oct.typeTitle = "성장을 따라가는\n탐험가";
        oct.typeDesc = "불확실하더라도 새로운 경험을 주는\n길을 더 자주 골랐어요.";
        oct.tags = Arrays.asList("성장", "도전", "새로운 경험");
        oct.expectAvg = 7.8f;
        oct.realAvg = 6.9f;
        oct.satisfactionDesc = "평균적으로 기대가 실제보다 0.9점 높았어요.<br>"
                + "특히 <b>'진로'</b> 관련 선택에서 차이가 컸어요.";
        oct.valueCounts = new LinkedHashMap<>();
        oct.valueCounts.put("성장", 5);
        oct.valueCounts.put("안정", 3);
        oct.valueCounts.put("관계", 2);
        oct.valueCounts.put("경제", 1);
        oct.worryTotal = 8;
        oct.worryHit = 3;
        oct.reselectSame = 6;
        oct.reselectTotal = 8;
        oct.aiComment = "걱정은 대부분 일어나지 않았어요.\n"
                + "다음 선택에서는 걱정보다 기대한 점에\n"
                + "조금 더 무게를 둬도 괜찮아요.";
        reports.put("2026년 10월", oct);

        ReportData sep = new ReportData();
        sep.typeTitle = "안정을 지키는\n설계자";
        sep.typeDesc = "위험을 줄이고 예측 가능한\n길을 더 자주 골랐어요.";
        sep.tags = Arrays.asList("안정", "관계");
        sep.expectAvg = 6.5f;
        sep.realAvg = 7.2f;
        sep.satisfactionDesc = "평균적으로 실제가 기대보다 0.7점 높았어요.<br>"
                + "특히 <b>'인간관계'</b> 관련 선택에서 만족이 컸어요.";
        sep.valueCounts = new LinkedHashMap<>();
        sep.valueCounts.put("안정", 4);
        sep.valueCounts.put("관계", 3);
        sep.valueCounts.put("여유", 1);
        sep.worryTotal = 5;
        sep.worryHit = 2;
        sep.reselectSame = 4;
        sep.reselectTotal = 5;
        sep.aiComment = "생각보다 결과가 좋았던 달이에요.\n"
                + "익숙한 선택도 충분히 좋은 선택이었어요.";
        reports.put("2026년 9월", sep);
    }
}