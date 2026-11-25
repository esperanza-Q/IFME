
package com.example.ifme;

import android.graphics.PointF;
import java.util.HashMap;
import java.util.Map;


import android.content.Intent;


import android.view.Gravity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;


import androidx.appcompat.app.AppCompatActivity;

public class TreeActivity extends AppCompatActivity {

    private TreeConnectionView treeConnections;

    private View nodeChoice1;
    private View nodeChoice2;

    private LinearLayout plusGroup1;
    private LinearLayout plusGroup2;


    private TreeNode rootNode;
    private TreeNode choice1Node;
    private TreeNode choice2Node;


    private ConstraintLayout treeContainer;

    private final List<TreeConnectionView.Connection> connections =
            new ArrayList<>();

    private int newNodeCount = 0;


    // 트리 데이터와 화면의 노드를 연결
    private final Map<TreeNode, View> nodeViews = new HashMap<>();

    // 초기 화면 중앙 정렬 여부
    private boolean treeCentered = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);


        rootNode = new TreeNode("root", "me");

        choice1Node = addChild(rootNode, true, "취업하기");
        choice2Node = addChild(rootNode, false, "대학원 진학하기");


        // 1. 노드 연결
        View nodeMe = findViewById(R.id.nodeMe);

        nodeChoice1 = findViewById(R.id.nodeChoice1);
        nodeChoice2 = findViewById(R.id.nodeChoice2);

        TextView tvChoice1 = findViewById(R.id.tvChoice1);
        TextView tvChoice2 = findViewById(R.id.tvChoice2);

        tvChoice1.setText(choice1Node.title);
        tvChoice2.setText(choice2Node.title);


        treeConnections = findViewById(R.id.treeConnections);

        treeContainer = findViewById(R.id.treeContainer);


        nodeViews.put(rootNode, nodeMe);
        nodeViews.put(choice1Node, nodeChoice1);
        nodeViews.put(choice2Node, nodeChoice2);



        connections.add(new TreeConnectionView.Connection(
                nodeMe, nodeChoice1, false
        ));

        connections.add(new TreeConnectionView.Connection(
                nodeMe, nodeChoice2, false
        ));


        treeConnections.post(() ->
                treeConnections.setConnections(connections)
        );


        // 2. 하위 노드 추가 버튼 그룹
        plusGroup1 = findViewById(R.id.plusGroup1);
        plusGroup2 = findViewById(R.id.plusGroup2);

        View plus1Left = findViewById(R.id.btnPlus1Left);
        View plus1Right = findViewById(R.id.btnPlus1Right);

        View plus2Left = findViewById(R.id.btnPlus2Left);
        View plus2Right = findViewById(R.id.btnPlus2Right);


        // 처음에는 + 버튼 숨기기
        plusGroup1.setVisibility(View.GONE);
        plusGroup2.setVisibility(View.GONE);


        // 선택지 클릭 → 노드 요약 확인
        nodeChoice1.setOnClickListener(v -> {
            showNodeSummary(choice1Node.title, 1);
        });

        nodeChoice2.setOnClickListener(v -> {
            showNodeSummary(choice2Node.title, 2);
        });


        // 취업하기 - 왼쪽 하위 노드
        plus1Left.setOnClickListener(v ->
                createTestNode(choice1Node, true, nodeChoice1, plus1Left)
        );

        // 취업하기 - 오른쪽 하위 노드
        plus1Right.setOnClickListener(v ->
                createTestNode(choice1Node, false, nodeChoice1, plus1Right)
        );

        // 대학원 진학하기 - 왼쪽 하위 노드
        plus2Left.setOnClickListener(v ->
                createTestNode(choice2Node, true, nodeChoice2, plus2Left)
        );

        // 대학원 진학하기 - 오른쪽 하위 노드
        plus2Right.setOnClickListener(v ->
                createTestNode(choice2Node, false, nodeChoice2, plus2Right)
        );



    }


        // 임시 노드 요약 확인창
    private void showNodeSummary(String title, int choiceNumber) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("노드 요약 카드 화면을 대신하는 테스트입니다.")
                .setNegativeButton("닫기", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("선택하기", (dialog, which) -> {
                    confirmNodeSelection(choiceNumber);
                })
                .show();
    }


    // 선택하기를 눌렀을 때 트리에 선택 상태 반영
    private void confirmNodeSelection(int choiceNumber) {

        View nodeMe = findViewById(R.id.nodeMe);

        View plus1Left = findViewById(R.id.btnPlus1Left);
        View plus1Right = findViewById(R.id.btnPlus1Right);

        View plus2Left = findViewById(R.id.btnPlus2Left);
        View plus2Right = findViewById(R.id.btnPlus2Right);

        // 선택 여부와 관계없이 양쪽 + 버튼 표시
        plusGroup1.setVisibility(View.VISIBLE);
        plusGroup2.setVisibility(View.VISIBLE);

        // 선택한 노드에 따라 테두리 색상 변경
        setNodeSelected(nodeMe, true);

        boolean firstSelected = choiceNumber == 1;

        setNodeSelected(nodeChoice1, firstSelected);
        setNodeSelected(nodeChoice2, !firstSelected);

        // 선택한 경로와 양쪽 + 연결선 갱신
        plusGroup1.post(() -> {

            if (firstSelected) {

                treeConnections.setSelectedNode(
                        nodeChoice1,
                        plus1Left,
                        plus1Right
                );

                treeConnections.setOtherPlusButtons(
                        plus2Left,
                        plus2Right
                );

            } else {

                treeConnections.setSelectedNode(
                        nodeChoice2,
                        plus2Left,
                        plus2Right
                );

                treeConnections.setOtherPlusButtons(
                        plus1Left,
                        plus1Right
                );
            }
        });
    }


    //선택한 부모 노드의 제목과 ID를 하위 노드 생성 화면으로 전달
    private void openNodeCreate(String parentTitle, int parentId) {

        Intent intent = new Intent(
                TreeActivity.this,
                NodeCreateActivity.class
        );

        intent.putExtra("parentNodeTitle", parentTitle);
        intent.putExtra("parentNodeId", parentId);

        startActivity(intent);
    }
    //테두리 색상 변경

    private void setNodeSelected(View node, boolean selected) {

        GradientDrawable background =
                (GradientDrawable) node.getBackground()
                        .getConstantState()
                        .newDrawable()
                        .mutate();

        int color = Color.parseColor(
                selected ? "#9A78F5" : "#7682B6"
        );

        int strokeWidth = (int) (1.5f *
                getResources().getDisplayMetrics().density);

        background.setStroke(strokeWidth, color);

        node.setBackground(background);
    }


    private static class TreeNode {
        String id;
        String title;

        TreeNode parent;
        TreeNode left;
        TreeNode right;

        TreeNode(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }


    private void arrangeTree() {

        int maxDepth = getMaxDepth(rootNode);

        // 초기 트리는 기존 XML 디자인 유지
        if (maxDepth < 2) return;

        int nodeWidth = dp(120);
        int horizontalGap = dp(24);
        int verticalGap = dp(200);
        int topMargin = dp(100);

        // 실제 트리 깊이에 따라 너비 계산
        int treeWidth = TreeLayoutManager.calculateWidth(
                maxDepth,
                nodeWidth,
                horizontalGap
        );

        // 화면 너비보다 작아지지 않도록 설정
        View zoomableTree = findViewById(R.id.zoomableTree);

        treeWidth = Math.max(
                treeWidth,
                zoomableTree.getWidth()
        );

        // 트리 깊이에 따라 높이도 확장
        int treeHeight = Math.max(
                zoomableTree.getHeight(),
                topMargin + maxDepth * verticalGap + dp(180)
        );

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams)
                        treeContainer.getLayoutParams();

        params.width = treeWidth;
        params.height = treeHeight;

        treeContainer.setLayoutParams(params);

        final int finalTreeWidth = treeWidth;

        treeContainer.post(() -> {

            // 모든 노드 위치 다시 계산
            positionTreeNode(
                    rootNode,
                    0,
                    0,
                    maxDepth,
                    nodeWidth,
                    horizontalGap,
                    verticalGap,
                    topMargin
            );

            // 초기 두 선택지의 + 버튼도 함께 이동
            positionPlusGroup(nodeChoice1, plusGroup1);
            positionPlusGroup(nodeChoice2, plusGroup2);

            // 자동 배치를 처음 적용할 때만 가운데 정렬
            if (!treeCentered) {

                treeContainer.setTranslationX(
                        (zoomableTree.getWidth()
                                - finalTreeWidth) / 2f
                );

                treeCentered = true;
            }

            // 변경된 위치 기준으로 연결선 다시 그리기
            treeConnections.invalidate();
        });
    }


    private void positionTreeNode(
            TreeNode node,
            int depth,
            int index,
            int maxDepth,
            int nodeWidth,
            int horizontalGap,
            int verticalGap,
            int topMargin
    ) {
        if (node == null) return;

        View nodeView = nodeViews.get(node);

        if (nodeView != null) {

            PointF position = TreeLayoutManager.calculatePosition(
                    depth,
                    index,
                    maxDepth,
                    nodeWidth,
                    horizontalGap,
                    verticalGap,
                    topMargin
            );

            // 노드마다 실제 너비가 달라도 중앙 정렬
            float x = position.x
                    + (nodeWidth - nodeView.getWidth()) / 2f;

            nodeView.setX(x);
            nodeView.setY(position.y);
        }

        // 왼쪽 자식은 index * 2
        positionTreeNode(
                node.left,
                depth + 1,
                index * 2,
                maxDepth,
                nodeWidth,
                horizontalGap,
                verticalGap,
                topMargin
        );

        // 오른쪽 자식은 index * 2 + 1
        positionTreeNode(
                node.right,
                depth + 1,
                index * 2 + 1,
                maxDepth,
                nodeWidth,
                horizontalGap,
                verticalGap,
                topMargin
        );
    }

    private void positionPlusGroup(
            View parentNode,
            LinearLayout plusGroup
    ) {
        plusGroup.setX(
                parentNode.getX()
                        + (parentNode.getWidth()
                        - plusGroup.getWidth()) / 2f
        );

        plusGroup.setY(
                parentNode.getY()
                        + parentNode.getHeight()
                        + dp(46)
        );
    }


    private void createTestNode(
                TreeNode parent,
        boolean isLeft,
        View parentView,
        View plusButton) {
            // 이미 자식이 있는 자리라면 추가하지 않음
            if (isLeft && parent.left != null) return;
            if (!isLeft && parent.right != null) return;

            newNodeCount++;
            String title = "새 선택지 " + newNodeCount;

            TreeNode child = addChild(parent, isLeft, title);
            if (child == null) return;

            // 노드 화면 생성
            LinearLayout childView = new LinearLayout(this);
            childView.setVisibility(View.INVISIBLE);
            childView.setOrientation(LinearLayout.VERTICAL);
            childView.setGravity(Gravity.CENTER);
            childView.setBackgroundResource(R.drawable.bg_tree_node);

            TextView titleView = new TextView(this);
            titleView.setText(child.title);
            titleView.setTextColor(Color.WHITE);
            titleView.setTextSize(16);
            titleView.setGravity(Gravity.CENTER);

            childView.addView(titleView);

            int width = dp(120);
            int height = dp(93);

            ConstraintLayout.LayoutParams params =
                    new ConstraintLayout.LayoutParams(width, height);

            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

            treeContainer.addView(childView, params);

            nodeViews.put(child, childView);


            childView.post(() -> {

                int[] plusLocation = new int[2];
                int[] containerLocation = new int[2];

                plusButton.getLocationInWindow(plusLocation);
                treeContainer.getLocationInWindow(containerLocation);

                float x = plusLocation[0] - containerLocation[0]
                        + plusButton.getWidth() / 2f
                        - childView.getWidth() / 2f;

                float y = plusLocation[1] - containerLocation[1]
                        + plusButton.getHeight()
                        + dp(20);

                childView.setX(x);
                childView.setY(y);

                // 위치 계산이 완료되면 노드를 다시 표시
                childView.setVisibility(View.VISIBLE);

                // 노드가 보이는 상태에서 연결선 갱신
                treeConnections.invalidate();
            });






        // 사용한 + 버튼은 새 노드로 대체
            plusButton.setVisibility(View.INVISIBLE);

            // 부모-자식 연결
            connections.add(new TreeConnectionView.Connection(
                    parentView,
                    childView,
                    false
            ));

            treeConnections.setConnections(connections);

        }

// dp를 픽셀로 변환
        private int dp(float value) {
            return (int) (
                    value * getResources().getDisplayMetrics().density + 0.5f
            );
        }


        private TreeNode addChild(
            TreeNode parent,
            boolean isLeft,
            String title) {
        if (parent == null) return null;

        // 이미 노드가 있는 자리는 추가 불가
        if (isLeft && parent.left != null) return null;
        if (!isLeft && parent.right != null) return null;

        TreeNode child = new TreeNode(
                java.util.UUID.randomUUID().toString(),
                title
        );

        child.parent = parent;

        if (isLeft) {
            parent.left = child;
        } else {
            parent.right = child;
        }

        return child;
    }

    private int getMaxDepth(TreeNode node) {
        if (node == null) {
            return -1;
        }

        int leftDepth = getMaxDepth(node.left);
        int rightDepth = getMaxDepth(node.right);

        return 1 + Math.max(leftDepth, rightDepth);
    }





}
