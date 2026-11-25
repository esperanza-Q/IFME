
package com.example.ifme;

import android.graphics.PointF;

public class TreeLayoutManager {

    // 트리의 전체 너비 계산
    public static int calculateWidth(
            int maxDepth,
            int nodeWidth,
            int horizontalGap
    ) {
        int leafCount = 1 << Math.min(maxDepth, 12);

        return leafCount * nodeWidth
                + (leafCount - 1) * horizontalGap;
    }

    // 노드의 위치 계산
    public static PointF calculatePosition(
            int depth,
            int index,
            int maxDepth,
            int nodeWidth,
            int horizontalGap,
            int verticalGap,
            int topMargin
    ) {
        int leafCount = 1 << Math.min(maxDepth, 12);

        float slotWidth = nodeWidth + horizontalGap;

        // 현재 깊이에서 한 노드가 담당하는 공간
        float span = (float) leafCount / (1 << depth);

        // 담당 공간의 중앙
        float centerX = (index + 0.5f) * span * slotWidth
                - horizontalGap / 2f;

        float x = centerX - nodeWidth / 2f;
        float y = topMargin + depth * verticalGap;

        return new PointF(x, y);
    }

    // 부모 노드 아래 + 버튼의 위치 계산
    public static PointF calculatePlusPosition(
            float parentX,
            float parentY,
            int parentWidth,
            int parentHeight,
            int plusWidth,
            boolean isLeft,
            int verticalGap
    ) {
        float centerX = parentX + parentWidth / 2f;

        float offset = parentWidth / 4f;

        float x = isLeft
                ? centerX - offset - plusWidth / 2f
                : centerX + offset - plusWidth / 2f;

        float y = parentY + parentHeight + verticalGap;

        return new PointF(x, y);
    }
}
