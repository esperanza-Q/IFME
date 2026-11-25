package com.example.ifme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/** 같은 treeContainer에 직접 배치된 노드와 버튼 사이의 연결선을 그림. */
public class TreeConnectionView extends View {

    public static final class Connection {
        public final View parent;
        public final View child;
        public final boolean selected;
        public final boolean dashed;

        public Connection(View parent, View child, boolean selected, boolean dashed) {
            this.parent = parent;
            this.child = child;
            this.selected = selected;
            this.dashed = dashed;
        }
    }

    private final List<Connection> connections = new ArrayList<>();
    private final Paint regularPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint regularDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TreeConnectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        setup(regularPaint, 0xFF7682B6, false, density);
        setup(selectedPaint, 0xFF9A78F5, false, density);
        setup(regularDashPaint, 0xFF7682B6, true, density);
        setup(selectedDashPaint, 0xFF9A78F5, true, density);
        setClickable(false);
    }

    private void setup(Paint paint, int color, boolean dashed, float density) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1.5f * density);
        if (dashed) {
            paint.setPathEffect(new DashPathEffect(
                    new float[]{6f * density, 7f * density}, 0));
        }
    }

    public void setConnections(List<Connection> newConnections) {
        connections.clear();
        connections.addAll(newConnections);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Connection connection : connections) {
            if (connection.parent == null || connection.child == null
                    || connection.parent.getVisibility() != View.VISIBLE
                    || connection.child.getVisibility() != View.VISIBLE) {
                continue;
            }
            Paint paint = connection.dashed
                    ? (connection.selected ? selectedDashPaint : regularDashPaint)
                    : (connection.selected ? selectedPaint : regularPaint);
            drawConnection(canvas, connection.parent, connection.child, paint);
        }
    }

    private void drawConnection(Canvas canvas, View parent, View child, Paint paint) {
        // 연결선 양 끝점은 같은 부모(treeContainer)를 기준으로 하므로
        // 확대·축소 전의 동일한 좌표계를 사용함. 이동과 확대·축소는 컨테이너 전체에 적용됨.
        float x1 = parent.getLeft() + parent.getWidth() / 2f;
        float y1 = parent.getTop() + parent.getHeight();
        float x2 = child.getLeft() + child.getWidth() / 2f;
        float y2 = child.getTop();
        float middle = (y1 + y2) / 2f;
        Path path = new Path();
        path.moveTo(x1, y1);
        path.cubicTo(x1, middle, x2, middle, x2, y2);
        canvas.drawPath(path, paint);
    }
}
