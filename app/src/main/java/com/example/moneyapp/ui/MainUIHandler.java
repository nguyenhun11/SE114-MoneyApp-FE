package com.example.moneyapp.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainUIHandler {

    private final Context context;
    private final NavController navController;
    private final BottomNavigationView bottomNav;
    private final FloatingActionButton fabAdd;

    // Các fragment chính sẽ hiện BottomNav
    private final Set<Integer> mainFragments = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.transactionFragment,
            R.id.accountFragment
    ));

    public MainUIHandler(Context context, NavController navController, BottomNavigationView bottomNav, FloatingActionButton fabAdd) {
        this.context = context;
        this.navController = navController;
        this.bottomNav = bottomNav;
        this.fabAdd = fabAdd;
        setupNavigationListener();
    }

    private void setupNavigationListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            
            // Tự động xử lý ẩn hiện BottomNav
            setBottomNavigationVisibility(mainFragments.contains(destId));

            // Tự động xử lý trạng thái Menu
            if (!mainFragments.contains(destId)) {
                unselectAllMenuItems();
            } else {
                bottomNav.getMenu().setGroupCheckable(0, true, true);
            }
        });
    }

    public void updateFAB(@DrawableRes int iconRes, View.OnClickListener listener) {
        if (fabAdd != null) {
            fabAdd.setImageResource(iconRes);
            fabAdd.setOnClickListener(listener);
        }
    }

    public void setBottomNavigationVisibility(boolean visible) {
        if (bottomNav != null) {
            bottomNav.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    public void showCustomMorePopup() {
        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_more_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        setupPopupItem(popupView.findViewById(R.id.item_profile), R.drawable.ic_account, "Hồ sơ", R.id.profileFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_info), R.drawable.ic_transaction, "Thông tin", R.id.informationFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_settings), R.drawable.ic_more, "Cài đặt", R.id.settingsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_statistics), R.drawable.ic_transaction, "Thống kê", R.id.statisticsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_categories), R.drawable.ic_more, "Hạng mục", R.id.categoryFragment, popupWindow);

        float density = context.getResources().getDisplayMetrics().density;
        int yOffset = bottomNav.getHeight() + (int)(20 * density);
        popupWindow.showAtLocation(bottomNav.getRootView(), Gravity.BOTTOM | Gravity.END, (int)(20 * density), yOffset);
        dimBehind(popupWindow);
    }

    private void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.4f;
        wm.updateViewLayout(container, p);
    }

    private void setupPopupItem(View itemView, int iconRes, String title, int destinationId, PopupWindow popupWindow) {
        if (itemView == null) return;
        ((ImageView)itemView.findViewById(R.id.iv_icon)).setImageResource(iconRes);
        ((TextView)itemView.findViewById(R.id.tv_title)).setText(title);
        itemView.setOnClickListener(v -> {
            NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).setLaunchSingleTop(true).build();
            navController.navigate(destinationId, null, options);
            popupWindow.dismiss();
        });
    }
}
