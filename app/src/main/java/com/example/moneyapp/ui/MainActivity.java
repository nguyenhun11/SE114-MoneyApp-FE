package com.example.moneyapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.moneyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);

            bottomNav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.moreFragment) {
                    showCustomMorePopup(bottomNav);
                    // Return false to prevent the "More" button from becoming selected/highlighted
                    // and keep the previous tab highlighted.
                    return false;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }

    private void showCustomMorePopup(View bottomNav) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.layout_more_popup, null);
        
        // Setup menu items
        setupPopupItem(popupView.findViewById(R.id.item_profile), R.drawable.ic_account, "Hồ sơ");
        setupPopupItem(popupView.findViewById(R.id.item_info), R.drawable.ic_transaction, "Thông tin");
        setupPopupItem(popupView.findViewById(R.id.item_settings), R.drawable.ic_more, "Cài đặt");
        setupPopupItem(popupView.findViewById(R.id.item_statistics), R.drawable.ic_transaction, "Thống kê");
        setupPopupItem(popupView.findViewById(R.id.item_categories), R.drawable.ic_more, "Hạng mục");

        PopupWindow popupWindow = new PopupWindow(popupView, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                true);

        // Convert dp to pixels
        float density = getResources().getDisplayMetrics().density;
        int marginHorizontal = (int) (20 * density);
        int marginFromNav = (int) (20 * density);
        
        int navBarHeight = bottomNav != null ? bottomNav.getHeight() : 0;
        int yOffset = navBarHeight + marginFromNav;

        // Show the popup
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.END, marginHorizontal, yOffset);

        // Dim background
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

    private void setupPopupItem(View itemView, int iconRes, String title) {
        if (itemView == null) return;
        ImageView ivIcon = itemView.findViewById(R.id.iv_icon);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        
        ivIcon.setImageResource(iconRes);
        tvTitle.setText(title);
        
        itemView.setOnClickListener(v -> {
            // Handle item click here
        });
    }
}
