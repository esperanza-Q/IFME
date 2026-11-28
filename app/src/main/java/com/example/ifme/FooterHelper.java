package com.example.ifme;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

public class FooterHelper {

    public static final int TAB_WORLD = 0;
    public static final int TAB_HOME = 1;
    public static final int TAB_MYPAGE = 2;

    public static void setup(Activity activity, int selectedTab) {

        ImageView[] icons = {
                activity.findViewById(R.id.footerWorldIcon),
                activity.findViewById(R.id.footerHomeIcon),
                activity.findViewById(R.id.footerMypageIcon)
        };
        TextView[] texts = {
                activity.findViewById(R.id.footerWorldText),
                activity.findViewById(R.id.footerHomeText),
                activity.findViewById(R.id.footerMypageText)
        };

        int[] onIcons = {
                R.drawable.ic_footer_worlds_selected,
                R.drawable.ic_footer_home_selected,
                R.drawable.ic_footer_mypage_selected
        };
        int[] offIcons = {
                R.drawable.ic_footer_worlds,
                R.drawable.ic_footer_home,
                R.drawable.ic_footer_mypage
        };

        int selectedColor = ContextCompat.getColor(activity, R.color.purple1);
        int normalColor = ContextCompat.getColor(activity, R.color.footer_gray);

        for (int i = 0; i < 3; i++) {
            boolean isSelected = (i == selectedTab);
            icons[i].setImageResource(isSelected ? onIcons[i] : offIcons[i]);
            texts[i].setTextColor(isSelected ? selectedColor : normalColor);
        }
//        setTabClick(activity, R.id.footerWorld, TAB_WORLD, selectedTab, WorldActivity.class);
        setTabClick(activity, R.id.footerHome, TAB_HOME, selectedTab, MainActivity.class);
        setTabClick(activity, R.id.footerMypage, TAB_MYPAGE, selectedTab, MypageActivity.class);
    }

    private static void setTabClick(Activity activity, int viewId, int tab,
                                    int selectedTab, Class<?> target) {
        activity.findViewById(viewId).setOnClickListener(v -> {
            if (activity.getClass() == target) return;

            Intent intent = new Intent(activity, target);
            if (tab == selectedTab) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            activity.startActivity(intent);
        });
    }
}

//사용 방법 - Setcontent 함수 아래에
//FooterHelper.setup(this, FooterHelper.TAB_MYPAGE);