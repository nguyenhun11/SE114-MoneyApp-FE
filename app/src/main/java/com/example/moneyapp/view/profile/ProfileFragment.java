package com.example.moneyapp.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.view.SplashActivity;
import com.example.moneyapp.viewmodel.ProfileViewModel;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileFragment extends BaseFragment {
    private ProfileViewModel profileViewModel;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    profileViewModel.updateProfileImage(uri.toString());
                }
            }
    );

    // XÓA HÀM getFabIcon() VÀ DÙNG HÀM NÀY ĐỂ ẨN FAB CHUẨN XÁC NHẤT
    @Override
    protected boolean shouldShowFAB() {
        return false;
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

        setupHeader(
                view,
                getString(R.string.profile_title),
                null, null,
                "gmd_menu",
                v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openRightSideMenu();
                    }
                }
        );

        // UI Components
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvCreatedAt = view.findViewById(R.id.tv_created_at);
        TextView tvUserId = view.findViewById(R.id.tv_user_id);
        TextView tvChangePassword = view.findViewById(R.id.tv_change_password_link);
        SwitchCompat swSync = view.findViewById(R.id.sw_sync);
        ImageButton btnLogout = view.findViewById(R.id.btn_logout);
        ImageButton btnDelete = view.findViewById(R.id.btn_delete_account);
        ImageView ivAvatar = view.findViewById(R.id.iv_profile_avatar);
        View cvAvatarContainer = view.findViewById(R.id.cv_avatar_container);

        // Fetch & Observe Data
        profileViewModel.fetchUserData();
        profileViewModel.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvUserId.setText("UserID: " + user.getUserId());

                IconicsDrawable defaultAvatar = new IconicsDrawable(requireContext(), "gmd-person");
                defaultAvatar.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant), android.graphics.PorterDuff.Mode.SRC_IN);

                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getProfileImageUrl())
                            .placeholder(defaultAvatar) // Dùng Iconics làm placeholder
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageDrawable(defaultAvatar);
                }

                if (user.getCreatedAt() != null) {
                    tvCreatedAt.setText(getVietnameseDate(user.getCreatedAt()));
                }
            }
        });

        profileViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message == null) return;

            if (message.equals("SUCCESS_DELETE")) {
                Toast.makeText(requireContext(), "Tài khoản đã được xóa thành công", Toast.LENGTH_SHORT).show();
                performLogout();
            } else if (!message.equals("SUCCESS")) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Click Listeners
        tvName.setOnClickListener(v -> {
            android.widget.EditText input = new android.widget.EditText(requireContext());
            input.setText(tvName.getText().toString());
            input.setPadding(50, 40, 50, 40);
            input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(20)});

            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Đổi tên người dùng")
                    .setMessage("Nhập tên mới của bạn (tối đa 20 ký tự):")
                    .setView(input)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            profileViewModel.updateUserName(newName);
                        } else {
                            Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        tvChangePassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_changePasswordFragment);
        });

        btnLogout.setOnClickListener(v -> performLogout());

        btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa tài khoản")
                    .setMessage("Tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn. Bạn có chắc chắn muốn tiếp tục?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        profileViewModel.deleteAccount();
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(android.R.drawable.ic_dialog_alert) // Đây là icon hệ thống, không sao cả
                    .show();
        });

        swSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Đã bật đồng bộ" : "Đã tắt đồng bộ";
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show();
        });

        cvAvatarContainer.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
    }

    private String getVietnameseDate(java.util.Date date) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat(" 'ngày' dd/MM/yyyy", new Locale("vi", "VN"));
        return dayFormat.format(date) + dateFormat.format(date);
    }

    private void performLogout() {
        PreferenceManager.getInstance(requireActivity().getApplicationContext()).clear();
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}