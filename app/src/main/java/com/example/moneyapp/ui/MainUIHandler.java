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

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainUIHandler {

    private final Context context;
    private final NavController navController;
    private final BottomNavigationView bottomNav;
    private final FloatingActionButton fabAdd;

    public MainUIHandler(Context context, NavController navController, BottomNavigationView bottomNav, FloatingActionButton fabAdd) {
        this.context = context;
        this.navController = navController;
        this.bottomNav = bottomNav;
        this.fabAdd = fabAdd;
    }

    public void updateFAB(int iconRes, View.OnClickListener listener) {
        if (fabAdd != null) {
            fabAdd.setImageResource(iconRes);
            fabAdd.setOnClickListener(listener);
        }
    }

    public void unselectAllMenuItems() {
        Menu menu = bottomNav.getMenu();
        menu.setGroupCheckable(0, true, false);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.setGroupCheckable(0, true, true);
    }

    public void showCustomMorePopup() {
        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_more_popup, null);
        
        PopupWindow popupWindow = new PopupWindow(popupView, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                true);

        setupPopupItem(popupView.findViewById(R.id.item_profile), R.drawable.ic_account, "Hồ sơ", R.id.profileFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_info), R.drawable.ic_transaction, "Thông tin", R.id.informationFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_settings), R.drawable.ic_more, "Cài đặt", R.id.settingsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_statistics), R.drawable.ic_transaction, "Thống kê", R.id.statisticsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_categories), R.drawable.ic_more, "Hạng mục", R.id.categoryFragment, popupWindow);

        float density = context.getResources().getDisplayMetrics().density;
        int marginHorizontal = (int) (20 * density);
        int marginFromNav = (int) (20 * density);
        
        int navBarHeight = bottomNav != null ? bottomNav.getHeight() : 0;
        int yOffset = navBarHeight + marginFromNav;

        popupWindow.showAtLocation(bottomNav.getRootView(), Gravity.BOTTOM | Gravity.END, marginHorizontal, yOffset);
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
        ImageView ivIcon = itemView.findViewById(R.id.iv_icon);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        
        ivIcon.setImageResource(iconRes);
        tvTitle.setText(title);
        
        itemView.setOnClickListener(v -> {
            if (navController != null) {
                NavOptions options = new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .setLaunchSingleTop(true)
                        .setRestoreState(false)
                        .build();
                navController.navigate(destinationId, null, options);
            }
            popupWindow.dismiss();
        });
    }
}
