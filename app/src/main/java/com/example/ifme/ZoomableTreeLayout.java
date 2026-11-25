
package com.example.ifme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class ZoomableTreeLayout extends FrameLayout {

    private final ScaleGestureDetector scaleDetector;

    private float scale = 1.0f;
    private float lastX;
    private float lastY;

    private boolean dragging = false;
    private final int touchSlop;

    public ZoomableTreeLayout(Context context,
                              AttributeSet attrs) {
        super(context, attrs);

        touchSlop = ViewConfiguration.get(context)
                .getScaledTouchSlop();

        scaleDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(
                            ScaleGestureDetector detector) {

                        scale *= detector.getScaleFactor();

                        // 최소 50%, 최대 250%
                        scale = Math.max(0.5f,
                                Math.min(scale, 2.5f));

                        updateScale();
                        return true;
                    }
                }
        );

        setClipChildren(false);
        setClipToPadding(false);
    }

    private void updateScale() {
        if (getChildCount() == 0) return;

        View tree = getChildAt(0);

        tree.setPivotX(tree.getWidth() / 2f);
        tree.setPivotY(tree.getHeight() / 2f);

        tree.setScaleX(scale);
        tree.setScaleY(scale);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                dragging = false;
                return false;

            case MotionEvent.ACTION_POINTER_DOWN:
                return true;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    return true;
                }

                float dx = event.getX() - lastX;
                float dy = event.getY() - lastY;

                if (Math.hypot(dx, dy) > touchSlop) {
                    dragging = true;
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleDetector.onTouchEvent(event);

        if (getChildCount() == 0) return true;

        View tree = getChildAt(0);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1
                        && !scaleDetector.isInProgress()) {

                    float dx = event.getX() - lastX;
                    float dy = event.getY() - lastY;

                    //빈 공간에서도 드래그 시작 감지
                    if (!dragging && Math.hypot(dx, dy) > touchSlop) {
                        dragging = true;
                    }


                    if (dragging) {
                        tree.setTranslationX(
                                tree.getTranslationX() + dx
                        );

                        tree.setTranslationY(
                                tree.getTranslationY() + dy
                        );
                    }
                }

                lastX = event.getX();
                lastY = event.getY();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                dragging = false;
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                return true;
        }

        return true;
    }
}
