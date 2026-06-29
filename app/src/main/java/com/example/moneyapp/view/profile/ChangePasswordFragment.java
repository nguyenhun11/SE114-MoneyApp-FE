package com.example.moneyapp.view.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.ProfileViewModel;

public class ChangePasswordFragment extends BaseFragment {

    // Đưa các biến lên cấp độ class để dùng chung cho onFabClick()
    private ProfileViewModel profileViewModel;
    private EditText etCurrentPass, etNewPass, etConfirmPass;

    @Override
    protected boolean shouldShowFAB() {
        return true;
    }

    @Override
    protected String getFabIcon() {
        return "gmd_check";
    }

    @Override
    protected String getFabLabel() {
        return "Cập nhật";
    }

    @Override
    protected void onFabClick() {
        // Thực thi logic cập nhật khi bấm vào FAB
        if (etCurrentPass != null && etNewPass != null && etConfirmPass != null) {
            String oldPass = etCurrentPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            profileViewModel.updatePassword(oldPass, newPass, confirmPass);
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    // -------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view, R.string.change_password_title, true);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // 1. ÁNH XẠ UI VÀO CÁC BIẾN TOÀN CỤC
        androidx.core.widget.NestedScrollView rootScrollView = view.findViewById(R.id.root_scroll_view_change_pwd);
        etCurrentPass = view.findViewById(R.id.et_current_password);
        etNewPass = view.findViewById(R.id.et_new_password);
        etConfirmPass = view.findViewById(R.id.et_confirm_new_password);

        com.mikepenz.iconics.view.IconicsImageView ivShowCurrentPass = view.findViewById(R.id.iv_show_current_password);
        com.mikepenz.iconics.view.IconicsImageView ivShowNewPass = view.findViewById(R.id.iv_show_new_password);
        com.mikepenz.iconics.view.IconicsImageView ivShowConfirmPass = view.findViewById(R.id.iv_show_confirm_new_password);

        // 2. XỬ LÝ ẨN/HIỆN MẬT KHẨU
        final boolean[] isCurrentVisible = {false};
        if (ivShowCurrentPass != null) {
            ivShowCurrentPass.setOnClickListener(v -> {
                isCurrentVisible[0] = !isCurrentVisible[0];
                togglePasswordVisibility(requireContext(), etCurrentPass, ivShowCurrentPass, isCurrentVisible[0]);
            });
        }

        final boolean[] isNewVisible = {false};
        if (ivShowNewPass != null) {
            ivShowNewPass.setOnClickListener(v -> {
                isNewVisible[0] = !isNewVisible[0];
                togglePasswordVisibility(requireContext(), etNewPass, ivShowNewPass, isNewVisible[0]);
            });
        }

        final boolean[] isConfirmVisible = {false};
        if (ivShowConfirmPass != null) {
            ivShowConfirmPass.setOnClickListener(v -> {
                isConfirmVisible[0] = !isConfirmVisible[0];
                togglePasswordVisibility(requireContext(), etConfirmPass, ivShowConfirmPass, isConfirmVisible[0]);
            });
        }

        // 3. TỰ ĐỘNG ĐỆM PHẦN ĐÁY VÀ CUỘN KHI BÀN PHÍM HIỆN
        // 3. TỰ ĐỘNG CUỘN KHI BÀN PHÍM HIỆN
        if (rootScrollView != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootScrollView, (v, insets) -> {
                int imeHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom;

                // XÓA HOẶC COMMENT DÒNG NÀY ĐỂ TRÁNH LỖI KHOẢNG TRẮNG KÉP
                // int navHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).bottom;
                // int paddingBottom = Math.max(imeHeight, navHeight);
                // v.setPadding(0, 0, 0, paddingBottom);

                if (imeHeight > 0) {
                    v.postDelayed(() -> {
                        View focus = view.findFocus();
                        if (focus == etCurrentPass || focus == etNewPass || focus == etConfirmPass) {
                            View target = focus;
                            if (focus == etCurrentPass) {
                                target = view.findViewById(R.id.fl_current_password_wrapper);
                            } else if (focus == etNewPass) {
                                target = view.findViewById(R.id.fl_new_password_wrapper);
                            } else if (focus == etConfirmPass) {
                                target = view.findViewById(R.id.fl_confirm_new_password_wrapper);
                            }

                            if (target != null) {
                                // Tính toán vị trí cuộn không cần cộng thêm paddingBottom nữa
                                int visibleHeight = v.getHeight();
                                int scrollY = target.getBottom() + 80 - visibleHeight;
                                rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                            }
                        }
                    }, 100);
                }
                return insets;
            });
        }

        // 4. XỬ LÝ KHI NGƯỜI DÙNG CHUYỂN NHIỀU Ô NHẬP LIỆU
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus && rootScrollView != null) {
                rootScrollView.postDelayed(() -> {
                    View target = v;
                    if (v == etCurrentPass) {
                        target = view.findViewById(R.id.fl_current_password_wrapper);
                    } else if (v == etNewPass) {
                        target = view.findViewById(R.id.fl_new_password_wrapper);
                    } else if (v == etConfirmPass) {
                        target = view.findViewById(R.id.fl_confirm_new_password_wrapper);
                    }

                    if (target != null) {
                        int visibleHeight = rootScrollView.getHeight() - rootScrollView.getPaddingBottom();
                        int scrollY = target.getBottom() + 80 - visibleHeight;
                        rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                    }
                }, 150);
            }
        };
        etCurrentPass.setOnFocusChangeListener(focusListener);
        etNewPass.setOnFocusChangeListener(focusListener);
        etConfirmPass.setOnFocusChangeListener(focusListener);

        // 5. OBSERVE VIEWMODEL
        profileViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                if (message.equals("SUCCESS")) {
                    DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Đổi mật khẩu thành công!", () -> {
                        Navigation.findNavController(requireView()).navigateUp();
                    });
                } else {
                    DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
                }
                profileViewModel.errorMessage.setValue(null);
            }
        });
    }

    // Hàm tiện ích ẩn/hiện mật khẩu
    private void togglePasswordVisibility(Context context, EditText editText, com.mikepenz.iconics.view.IconicsImageView iconView, boolean isVisible) {
        if (editText == null || iconView == null) return;
        int iconColor = androidx.core.content.ContextCompat.getColor(context, R.color.colorOnSurfaceVariant);

        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            com.mikepenz.iconics.IconicsDrawable icon = new com.mikepenz.iconics.IconicsDrawable(context, "gmd-visibility");
            icon.setColorFilter(iconColor, android.graphics.PorterDuff.Mode.SRC_IN);
            iconView.setIcon(icon);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            com.mikepenz.iconics.IconicsDrawable icon = new com.mikepenz.iconics.IconicsDrawable(context, "gmd-visibility-off");
            icon.setColorFilter(iconColor, android.graphics.PorterDuff.Mode.SRC_IN);
            iconView.setIcon(icon);
        }

        editText.setTypeface(android.graphics.Typeface.DEFAULT);
        editText.setSelection(editText.getText().length());
    }
}