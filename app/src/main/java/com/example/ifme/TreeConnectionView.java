
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

public class TreeConnectionView extends View {

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dottedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // 모든 연결선을 저장하는 리스트
    private final List<Connection> connections = new ArrayList<>();

    private View selectedNode;
    private View plusButton1;
    private View plusButton2;


    // 미선택 노드의 + 버튼
    private View otherPlusButton1;
    private View otherPlusButton2;

    // 미선택 경로의 점선
    private final Paint normalDottedPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);


    // 부모와 자식 사이의 연결 정보
    public static class Connection {
        View parent;
        View child;
        boolean selected;

        public Connection(View parent, View child, boolean selected) {
            this.parent = parent;
            this.child = child;
            this.selected = selected;
        }
    }

    public TreeConnectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = getResources().getDisplayMetrics().density;

        linePaint.setColor(0xFF7682B6);
        linePaint.setStrokeWidth(1.5f * density);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        selectedLinePaint.setColor(0xFF9A78F5);
        selectedLinePaint.setStrokeWidth(1.5f * density);
        selectedLinePaint.setStyle(Paint.Style.STROKE);
        selectedLinePaint.setStrokeCap(Paint.Cap.ROUND);

        dottedPaint.setColor(0xFF9A78F5);
        dottedPaint.setStrokeWidth(1.5f * density);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);
        dottedPaint.setPathEffect(
                new DashPathEffect(
                        new float[]{6f * density, 7f * density}, 0
                )
        );

        // 미선택 노드의 파란색 점선
        normalDottedPaint.setColor(0xFF7682B6);
        normalDottedPaint.setStrokeWidth(1.5f * density);
        normalDottedPaint.setStyle(Paint.Style.STROKE);
        normalDottedPaint.setStrokeCap(Paint.Cap.ROUND);

        normalDottedPaint.setPathEffect(
                new DashPathEffect(
                        new float[]{6f * density, 7f * density}, 0
                )
        );


    }

    // 여러 노드의 연결선을 한 번에 설정
    public void setConnections(List<Connection> newConnections) {
        connections.clear();
        connections.addAll(newConnections);
        invalidate();
    }

    // 기존 TreeActivity와 호환되는 메서드
    public void setNodes(View parent, View first, View second) {
        connections.clear();

        connections.add(new Connection(parent, first, false));
        connections.add(new Connection(parent, second, false));

        invalidate();
    }

    // 선택된 노드와 + 버튼 설정
    public void setSelectedNode(
            View selected,
            View firstPlus,
            View secondPlus
    ) {
        selectedNode = selected;
        plusButton1 = firstPlus;
        plusButton2 = secondPlus;

        // 기존 연결선의 선택 상태 갱신
        for (Connection connection : connections) {
            connection.selected = connection.child == selected;
        }

        invalidate();
    }

    // 미선택 노드의 + 버튼 등록
    public void setOtherPlusButtons(
            View firstPlus,
            View secondPlus
    ) {
        otherPlusButton1 = firstPlus;
        otherPlusButton2 = secondPlus;

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 저장된 모든 연결선 그리기
        for (Connection connection : connections) {

            if (connection.parent == null || connection.child == null) {
                continue;
            }

            Paint paint = connection.selected
                    ? selectedLinePaint
                    : linePaint;

            drawConnection(
                    canvas,
                    connection.parent,
                    connection.child,
                    paint
            );
        }

        // 선택된 노드 아래의 + 연결선
        if (selectedNode != null) {

            if (plusButton1 != null
                    && plusButton1.getVisibility() == View.VISIBLE) {
                drawConnection(
                        canvas, selectedNode, plusButton1, dottedPaint
                );
            }

            if (plusButton2 != null
                    && plusButton2.getVisibility() == View.VISIBLE) {
                drawConnection(
                        canvas, selectedNode, plusButton2, dottedPaint
                );
            }
        }

        // 미선택 노드의 + 버튼도 파란색 점선으로 연결
        if (selectedNode != null) {

            View otherNode = null;

            for (Connection connection : connections) {
                if (connection.child != selectedNode) {
                    otherNode = connection.child;
                    break;
                }
            }

            if (otherNode != null) {

                if (otherPlusButton1 != null
                        && otherPlusButton1.getVisibility() == View.VISIBLE) {
                    drawConnection(
                            canvas,
                            otherNode,
                            otherPlusButton1,
                            normalDottedPaint
                    );
                }

                if (otherPlusButton2 != null
                        && otherPlusButton2.getVisibility() == View.VISIBLE) {
                    drawConnection(
                            canvas,
                            otherNode,
                            otherPlusButton2,
                            normalDottedPaint
                    );
                }
            }
        }

    }

    private void drawConnection(
            Canvas canvas,
            View startNode,
            View endNode,
            Paint paint
    ) {
        // 연결선 View 기준으로 좌표 계산
        int[] lineLocation = new int[2];
        int[] startLocation = new int[2];
        int[] endLocation = new int[2];

        getLocationInWindow(lineLocation);
        startNode.getLocationInWindow(startLocation);
        endNode.getLocationInWindow(endLocation);

        float startX = startLocation[0] - lineLocation[0]
                + startNode.getWidth() / 2f;

        float startY = startLocation[1] - lineLocation[1]
                + startNode.getHeight();

        float endX = endLocation[0] - lineLocation[0]
                + endNode.getWidth() / 2f;

        float endY = endLocation[1] - lineLocation[1];

        float middleY = (startY + endY) / 2f;

        Path path = new Path();
        path.moveTo(startX, startY);

        path.cubicTo(
                startX, middleY,
                endX, middleY,
                endX, endY
        );

        canvas.drawPath(path, paint);
    }
}
