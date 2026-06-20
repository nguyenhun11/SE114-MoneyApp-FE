package com.example.moneyapp.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

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

    @Override
    protected boolean shouldShowFAB() { return true; }

    @Override
    protected String getFabIcon() { return "gmd-share"; }

    @Override
    protected void onFabClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hãy tải MoneyApp để quản lý tài chính thông minh nhé!");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return true; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupHeader(view, getString(R.string.profile_title), null, null, "gmd-menu", v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openRightSideMenu();
            }
        });

        setupScrollBehavior(view);

        // Ánh xạ View Expanded & Collapsed
        TextView tvNameExpanded = view.findViewById(R.id.tv_profile_name_expanded);
        TextView tvUserIdExpanded = view.findViewById(R.id.tv_user_id_expanded);
        TextView tvNameCollapsed = view.findViewById(R.id.tv_profile_name_collapsed);
        TextView tvUserIdCollapsed = view.findViewById(R.id.tv_user_id_collapsed);

        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvCreatedAt = view.findViewById(R.id.tv_created_at);

        TextView tvStreakCount = view.findViewById(R.id.tv_streak_count);
        MaterialButton btnCheckin = view.findViewById(R.id.btn_checkin);
        IconicsImageView ivStreakIcon = view.findViewById(R.id.iv_streak_icon);

        View llChangePassword = view.findViewById(R.id.ll_change_password);
        SwitchCompat swSync = view.findViewById(R.id.sw_sync);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnDelete = view.findViewById(R.id.btn_delete_account);
        IconicsImageView ivAvatar = view.findViewById(R.id.iv_profile_avatar);
        View cvAvatarContainer = view.findViewById(R.id.cv_avatar_container);
        View collapsedContent = view.findViewById(R.id.collapsed_content);

        // Fetch & Observe
        profileViewModel.fetchUserData();
        profileViewModel.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvNameExpanded.setText(user.getName());
                tvNameCollapsed.setText(user.getName());

                String idText = "ID: " + user.getUserId();
                tvUserIdExpanded.setText(idText);
                tvUserIdCollapsed.setText(idText);

                tvEmail.setText(user.getEmail());

                IconicsDrawable defaultAvatar = new IconicsDrawable(requireContext(), "gmd-person");
                defaultAvatar.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant), android.graphics.PorterDuff.Mode.SRC_IN);

                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    Glide.with(this).load(user.getProfileImageUrl()).placeholder(defaultAvatar).into(ivAvatar);
                } else {
                    ivAvatar.setImageDrawable(defaultAvatar);
                }

                if (user.getCreatedAt() != null) {
                    tvCreatedAt.setText(getVietnameseDate(user.getCreatedAt()));
                }

                // STREAK
                tvStreakCount.setText(user.getDailyStreak() + " ngày");
                if (user.isTodayCheckedIn()) {
                    btnCheckin.setText("Đã điểm danh");
                    btnCheckin.setEnabled(false);
                    btnCheckin.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorSuccess));
                    btnCheckin.setIcon(new IconicsDrawable(requireContext(), "gmd-check"));
                    ivStreakIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                } else {
                    btnCheckin.setText("Check-in");
                    btnCheckin.setEnabled(true);
                    btnCheckin.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorWarning));
                    btnCheckin.setIcon(null);
                    ivStreakIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant));
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
        View.OnClickListener editNameListener = v -> {
            android.widget.EditText input = new android.widget.EditText(requireContext());
            input.setText(tvNameExpanded.getText().toString());
            input.setPadding(50, 40, 50, 40);
            input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(20)});

            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Đổi tên người dùng")
                    .setView(input)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) profileViewModel.updateUserName(newName);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        };

        tvNameExpanded.setOnClickListener(editNameListener);
        collapsedContent.setOnClickListener(editNameListener); // Cho phép bấm vào tên lúc thu gọn để đổi

        btnCheckin.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Tính năng Check-in đang phát triển!", Toast.LENGTH_SHORT).show();
        });

        llChangePassword.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_changePasswordFragment));
        btnLogout.setOnClickListener(v -> performLogout());
        btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa tài khoản")
                    .setMessage("Dữ liệu sẽ bị xóa vĩnh viễn. Tiếp tục?")
                    .setPositiveButton("Xóa", (dialog, which) -> profileViewModel.deleteAccount())
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        cvAvatarContainer.setOnClickListener(v -> mGetContent.launch("image/*"));
    }

    private void setupScrollBehavior(View view) {
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar_profile);
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);

        View profileCardWrapper = view.findViewById(R.id.profile_card_wrapper);
        View topSlice = view.findViewById(R.id.top_slice);
        View expandedContent = view.findViewById(R.id.expanded_content);
        View collapsedContent = view.findViewById(R.id.collapsed_content);

        profileCardWrapper.post(() -> {
            int topHeight = topSlice.getHeight();
            int collapsedHeight = collapsedContent.getHeight();
            int wrapperMarginBottom = getResources().getDimensionPixelSize(R.dimen.card_horizontal_margin); // ~12-16dp

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) profileCardWrapper.getLayoutParams();
            params.topMargin = topHeight + 80;
            profileCardWrapper.setLayoutParams(params);

            collapsingToolbar.setMinimumHeight(topHeight + collapsedHeight + wrapperMarginBottom);

            if (appBarLayout != null) {
                collapsedContent.setAlpha(0f);
                collapsedContent.setTranslationY(30f);
                collapsedContent.setVisibility(View.INVISIBLE);

                expandedContent.setAlpha(1f);
                expandedContent.setScaleX(1f);
                expandedContent.setScaleY(1f);
                expandedContent.setVisibility(View.VISIBLE);

                final boolean[] isCurrentlyCollapsed = {false};

                appBarLayout.addOnOffsetChangedListener((appBar, verticalOffset) -> {
                    int totalScrollRange = appBar.getTotalScrollRange();
                    if (totalScrollRange == 0) return;

                    float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;
                    boolean shouldCollapse = percentage > 0.5f;

                    // Chỉ kích hoạt Animation khi qua ngưỡng 50%
                    if (shouldCollapse != isCurrentlyCollapsed[0]) {
                        isCurrentlyCollapsed[0] = shouldCollapse;

                        if (shouldCollapse) {
                            // THU GỌN: Ẩn Avatar, Hiện Tên nhỏ
                            expandedContent.animate()
                                    .alpha(0f)
                                    .scaleX(0.8f).scaleY(0.8f)
                                    .setDuration(150)
                                    .withEndAction(() -> expandedContent.setVisibility(View.INVISIBLE))
                                    .start();

                            collapsedContent.setVisibility(View.VISIBLE);
                            collapsedContent.animate()
                                    .alpha(1f)
                                    .translationY(0f)
                                    .setDuration(150)
                                    .start();
                        } else {
                            // MỞ BUNG: Hiện Avatar, Ẩn Tên nhỏ
                            collapsedContent.animate()
                                    .alpha(0f)
                                    .translationY(30f)
                                    .setDuration(150)
                                    .withEndAction(() -> collapsedContent.setVisibility(View.INVISIBLE))
                                    .start();

                            expandedContent.setVisibility(View.VISIBLE);
                            expandedContent.animate()
                                    .alpha(1f)
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(150)
                                    .start();
                        }
                    }
                });
            }
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