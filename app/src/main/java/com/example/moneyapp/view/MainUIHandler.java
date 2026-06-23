package com.example.moneyapp.view;

import android.content.Context;
import android.graphics.drawable.InsetDrawable;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

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
    private final View fabContainer;
    private final TextView fabLabel;

    private final Set<Integer> mainFragments = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.historyFragment,
            R.id.accountFragment,
            R.id.categoryFragment,
            R.id.goalFragment,
            R.id.profileFragment
    ));

    public MainUIHandler(Context context, NavController navController, BottomNavigationView bottomNav, AppCompatImageButton fabAdd) {
        this.navController = navController;
        this.bottomNav = bottomNav;
        this.fabAdd = fabAdd;

        this.fabContainer = (View) fabAdd.getParent();
        this.fabLabel = fabContainer.findViewById(R.id.fab_label);

        if (this.fabLabel != null) {
            this.fabLabel.setSelected(true);
        }

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

    public void updateFAB(String iconName, String labelText, View.OnClickListener listener) {
        if (fabAdd != null) {
            Context context = fabAdd.getContext();
            if (iconName != null && !iconName.isEmpty()) {
                IconicsDrawable drawable = new IconicsDrawable(context, iconName);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorOnPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                int paddingPx = (int) (4 * context.getResources().getDisplayMetrics().density);

                InsetDrawable insetDrawable = new InsetDrawable(drawable, paddingPx);
                fabAdd.setImageDrawable(insetDrawable);
            }
            if (fabLabel != null && labelText != null) {
                fabLabel.setText(labelText);
            }
            fabAdd.setOnClickListener(listener);
            setFABEnabled(true);
        }
    }

    public void setBottomNavigationVisibility(boolean visible) {
        if (bottomNav != null) {
            bottomNav.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
    public void setFABEnabled(boolean isEnabled) {
        if (fabAdd != null && fabContainer != null) {
            fabAdd.setEnabled(isEnabled);
            fabContainer.setAlpha(isEnabled ? 1.0f : 0.4f);
        }
    }

    public void setFABVisibility(boolean visible) {
        if (fabContainer == null) return;

        if (visible && fabContainer.getVisibility() != View.VISIBLE) {
            fabContainer.setVisibility(View.VISIBLE);
            fabContainer.setScaleX(0f);
            fabContainer.setScaleY(0f);
            fabContainer.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        } else if (!visible && fabContainer.getVisibility() == View.VISIBLE) {
            fabContainer.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(200)
                    .setInterpolator(new AnticipateInterpolator()) // Thụt lùi lấy đà rồi thu nhỏ
                    .withEndAction(() -> fabContainer.setVisibility(View.GONE))
                    .start();
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