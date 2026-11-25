
package com.example.ifme;

import android.content.Intent;

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

        treeConnections.post(() -> {
            treeConnections.setNodes(
                    nodeMe,
                    nodeChoice1,
                    nodeChoice2
            );
        });

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



        // 선택지 1의 + 버튼
        View.OnClickListener addToChoice1 = v -> {
            openNodeCreate("취업하기", 1);
        };

        findViewById(R.id.btnPlus1Left)
                .setOnClickListener(addToChoice1);
        findViewById(R.id.btnPlus1Right)
                .setOnClickListener(addToChoice1);

        // 선택지 2의 + 버튼
        View.OnClickListener addToChoice2 = v -> {
            openNodeCreate("대학원 진학하기", 2);
        };

        findViewById(R.id.btnPlus2Left)
                .setOnClickListener(addToChoice2);
        findViewById(R.id.btnPlus2Right)
                .setOnClickListener(addToChoice2);


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

    private TreeNode addChild(
            TreeNode parent,
            boolean isLeft,
            String title
    ) {
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




}
