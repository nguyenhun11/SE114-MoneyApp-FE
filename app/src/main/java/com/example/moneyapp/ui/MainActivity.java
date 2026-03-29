package com.example.moneyapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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



//            bottomNav.setOnItemSelectedListener(item -> {
//                // 1. Kiểm tra: Nếu đang đứng ở tab nào mà bấm lại tab đó thì bỏ qua, không trượt
//                if (navController.getCurrentDestination() != null &&
//                        navController.getCurrentDestination().getId() == item.getItemId()) {
//                    return true;
//                }
//
//                // 2. Chế tạo "Bộ tùy chỉnh điều hướng" (Gắn hiệu ứng trượt vào đây)
//                NavOptions navOptions = new NavOptions.Builder()
//                        .setLaunchSingleTop(true)
//                        .setRestoreState(true)
//                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false, true)
//
//                        // Khi TIẾN tới trang mới (Ví dụ: Home -> Giao dịch)
//                        .setEnterAnim(R.anim.slide_in_right)
//                        .setExitAnim(R.anim.slide_out_left)
//
//                        // Khi LÙI về trang gốc (Ví dụ: Giao dịch -> Home)
//                        .setPopEnterAnim(R.anim.slide_in_left)   // <-- THÊM DÒNG NÀY
//                        .setPopExitAnim(R.anim.slide_out_right)  // <-- THÊM DÒNG NÀY
//                        .build();
//
//                // 3. Ra lệnh chuyển trang kèm hiệu ứng
//                navController.navigate(item.getItemId(), null, navOptions);
//                return true;
//            });
        }
    }
}
