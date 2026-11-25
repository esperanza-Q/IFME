package com.example.ifme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;

public class NodeCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_node_create);

        String parentTitle = getIntent()
                .getStringExtra("parentNodeTitle");

        int parentId = getIntent()
                .getIntExtra("parentNodeId", -1);

        /*TextView tvParentNodeTitle =
                findViewById(R.id.tvParentNodeTitle);

        if (parentTitle != null) {
            tvParentNodeTitle.setText(parentTitle);
        }
        */


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}