package com.example.moneyapp.view;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
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

        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.homeFragment).setIcon(getShrunkIcon("gmd_home", 0));
        menu.findItem(R.id.historyFragment).setIcon(getShrunkIcon("gmd_history", 0));
        menu.findItem(R.id.accountFragment).setIcon(getShrunkIcon("gmd_account_balance_wallet", 0));
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

    public void openRightSideMenu() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    private void setupSideMenuIcons(NavController navController) {
        setupSideMenuItem(R.id.btn_statistics, "gmd_insert_chart", "Thống kê", R.id.statisticsFragment, navController);
        setupSideMenuItem(R.id.btn_goals, "gmd_star", getString(R.string.goal_title), R.id.goalFragment, navController);
        setupSideMenuItem(R.id.btn_categories, "gmd_category", "Hạng mục", R.id.categoryFragment, navController);
        
        // Thêm MoneyCity và Budget vào Side Menu
        setupSideMenuItem(R.id.btn_city_menu, "gmd_location_city", "Thành phố MoneyCity", R.id.cityFragment, navController);
        setupSideMenuItem(R.id.btn_budget_menu, "gmd_account_balance_wallet", "Quản lý Ngân sách", R.id.budgetFragment, navController);

        setupSideMenuItem(R.id.btn_info, "gmd_info", "Thông tin", R.id.informationFragment, navController);
        setupSideMenuItem(R.id.btn_settings, "gmd_settings", "Cài đặt", R.id.settingsFragment, navController);
    }

    private void setupSideMenuItem(int viewId, String iconName, String title, int destinationId, NavController navController) {
        View menuItem = findViewById(viewId);
        if (menuItem != null) {
            IconicsImageView ivIcon = menuItem.findViewById(R.id.iv_icon);
            ivIcon.setImageDrawable(getShrunkIcon(iconName, R.color.colorOnSurface));

            ((TextView) menuItem.findViewById(R.id.tv_title)).setText(title);

            menuItem.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.END);
                navController.navigate(destinationId);
            });
        }
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

    public MainUIHandler getUiHandler() {
        return uiHandler;
    }
}