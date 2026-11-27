package com.example.ifme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/** Figma 형태의 만족도 바. 원형 핸들과 레벨 텍스트가 함께 움직임. */
public class SatisfactionSliderView extends View {

    public interface OnLevelChangeListener {
        void onLevelChanged(int level);
    }

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int level = 7;
    private OnLevelChangeListener listener;

    public SatisfactionSliderView(Context context) {
        this(context, null);
    }

    public SatisfactionSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        trackPaint.setColor(Color.parseColor("#EEEFF0"));
        fillPaint.setColor(Color.parseColor("#5D7AFA"));
        thumbPaint.setColor(Color.parseColor("#5D7AFA"));
        labelPaint.setColor(Color.parseColor("#5D7AFA"));
        labelPaint.setTextSize(sp(14));
        labelPaint.setFakeBoldText(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        setClickable(true);
        setFocusable(true);
    }

    public void setLevel(int nextLevel) {
        int value = Math.max(1, Math.min(10, nextLevel));
        if (level != value) {
            level = value;
            if (listener != null) listener.onLevelChanged(level);
            invalidate();
        }
    }

    public void setOnLevelChangeListener(OnLevelChangeListener callback) {
        listener = callback;
    }

    public int getLevel() {
        return level;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = dp(8);
        float cy = dp(14);
        float barHalfHeight = dp(3);
        float startX = radius;
        float endX = Math.max(startX, getWidth() - radius);
        float x = startX + (endX - startX) * (level - 1) / 9f;

        RectF wholeTrack = new RectF(startX, cy - barHalfHeight,
                endX, cy + barHalfHeight);
        canvas.drawRoundRect(wholeTrack, barHalfHeight, barHalfHeight, trackPaint);
        RectF filledTrack = new RectF(startX, cy - barHalfHeight,
                x, cy + barHalfHeight);
        canvas.drawRoundRect(filledTrack, barHalfHeight, barHalfHeight, fillPaint);
        canvas.drawCircle(x, cy, radius, thumbPaint);

        // Figma의 Lv. 표시처럼 원형 핸들 아래쪽 중앙에 배치함.

        String levelText = "Lv." + level;

        // 레벨 텍스트가 화면 좌우로 잘리지 않도록 위치를 보정함.
        float textHalfWidth = labelPaint.measureText(levelText) / 2f;

        float textX = Math.max(
                textHalfWidth,
                Math.min(x, getWidth() - textHalfWidth)
        );

        canvas.drawText(levelText, textX, dp(42), labelPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            float radius = dp(8);
            float span = Math.max(1, getWidth() - radius * 2);
            float proportion = (event.getX() - radius) / span;
            setLevel(Math.round(Math.max(0f, Math.min(1f, proportion)) * 9f) + 1);
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            performClick();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
