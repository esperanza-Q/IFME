package com.example.ifme;


import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 트리 화면은 하나의 좌표계를 사용함.
 * 모든 노드 카드, '+' 버튼, 연결선은 확대 전의 treeContainer 안에 배치하고,
 * 확대·축소와 이동은 뷰포트에서 컨테이너 전체에 적용함.
 */
public class TreeActivity extends AppCompatActivity {

    private ZoomableTreeLayout viewport;
    private FrameLayout treeContainer;
    private TreeConnectionView treeConnections;

    private TreeLayoutManager.Node rootNode;
    private TreeLayoutManager.Node selectedNode;
    private TreeLayoutManager.Result lastLayout;

    // 형제 노드 중 하나를 선택하면 두 형제 노드 모두의 '+' 버튼을 표시함.
    // 이 표시 여부는 현재 선택된 노드 정보와 별도로 관리함.
    private final Set<TreeLayoutManager.Node> expandedPairs =
            Collections.newSetFromMap(new IdentityHashMap<TreeLayoutManager.Node, Boolean>());
    private final Map<TreeLayoutManager.Node, View> nodeViews = new IdentityHashMap<>();
    private int newNodeCount = 0;

    private int cardWidth;
    private int cardHeight;
    private int rootSize;
    private int plusSize;


    private TreeLayoutManager.Node pendingParent;
    private boolean pendingIsLeft;

    private final ActivityResultLauncher<Intent> nodeCreateLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK
                                || result.getData() == null) {
                            return;
                        }

                        Intent data = result.getData();
                        String title = data.getStringExtra("nodeTitle");

                        if (pendingParent == null || title == null
                                || title.trim().isEmpty()) {
                            return;
                        }

                        TreeLayoutManager.Node added =
                                addChild(pendingParent, pendingIsLeft, title);

                        if (added != null) {
                            renderTree();
                        }

                        pendingParent = null;
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        viewport = findViewById(R.id.zoomableTree);
        treeContainer = findViewById(R.id.treeContainer);
        treeConnections = findViewById(R.id.treeConnections);

        findViewById(R.id.btnTreeBack).setOnClickListener(v -> finish());
        TextView worldTitle = findViewById(R.id.tvWorldTitle);
        String suppliedWorldTitle = getIntent().getStringExtra("worldTitle");
        worldTitle.setText(suppliedWorldTitle == null ? "세계 제목" : suppliedWorldTitle);

        // 임시 세계 데이터이며, 추후 다른 팀원이 구현한 화면의 데이터로 대체할 예정.
        String first = getIntent().getStringExtra("choice1");
        String second = getIntent().getStringExtra("choice2");
        if (first == null) first = "취업하기";
        if (second == null) second = "대학원 진학하기";
        rootNode = new TreeLayoutManager.Node("root", "me");
        addChild(rootNode, true, first);
        addChild(rootNode, false, second);

        viewport.post(this::renderTree);
    }

    private TreeLayoutManager.Node addChild(
            TreeLayoutManager.Node parent, boolean isLeft, String title) {
        if (parent == null || (isLeft ? parent.left != null : parent.right != null)) {
            return null;
        }
        TreeLayoutManager.Node child = new TreeLayoutManager.Node(
                UUID.randomUUID().toString(), title);
        child.parent = parent;
        if (isLeft) parent.left = child;
        else parent.right = child;
        return child;
    }
