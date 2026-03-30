package com.example.moneyapp.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.ui.SplashActivity;

public class ProfileFragment extends BaseFragment {

    @Override
    protected int getFabIcon() {
        return 0; // Hide FAB on profile
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thiết lập tiêu đề cho Header chung
        TextView tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText("Hồ sơ");
        }

        // Sự kiện Thay đổi mật khẩu
        View btnChangePassword = view.findViewById(R.id.tv_change_password);
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_changePasswordFragment);
            });
        }

        // Sự kiện Đăng xuất
        View btnLogout = view.findViewById(R.id.tv_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("isLoggedIn", false).apply();

                Intent intent = new Intent(requireActivity(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }
}
