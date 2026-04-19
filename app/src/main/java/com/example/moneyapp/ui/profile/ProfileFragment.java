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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.ui.SplashActivity;
import com.example.moneyapp.utils.PreferenceManager;
import com.example.moneyapp.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment implements ProfileAdapter.OnOptionClickListener {
    private ProfileViewModel profileViewModel;
    private static final int OPTION_CHANGE_PASSWORD = 1;
    private static final int OPTION_LOGOUT = 2;
    private static final int OPTION_INFORMATION = 3;

    @Override
    protected int getFabIcon() {
        return 0; // Hide FAB
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
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Header Title
        TextView tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        if (tvHeaderTitle != null) tvHeaderTitle.setText("Hồ sơ");

        // Setup RecyclerView
        RecyclerView rvOptions = view.findViewById(R.id.rv_profile_options);
        ProfileAdapter adapter = new ProfileAdapter(getProfileOptions(), this);
        rvOptions.setAdapter(adapter);

        // Mock User Data
        profileViewModel.fetchUserData();
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        profileViewModel.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (tvName != null) tvName.setText(user.getName());
                if (tvEmail != null) tvEmail.setText(user.getEmail());
            }
        });
    }

    private List<ProfileOption> getProfileOptions() {
        List<ProfileOption> options = new ArrayList<>();
        options.add(new ProfileOption(OPTION_INFORMATION, R.drawable.ic_profile, "Thông tin ứng dụng"));
        options.add(new ProfileOption(OPTION_CHANGE_PASSWORD, R.drawable.ic_transfer, "Thay đổi mật khẩu"));
        options.add(new ProfileOption(OPTION_LOGOUT, R.drawable.ic_back, "Đăng xuất")); // Dùng tạm ic_back quay ngược làm logout
        return options;
    }

    @Override
    public void onOptionClick(ProfileOption option) {
        switch (option.getId()) {
            case OPTION_CHANGE_PASSWORD:
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_changePasswordFragment);
                break;
            case OPTION_INFORMATION:
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_informationFragment);
                break;
            case OPTION_LOGOUT:
                performLogout();
                break;
        }
    }

    private void performLogout() {
        PreferenceManager.getInstance(requireActivity().getApplicationContext()).setLoggedIn(false);

        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
