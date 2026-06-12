package com.example.moneyapp.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        AppCompatImageButton fabAdd = findViewById(R.id.fab_add);

        // Setup Iconics cho Bottom Navigation
        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.homeFragment).setIcon(new IconicsDrawable(this, "gmd_home"));
        menu.findItem(R.id.transactionFragment).setIcon(new IconicsDrawable(this, "gmd_receipt"));
        menu.findItem(R.id.accountFragment).setIcon(new IconicsDrawable(this, "gmd_account_balance_wallet"));
        menu.findItem(R.id.profileFragment).setIcon(new IconicsDrawable(this, "gmd_person"));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
            uiHandler = new MainUIHandler(this, navController, bottomNav, fabAdd);

            // GỌI HÀM SETUP SIDE MENU TẠI ĐÂY
            setupSideMenuIcons(navController);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(false)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false, false)
                        .build();
                try {
                    navController.navigate(itemId, null, options);
                    return true;
                } catch (Exception e) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
            });
        }
    }

    // Hàm public để ProfileFragment có thể gọi tới
    public void openRightSideMenu() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    private void setupSideMenuIcons(NavController navController) {
        setupSideMenuItem(R.id.btn_statistics, "gmd_insert_chart", "Thống kê", R.id.statisticsFragment, navController);
        setupSideMenuItem(R.id.btn_categories, "gmd_category", "Hạng mục", R.id.categoryFragment, navController);
        setupSideMenuItem(R.id.btn_info, "gmd_info", "Thông tin", R.id.informationFragment, navController);
        setupSideMenuItem(R.id.btn_settings, "gmd_settings", "Cài đặt", R.id.settingsFragment, navController);
    }

    private void setupSideMenuItem(int viewId, String iconName, String title, int destinationId, NavController navController) {
        View menuItem = findViewById(viewId);
        if (menuItem != null) {
            IconicsImageView ivIcon = menuItem.findViewById(R.id.iv_icon);
            IconicsDrawable drawable = new IconicsDrawable(this, iconName);
            int color = ContextCompat.getColor(this, R.color.colorOnSurface);
            drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

            ivIcon.setIcon(drawable);

            ((TextView) menuItem.findViewById(R.id.tv_title)).setText(title);

            menuItem.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.END);
                navController.navigate(destinationId);
            });
        }
    }

    public MainUIHandler getUiHandler() {
        return uiHandler;
    }
}