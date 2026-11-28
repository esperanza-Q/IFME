package com.example.ifme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 비대칭 이진트리를 위한 외곽선(Contour) 기반 Tidy Tree 배치 알고리즘.
 * 각 하위 트리의 깊이별 왼쪽/오른쪽 외곽선을 계산함.
 * 양쪽 하위 트리가 모두 있으면 모든 공통 깊이에서 겹치지 않도록
 * 오른쪽 하위 트리를 필요한 최소 거리만큼 이동함.
 * 화면에 표시되는 빈 '+' 버튼도 가상 자식으로 취급하여 겹침 검사에 포함함.
 * 모든 위치는 확대·축소 전 treeContainer의 픽셀 좌표를 기준으로 함.
 */
public final class TreeLayoutManager {
    private TreeLayoutManager() { }

    public static final class Node {
        public final String id;
        public final String title;
        public Node parent;
        public Node left;
        public Node right;

        public Node(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public static final class Point {
        public final float x;
        public final float y;
        public Point(float x, float y) { this.x = x; this.y = y; }
    }

    public static final class Slots {
        public Point left;
        public Point right;
    }

    public static final class Result {
        public final Map<Node, Point> positions = new IdentityHashMap<>();
        public final Map<Node, Slots> slots = new IdentityHashMap<>();
        public final int contentWidth;
        public final int contentHeight;
        public final Point rootPosition;

        Result(int contentWidth, int contentHeight, Point rootPosition) {
            this.contentWidth = contentWidth;
            this.contentHeight = contentHeight;
            this.rootPosition = rootPosition;
        }
    }

    private static final class Item {
        final Node node;          // null이면 가상의 '+' 버튼 위치를 의미함
        final Node slotParent;    // 가상 '+' 버튼일 때만 사용하는 부모 노드
        final boolean leftSlot;
        final float width;
        final List<Float> min = new ArrayList<>();
        final List<Float> max = new ArrayList<>();
        Item left;
        Item right;
        float leftOffset;
        float rightOffset;

        Item(Node node, Node slotParent, boolean leftSlot, float width) {
            this.node = node;
            this.slotParent = slotParent;
            this.leftSlot = leftSlot;
            this.width = width;
        }

        boolean isVirtual() { return node == null; }
    }

    public static Result calculate(
            Node root,
            Set<Node> expandedPairs,
            int viewportWidth,
            int viewportHeight,
            int cardWidth,
            int cardHeight,
            int rootSize,
            int plusSize,
            int horizontalGap,
            int rootTop,
            int rootToFirstGap,
            int nextRowStep,
            int plusOffset,
            int edgePadding
    ) {
        if (root == null) throw new IllegalArgumentException("root is required");
        if (expandedPairs == null) expandedPairs = Collections.emptySet();
        if (cardWidth <= 0 || cardHeight <= 0 || plusSize <= 0 || horizontalGap < 0
                || rootSize <= 0 || nextRowStep < cardHeight + plusOffset + plusSize) {
            throw new IllegalArgumentException("Invalid layout sizes or vertical spacing");
        }

        Item tree = build(root, expandedPairs, cardWidth, rootSize, plusSize, horizontalGap);
        float leftBound = Float.POSITIVE_INFINITY;
        float rightBound = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < tree.min.size(); i++) {
            leftBound = Math.min(leftBound, tree.min.get(i));
            rightBound = Math.max(rightBound, tree.max.get(i));
        }
        // 양쪽에 동일한 여백을 추가함.
        // 화면이 트리보다 넓으면 카드 크기를 바꾸지 않고 루트 노드를 화면 중앙에 배치함.
        float total = rightBound - leftBound + edgePadding * 2f;
        int contentWidth = Math.max(viewportWidth, (int) Math.ceil(total));
        float originX = (contentWidth - (rightBound - leftBound)) / 2f - leftBound;

        Map<Node, Point> positions = new IdentityHashMap<>();
        Map<Node, Slots> slots = new IdentityHashMap<>();
        int[] maxDepth = {0};
        assign(tree, originX, 0, rootTop, rootSize, cardHeight,
                rootToFirstGap, nextRowStep, plusOffset,
                positions, slots, maxDepth);
        // 가장 깊은 행에는 실제 노드나 '+' 버튼이 올 수 있으므로 아래쪽 여백을 확보함.
        int deepestTop = maxDepth[0] == 0 ? rootTop
                : rootTop + rootSize + rootToFirstGap
                + (maxDepth[0] - 1) * nextRowStep;
        int contentHeight = Math.max(viewportHeight,
                deepestTop + Math.max(cardHeight, plusSize) + edgePadding + plusOffset);

        Result result = new Result(
                contentWidth,
                contentHeight,
                positions.get(root)
        );

// 계산된 노드 위치 저장
        result.positions.putAll(positions);

// 계산된 + 버튼 위치 저장
        result.slots.putAll(slots);

        return result;

    }

