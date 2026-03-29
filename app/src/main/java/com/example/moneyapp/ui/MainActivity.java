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

import java.util.Arrays;
import java.util.List;

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
                
                // For main tabs, we force navigation to the root fragment and don't restore sub-page states
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(false) // Crucial: don't show the last sub-page (e.g. Profile)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false, false)
                        .build();
                
                try {
                    navController.navigate(itemId, null, options);
                    return true;
                } catch (Exception e) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
            });

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                
                // Update FAB based on destination
                if (destId == R.id.accountFragment) {
                    uiHandler.updateFAB(R.drawable.ic_transfer, v -> {
                        // Handle transfer action
                    });
                } else {
                    uiHandler.updateFAB(R.drawable.ic_add_white, v -> {
                        // Handle default add action
                    });
                }

                List<Integer> mainTabs = Arrays.asList(
                    R.id.homeFragment, 
                    R.id.transactionFragment, 
                    R.id.accountFragment
                );

                if (!mainTabs.contains(destId)) {
                    uiHandler.unselectAllMenuItems();
                } else {
                    bottomNav.getMenu().setGroupCheckable(0, true, true);
                }
            });
        }
    }

    public void updateFAB(int iconRes, View.OnClickListener listener) {
        if (uiHandler != null) {
            uiHandler.updateFAB(iconRes, listener);
        }
    }
}
