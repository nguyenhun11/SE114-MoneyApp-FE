package com.example.moneyapp.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

public class MainActivity extends AppCompatActivity {

    private MainUIHandler uiHandler;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khôi phục kết nối tới Notification Listener Service nếu đã bật
        if (com.example.moneyapp.data.local.PreferenceManager.getInstance(this).isNotificationListenerEnabled()) {
            android.service.notification.NotificationListenerService.requestRebind(
                    new android.content.ComponentName(this, com.example.moneyapp.service.TransactionNotificationListenerService.class)
            );
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        AppCompatImageButton fabAdd = findViewById(R.id.fab_add);

        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.homeFragment).setIcon(getShrunkIcon("gmd_home", 0));
        menu.findItem(R.id.historyFragment).setIcon(getShrunkIcon("gmd_history", 0));
        menu.findItem(R.id.accountFragment).setIcon(getShrunkIcon("gmd_category", 0));
        menu.findItem(R.id.profileFragment).setIcon(getShrunkIcon("gmd_person", 0));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
            uiHandler = new MainUIHandler(this, navController, bottomNav, fabAdd);

            setupSideMenuIcons(navController);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true).setRestoreState(false)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false, false)
                        .setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
                        .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out).build();
                try {
                    navController.navigate(itemId, null, options);
                    return true;
                } catch (Exception e) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
            });

            // Kiểm tra và xử lý điều hướng nếu Activity được mở từ Click thông báo đẩy
            handleNotificationIntent(getIntent());
        }
        drawerLayout.setDrawerElevation(0f);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                View mainContent = findViewById(R.id.main_content_wrapper);

                if (mainContent != null) {
                    float slideX = -(drawerView.getWidth() * slideOffset);
                    mainContent.setTranslationX(slideX);
                }
            }
        });
    }

    private Drawable getShrunkIcon(String iconName, int colorResId) {
        IconicsDrawable drawable = new IconicsDrawable(this, iconName);
        if (colorResId != 0) {
            int color = ContextCompat.getColor(this, colorResId);
            drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        int paddingPx = (int) (3 * getResources().getDisplayMetrics().density);

        return new InsetDrawable(drawable, paddingPx);
    }

    public void openRightSideMenu() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }
    private void setupSideMenuIcons(NavController navController) {
        setupSideMenuItem(R.id.btn_home_menu, "gmd_home", "Trang chủ", R.id.homeFragment, navController, R.color.colorPrimary, R.color.colorPrimaryBgLight);
        setupSideMenuItem(R.id.btn_statistics, "gmd_insert_chart", "Thống kê", R.id.statisticsFragment, navController, R.color.colorPrimary, R.color.colorPrimaryBgLight);
        setupSideMenuItem(R.id.btn_budget_menu, "gmd_account_balance_wallet", "Quản lý Ngân sách", R.id.budgetFragment, navController, R.color.colorDanger, R.color.colorDangerBgLight);
        setupSideMenuItem(R.id.btn_goals, "gmd_star", getString(R.string.goal_title), R.id.goalFragment, navController, R.color.colorWarning, R.color.colorWarningBgLight);
        setupSideMenuItem(R.id.btn_city_menu, "gmd_location_city", "Thành phố MoneyCity", R.id.cityFragment, navController, R.color.colorSuccess, R.color.colorSuccessBgLight);
        setupSideMenuItem(R.id.btn_info, "gmd_info", "Thông tin ứng dụng", R.id.informationFragment, navController, R.color.colorInfo, R.color.colorInfoBgLight);
        setupSideMenuItem(R.id.btn_settings, "gmd_settings", "Cài đặt", R.id.settingsFragment, navController, R.color.colorNeutral, R.color.colorNeutralBgLight);
    }

    private void setupSideMenuItem(int viewId, String iconName, String title, int destinationId, NavController navController, int iconColorRes, int bgColorRes) {
        View menuItem = findViewById(viewId);
        if (menuItem != null) {
            IconicsImageView ivIcon = menuItem.findViewById(R.id.iv_icon);
            IconicsDrawable drawable = new IconicsDrawable(this, iconName);
            drawable.setColorFilter(ContextCompat.getColor(this, iconColorRes), android.graphics.PorterDuff.Mode.SRC_IN);
            ivIcon.setImageDrawable(drawable);

            FrameLayout flBg = menuItem.findViewById(R.id.fl_icon_bg);
            flBg.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, bgColorRes)));

            ((TextView) menuItem.findViewById(R.id.tv_title)).setText(title);

            menuItem.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.END);
                NavOptions options = new NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();
                navController.navigate(destinationId, null, options);
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật intent mới nhận được
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("OPEN_PENDING_LIST", false)) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                // Thực hiện điều hướng trực tiếp sang màn hình PendingTransactionsFragment
                navController.navigate(R.id.pendingTransactionsFragment);
            }
        }
    }

    public MainUIHandler getUiHandler() {
        return uiHandler;
    }
}