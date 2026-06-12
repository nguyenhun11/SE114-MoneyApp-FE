package com.example.moneyapp.view;

import android.content.Context;
import android.view.Menu;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainUIHandler {

    private final Context context;
    private final NavController navController;
    private final BottomNavigationView bottomNav;
    private final AppCompatImageButton fabAdd;

    // THÊM R.id.moreFragment vào đây để BottomNav giữ nguyên khi mở màn hình Khác
    private final Set<Integer> mainFragments = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.transactionFragment,
            R.id.accountFragment,
            R.id.categoryFragment,
            R.id.profileFragment
    ));

    public MainUIHandler(Context context, NavController navController, BottomNavigationView bottomNav, AppCompatImageButton fabAdd) {
        this.context = context;
        this.navController = navController;
        this.bottomNav = bottomNav;
        this.fabAdd = fabAdd;
        setupNavigationListener();
    }

    private void setupNavigationListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();

            setBottomNavigationVisibility(mainFragments.contains(destId));

            if (!mainFragments.contains(destId)) {
                unselectAllMenuItems();
            } else {
                bottomNav.getMenu().setGroupCheckable(0, true, true);
            }
        });
    }

    public void updateFAB(String iconName, View.OnClickListener listener) {
        if (fabAdd != null) {
            Context context = fabAdd.getContext();
            if (iconName != null && !iconName.isEmpty()) {
                com.mikepenz.iconics.IconicsDrawable drawable = new com.mikepenz.iconics.IconicsDrawable(context, iconName);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorOnPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                fabAdd.setImageDrawable(drawable);
            }
            fabAdd.setOnClickListener(listener);
        }
    }

    public void hideFAB() {
        if (fabAdd != null) {
            fabAdd.setVisibility(View.GONE);
        }
    }

    public void setBottomNavigationVisibility(boolean visible) {
        if (bottomNav != null) {
            bottomNav.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setFABVisibility(boolean visible) {
        if (fabAdd != null) {
            fabAdd.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void unselectAllMenuItems() {
        if (bottomNav == null) return;
        Menu menu = bottomNav.getMenu();
        menu.setGroupCheckable(0, true, false);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.setGroupCheckable(0, true, true);
    }

    // ĐÃ XÓA TOÀN BỘ CÁC HÀM SHOW POPUP LÀM RỐI CODE
}