//다른 팀원 담당 화면을 대체하는 임시팝업 입니다!!
    private void showNodeSummary(TreeLayoutManager.Node node) {
        new AlertDialog.Builder(this)
                .setTitle(node.title)
                .setMessage("노드 요약 카드 화면을 대신하는 테스트입니다.")
                .setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("선택하기", (dialog, which) -> {
                    selectedNode = node;
                    if (node.parent != null) expandedPairs.add(node.parent);
                    renderTree();
                })
                .show();
    }

    private void createTestNode(TreeLayoutManager.Node parent, boolean isLeft) {
        if (isLeft ? parent.left != null : parent.right != null) return;
        TreeLayoutManager.Node added = addChild(parent, isLeft,
                "새 선택지 " + (newNodeCount + 1));
        if (added != null) {
            newNodeCount++;
            // 추후 실제 NodeCreateActivity에서 이름과 상세 정보를 전달받도록 변경할 예정.
            renderTree();
        }
    }


    private void openNodeCreate(TreeLayoutManager.Node parent, boolean isLeft) {
        if (parent == null) return;

        // 이미 하위 노드가 존재하면 생성 화면을 열지 않음.
        if (isLeft ? parent.left != null : parent.right != null) return;

        // 생성할 노드의 부모와 방향을 기억함.
        pendingParent = parent;
        pendingIsLeft = isLeft;

        Intent intent = new Intent(this, NodeCreateActivity.class);
        intent.putExtra("parentNodeTitle", parent.title);
        intent.putExtra("parentNodeId", parent.id);

        nodeCreateLauncher.launch(intent);
    }


    private void renderTree() {
        if (viewport.getWidth() <= 0 || viewport.getHeight() <= 0) return;

        float scale = viewport.getCurrentScale();
        float previousRootScreenX;
        float previousRootScreenY;
        if (lastLayout != null && lastLayout.rootPosition != null) {
            previousRootScreenX = treeContainer.getTranslationX()
                    + scale * (lastLayout.rootPosition.x + rootSize / 2f);
            previousRootScreenY = treeContainer.getTranslationY()
                    + scale * (lastLayout.rootPosition.y + rootSize / 2f);
        } else {
            previousRootScreenX = viewport.getWidth() / 2f;
            previousRootScreenY = dp(100) + dp(82) / 2f;
        }

        int vw = viewport.getWidth();
        // 최초 화면의 간격을 유지하도록 카드 크기를 뷰포트 너비 기준으로 계산함.
        // 트리 콘텐츠 영역이 넓어지더라도 카드 자체의 너비는 늘어나지 않음.
        cardWidth = Math.max(dp(100), (vw - dp(72)) / 2);
        cardHeight = dp(93);
        rootSize = dp(82);
        plusSize = dp(46);

        TreeLayoutManager.Result layout = TreeLayoutManager.calculate(
                rootNode, expandedPairs,
                vw, viewport.getHeight(), cardWidth, cardHeight,
                rootSize, plusSize,
                dp(24), dp(100), dp(45), dp(245), dp(46), dp(24));

        // 최종 콘텐츠 좌표에 맞춰 노드 뷰를 다시 생성함.
        // 배치 후 setX()/setY()로 이동하지 않아 좌측 상단에 잠깐 나타나는 현상을 방지함.
        treeContainer.removeAllViews();
        nodeViews.clear();
        FrameLayout.LayoutParams linesParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        treeContainer.addView(treeConnections, linesParams);

        List<TreeLayoutManager.Node> nodes = new ArrayList<>();
        collectNodes(rootNode, nodes);
        for (TreeLayoutManager.Node node : nodes) {
            TreeLayoutManager.Point p = layout.positions.get(node);
            if (p == null) continue;
            View view = buildNodeView(node);
            int width = node == rootNode ? rootSize : cardWidth;
            int height = node == rootNode ? rootSize : cardHeight;
            place(view, Math.round(p.x), Math.round(p.y), width, height);
            nodeViews.put(node, view);
        }

        List<TreeConnectionView.Connection> edges = new ArrayList<>();
        for (TreeLayoutManager.Node node : nodes) {
            if (node.parent == null) continue;
            edges.add(new TreeConnectionView.Connection(
                    nodeViews.get(node.parent), nodeViews.get(node),
                    isOnSelectedPath(node), false));
        }

        // '+' 버튼 위치도 노드와 동일한 외곽선 계산 결과를 사용함.
        // 따라서 같은 행에서 버튼과 카드가 겹치지 않도록 배치함.
        for (TreeLayoutManager.Node node : nodes) {
            TreeLayoutManager.Slots pair = layout.slots.get(node);
            if (pair == null) continue;
            if (pair.left != null) addPlus(node, true, pair.left, edges);
            if (pair.right != null) addPlus(node, false, pair.right, edges);
        }

        FrameLayout.LayoutParams containerParams =
                (FrameLayout.LayoutParams) treeContainer.getLayoutParams();
        containerParams.width = layout.contentWidth;
        containerParams.height = layout.contentHeight;
        treeContainer.setLayoutParams(containerParams);

        // ZoomableTreeLayout의 확대 기준점은 (0,0)임.
        // 외곽선 배치로 콘텐츠 크기가 바뀌어도 루트 노드의 기존 화면 위치를 유지함.
        treeContainer.setTranslationX(previousRootScreenX
                - scale * (layout.rootPosition.x + rootSize / 2f));
        treeContainer.setTranslationY(previousRootScreenY
                - scale * (layout.rootPosition.y + rootSize / 2f));

        lastLayout = layout;
        treeConnections.setConnections(edges);
        treeConnections.invalidate();
    }

    private View buildNodeView(TreeLayoutManager.Node node) {
        if (node == rootNode) {
            TextView root = new TextView(this);
            root.setText("me");
            root.setTextColor(Color.WHITE);
            root.setTextSize(18);
            root.setTypeface(null, Typeface.BOLD);
            root.setGravity(Gravity.CENTER);
            root.setBackgroundResource(R.drawable.bg_tree_me);
            setOutlineSelected(root, selectedNode != null);
            return root;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundResource(R.drawable.bg_tree_node);
        card.setClickable(true);
        card.setFocusable(true);
        setOutlineSelected(card, isOnSelectedPath(node));
        boolean dimmed = selectedNode != null && !isOnSelectedPath(node);
        int nodeTextColor = Color.parseColor(dimmed ? "#D1D1D1" : "#FFFFFF");

        TextView label = new TextView(this);
        label.setText(node.parent.left == node ? "선택지 1" : "선택지 2");
        label.setTextColor(nodeTextColor);
        label.setTextSize(14);
        label.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText(node.title);
        title.setTextColor(nodeTextColor);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setMaxLines(2);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(8);
        card.addView(label);
        card.addView(title, titleParams);
        card.setOnClickListener(v -> showNodeSummary(node));
        return card;
    }

    private void addPlus(TreeLayoutManager.Node parent, boolean isLeft,
                         TreeLayoutManager.Point slotPosition,
                         List<TreeConnectionView.Connection> edges) {
        TextView plus = new TextView(this);
        plus.setText("+");
        plus.setTextSize(28);
        plus.setTextColor(Color.WHITE);
        plus.setGravity(Gravity.CENTER);
        plus.setBackgroundResource(R.drawable.bg_tree_plus);
        plus.setContentDescription(isLeft ? "왼쪽 하위 노드 추가" : "오른쪽 하위 노드 추가");
        plus.setClickable(true);
        plus.setFocusable(true);
        place(plus, Math.round(slotPosition.x), Math.round(slotPosition.y),
                plusSize, plusSize);
        plus.setOnClickListener(v -> openNodeCreate(parent, isLeft));
        edges.add(new TreeConnectionView.Connection(
                nodeViews.get(parent), plus, isOnSelectedPath(parent), true));
    }

    private void place(View view, int x, int y, int width, int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = x;
        params.topMargin = y;
        treeContainer.addView(view, params);
    }

    private boolean isOnSelectedPath(TreeLayoutManager.Node node) {
        for (TreeLayoutManager.Node cursor = selectedNode;
             cursor != null; cursor = cursor.parent) {
            if (cursor == node) return true;
        }
        return false;
    }

    private void collectNodes(TreeLayoutManager.Node node,
                              List<TreeLayoutManager.Node> list) {
        if (node == null) return;
        list.add(node);
        collectNodes(node.left, list);
        collectNodes(node.right, list);
    }


    private void setOutlineSelected(View view, boolean selected) {
        Drawable background = view.getBackground();

        if (!(background instanceof GradientDrawable)) {
            return;
        }

        Drawable.ConstantState state = background.getConstantState();

        if (state == null) {
            return;
        }

        GradientDrawable unique =
                (GradientDrawable) state.newDrawable().mutate();

        unique.setStroke(
                dp(2),
                Color.parseColor(selected ? "#A77CFF" : "#7682B6")
        );

        view.setBackground(unique);
    }


    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
