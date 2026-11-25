package com.example.ifme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/** 하나의 콘텐츠 뷰를 (0,0)을 기준으로 확대·축소하고 이동시키는 뷰포트. */
public class ZoomableTreeLayout extends FrameLayout {
    private final ScaleGestureDetector scaleDetector;
    private final int touchSlop;
    private float scale = 1f;
    private float initialX;
    private float initialY;
    private float lastX;
    private float lastY;
    private boolean dragging;

    public ZoomableTreeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setClipChildren(false);
        setClipToPadding(false);
        setClickable(true);

        scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        if (getChildCount() == 0) return false;
                        View content = getChildAt(0);
                        float updated = Math.max(0.15f,
                                Math.min(2.5f, scale * detector.getScaleFactor()));
                        // 두 손가락 중심 아래의 콘텐츠 좌표를 유지하여
                        // 확대·축소할 때 화면이 갑자기 튀지 않게 함.
                        float focusX = detector.getFocusX();
                        float focusY = detector.getFocusY();
                        float contentX = (focusX - content.getTranslationX()) / scale;
                        float contentY = (focusY - content.getTranslationY()) / scale;
                        scale = updated;
                        content.setPivotX(0f);
                        content.setPivotY(0f);
                        content.setScaleX(scale);
                        content.setScaleY(scale);
                        content.setTranslationX(focusX - contentX * scale);
                        content.setTranslationY(focusY - contentY * scale);
                        return true;
                    }
                });
    }

    public float getCurrentScale() {
        return scale;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                initialX = lastX = e.getX();
                initialY = lastY = e.getY();
                dragging = false;
                return false; // 노드와 '+' 버튼이 클릭 이벤트를 받을 수 있도록 함.
            case MotionEvent.ACTION_POINTER_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() > 1) return true;
                if (Math.hypot(e.getX() - initialX, e.getY() - initialY)
                        > touchSlop) {
                    dragging = true;
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleDetector.onTouchEvent(e);
        if (getChildCount() == 0) return true;
        View content = getChildAt(0);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                initialX = lastX = e.getX();
                initialY = lastY = e.getY();
                dragging = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() == 1 && !scaleDetector.isInProgress()) {
                    float dx = e.getX() - lastX;
                    float dy = e.getY() - lastY;
                    if (!dragging && Math.hypot(e.getX() - initialX,
                            e.getY() - initialY) > touchSlop) dragging = true;
                    if (dragging) {
                        content.setTranslationX(content.getTranslationX() + dx);
                        content.setTranslationY(content.getTranslationY() + dy);
                    }
                }
                lastX = e.getX();
                lastY = e.getY();
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                // 손가락 하나를 떼면 남은 손가락을 기준으로 이동 좌표를 다시 설정함.
                if (e.getPointerCount() == 2) {
                    int remainingIndex = e.getActionIndex() == 0 ? 1 : 0;
                    initialX = lastX = e.getX(remainingIndex);
                    initialY = lastY = e.getY(remainingIndex);
                    dragging = false;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                return true;
        }
        return true;
    }
}
