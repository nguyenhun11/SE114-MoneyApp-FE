package com.example.moneyapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);

            bottomNav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.moreFragment) {
                    showCustomMorePopup(bottomNav);
                    return false;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                List<Integer> mainTabs = Arrays.asList(
                    R.id.homeFragment, 
                    R.id.transactionFragment, 
                    R.id.accountFragment
                );

                if (!mainTabs.contains(destId)) {
                    unselectAllMenuItems();
                } else {
                    bottomNav.getMenu().setGroupCheckable(0, true, true);
                }
            });
        }
    }

    private void unselectAllMenuItems() {
        Menu menu = bottomNav.getMenu();
        menu.setGroupCheckable(0, true, false);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.setGroupCheckable(0, true, true);
    }

    private void showCustomMorePopup(View bottomNav) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.layout_more_popup, null);
        
        PopupWindow popupWindow = new PopupWindow(popupView, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                true);

        setupPopupItem(popupView.findViewById(R.id.item_profile), R.drawable.ic_account, "Hồ sơ", R.id.profileFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_info), R.drawable.ic_transaction, "Thông tin", R.id.informationFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_settings), R.drawable.ic_more, "Cài đặt", R.id.settingsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_statistics), R.drawable.ic_transaction, "Thống kê", R.id.statisticsFragment, popupWindow);
        setupPopupItem(popupView.findViewById(R.id.item_categories), R.drawable.ic_more, "Hạng mục", R.id.categoryFragment, popupWindow);

        float density = getResources().getDisplayMetrics().density;
        int marginHorizontal = (int) (20 * density);
        int marginFromNav = (int) (20 * density);
        
        int navBarHeight = bottomNav != null ? bottomNav.getHeight() : 0;
        int yOffset = navBarHeight + marginFromNav;

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.END, marginHorizontal, yOffset);
        dimBehind(popupWindow);
    }

    private void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
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
                // Use NavOptions to prevent these screens from being saved as part of main tab state
                NavOptions options = new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false) // Pop up to home to clear previous stack
                        .setLaunchSingleTop(true)
                        .setRestoreState(false) // Don't restore the state of the more-fragments
                        .build();
                
                navController.navigate(destinationId, null, options);
            }
            popupWindow.dismiss();
        });
    }
}
