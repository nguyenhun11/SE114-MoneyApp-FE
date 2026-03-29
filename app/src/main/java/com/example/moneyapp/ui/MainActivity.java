package com.example.moneyapp.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAdd;
    private MainUIHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
            
            uiHandler = new MainUIHandler(this, navController, bottomNav, fabAdd);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == R.id.moreFragment) {
                    uiHandler.showCustomMorePopup();
                    return false;
                }
                
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

    /**
     * Fragments can call this to show or hide the bottom navigation bar.
     */
    public void setBottomNavigationVisibility(boolean visible) {
        if (uiHandler != null) {
            uiHandler.setBottomNavigationVisibility(visible);
        }
    }

    /**
     * Updates the FAB icon and click listener.
     */
    public void updateFAB(int iconRes, View.OnClickListener listener) {
        if (uiHandler != null) {
            uiHandler.updateFAB(iconRes, listener);
        }
    }
}
