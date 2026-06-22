package com.example.moneyapp.view;

import android.content.Context;
import android.graphics.drawable.InsetDrawable;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainUIHandler {

    private final NavController navController;
    private final BottomNavigationView bottomNav;
    private final AppCompatImageButton fabAdd;

    private final Set<Integer> mainFragments = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.transactionFragment,
            R.id.accountFragment,
            R.id.categoryFragment,
            R.id.goalFragment,
            R.id.profileFragment
    ));

    public MainUIHandler(Context context, NavController navController, BottomNavigationView bottomNav, AppCompatImageButton fabAdd) {
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
                IconicsDrawable drawable = new IconicsDrawable(context, iconName);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorOnPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                int paddingPx = (int) (4 * context.getResources().getDisplayMetrics().density);

                InsetDrawable insetDrawable = new InsetDrawable(drawable, paddingPx);
                fabAdd.setImageDrawable(insetDrawable);
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
}