    private static Item build(Node node, Set<Node> expandedPairs,
                              int cardWidth, int rootSize, int plusSize, int gap) {
        float width = node.parent == null ? rootSize : cardWidth;
        Item current = new Item(node, null, false, width);
        current.min.add(-width / 2f);
        current.max.add(width / 2f);

        // 형제 노드 중 하나를 선택하면 두 형제 모두에 '+' 버튼을 표시함.
        // 이미 자식 노드가 존재하는 자리에는 '+' 버튼 대신 실제 노드를 배치함.
        boolean showSlots = node.parent != null && expandedPairs.contains(node.parent);
        current.left = node.left != null
                ? build(node.left, expandedPairs, cardWidth, rootSize, plusSize, gap)
                : (showSlots ? virtualSlot(node, true, plusSize) : null);
        current.right = node.right != null
                ? build(node.right, expandedPairs, cardWidth, rootSize, plusSize, gap)
                : (showSlots ? virtualSlot(node, false, plusSize) : null);

        if (current.left != null && current.right != null) {
            Item left = current.left;
            Item right = current.right;
            float separation = 0;
            int shared = Math.min(left.min.size(), right.min.size());
            for (int depth = 0; depth < shared; depth++) {
                separation = Math.max(separation,
                        left.max.get(depth) + gap - right.min.get(depth));
            }
            // 양쪽이 모두 빈 경우 '+' 버튼이 카드 중앙에 몰리지 않도록
            // 카드의 왼쪽과 오른쪽 가장자리 근처에 배치함.
            if (left.isVirtual() && right.isVirtual()) {
                separation = Math.max(separation, cardWidth - plusSize);
            }
            current.leftOffset = -separation / 2f;
            current.rightOffset = separation / 2f;
        } else if (current.left != null) {
            // 오른쪽 자식이 없어도 왼쪽 자식의 방향을 유지함.
            current.leftOffset = -(cardWidth + gap) / 2f;
        } else if (current.right != null) {
            current.rightOffset = (cardWidth + gap) / 2f;
        }

        // 외곽선에는 현재 깊이의 실제 노드 또는 버튼과 모든 하위 항목을 포함함.
        appendContours(current, current.left, current.leftOffset);
        appendContours(current, current.right, current.rightOffset);
        return current;
    }

    private static Item virtualSlot(Node parent, boolean left, int plusSize) {
        Item slot = new Item(null, parent, left, plusSize);
        slot.min.add(-plusSize / 2f);
        slot.max.add(plusSize / 2f);
        return slot;
    }

    private static void appendContours(Item parent, Item child, float offset) {
        if (child == null) return;
        for (int i = 0; i < child.min.size(); i++) {
            int atDepth = i + 1;
            float minX = child.min.get(i) + offset;
            float maxX = child.max.get(i) + offset;
            if (atDepth >= parent.min.size()) {
                parent.min.add(minX);
                parent.max.add(maxX);
            } else {
                parent.min.set(atDepth, Math.min(parent.min.get(atDepth), minX));
                parent.max.set(atDepth, Math.max(parent.max.get(atDepth), maxX));
            }
        }
    }

    private static void assign(Item item, float centerX, int depth,
                               int rootTop, int rootSize, int cardHeight,
                               int firstGap, int rowStep, int plusOffset,
                               Map<Node, Point> nodes, Map<Node, Slots> slots,
                               int[] deepest) {
        if (item == null) return;
        deepest[0] = Math.max(deepest[0], depth);
        int rowTop = depth == 0 ? rootTop
                : rootTop + rootSize + firstGap + (depth - 1) * rowStep;
        float top = rowTop;
        if (item.isVirtual()) {
            // 가상 '+' 버튼은 부모 노드 카드의 아래쪽에 배치함.
            int parentTop = depth - 1 == 0 ? rootTop
                    : rootTop + rootSize + firstGap + (depth - 2) * rowStep;
            int parentHeight = depth - 1 == 0 ? rootSize : cardHeight;
            top = parentTop + parentHeight + plusOffset;
            Slots pair = slots.get(item.slotParent);
            if (pair == null) {
                pair = new Slots();
                slots.put(item.slotParent, pair);
            }
            Point point = new Point(centerX - item.width / 2f, top);
            if (item.leftSlot) pair.left = point;
            else pair.right = point;
        } else {
            nodes.put(item.node, new Point(centerX - item.width / 2f, top));
        }
        assign(item.left, centerX + item.leftOffset, depth + 1, rootTop,
                rootSize, cardHeight, firstGap, rowStep, plusOffset,
                nodes, slots, deepest);
        assign(item.right, centerX + item.rightOffset, depth + 1, rootTop,
                rootSize, cardHeight, firstGap, rowStep, plusOffset,
                nodes, slots, deepest);
    }
}
