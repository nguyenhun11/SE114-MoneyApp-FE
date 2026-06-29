package com.example.moneyapp.view.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.viewmodel.AuthViewModel;

public class ResetPasswordFragment extends Fragment {
    private AuthViewModel authViewModel;
    private EditText etToken, etNewPassword, etConfirmPassword;
    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        // 1. ÁNH XẠ UI
        androidx.core.widget.NestedScrollView rootScrollView = view.findViewById(R.id.root_scroll_view_reset);
        etToken = view.findViewById(R.id.et_reset_token);
        etNewPassword = view.findViewById(R.id.et_reset_password);
        etConfirmPassword = view.findViewById(R.id.et_reset_confirm_password);
        Button btnReset = view.findViewById(R.id.btn_reset_password);
        TextView tvBack = view.findViewById(R.id.tv_reset_back_to_login);

        com.mikepenz.iconics.view.IconicsImageView ivShowPassword = view.findViewById(R.id.iv_show_reset_password);
        com.mikepenz.iconics.view.IconicsImageView ivShowConfirmPassword = view.findViewById(R.id.iv_show_reset_confirm_password);

        observeViewModel();

        // 2. XỬ LÝ ẨN/HIỆN MẬT KHẨU
        final boolean[] isPasswordVisible = {false};
        if (ivShowPassword != null) {
            ivShowPassword.setOnClickListener(v -> {
                isPasswordVisible[0] = !isPasswordVisible[0];
                togglePasswordVisibility(requireContext(), etNewPassword, ivShowPassword, isPasswordVisible[0]);
            });
        }

        final boolean[] isConfirmPasswordVisible = {false};
        if (ivShowConfirmPassword != null) {
            ivShowConfirmPassword.setOnClickListener(v -> {
                isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];
                togglePasswordVisibility(requireContext(), etConfirmPassword, ivShowConfirmPassword, isConfirmPasswordVisible[0]);
            });
        }

        // 3. TỰ ĐỘNG ĐỆM PHẦN ĐÁY VÀ CUỘN KHI BÀN PHÍM HIỆN
        if (rootScrollView != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootScrollView, (v, insets) -> {
                int imeHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom;
                int navHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).bottom;
                int paddingBottom = Math.max(imeHeight, navHeight);

                v.setPadding(0, 0, 0, paddingBottom);

                if (imeHeight > 0) {
                    v.postDelayed(() -> {
                        View focus = view.findFocus();
                        if (focus == etToken || focus == etNewPassword || focus == etConfirmPassword) {
                            View target = focus;
                            if (focus == etNewPassword) {
                                target = view.findViewById(R.id.fl_reset_password_wrapper);
                            } else if (focus == etConfirmPassword) {
                                target = view.findViewById(R.id.fl_reset_confirm_password_wrapper);
                            }

                            if (target != null) {
                                int visibleHeight = v.getHeight() - paddingBottom;
                                int scrollY = target.getBottom() + 80 - visibleHeight;
                                rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                            }
                        }
                    }, 100);
                }
                return insets;
            });
        }

        // 4. XỬ LÝ KHI NGƯỜI DÙNG CHUYỂN HOẶC NHẤN VÀO CÁC Ô NHẬP LIỆU
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus && rootScrollView != null) {
                rootScrollView.postDelayed(() -> {
                    View target = v;
                    if (v == etNewPassword) {
                        target = view.findViewById(R.id.fl_reset_password_wrapper);
                    } else if (v == etConfirmPassword) {
                        target = view.findViewById(R.id.fl_reset_confirm_password_wrapper);
                    }

                    if (target != null) {
                        int visibleHeight = rootScrollView.getHeight() - rootScrollView.getPaddingBottom();
                        int scrollY = target.getBottom() + 80 - visibleHeight;
                        rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                    }
                }, 150);
            }
        };
        etToken.setOnFocusChangeListener(focusListener);
        etNewPassword.setOnFocusChangeListener(focusListener);
        etConfirmPassword.setOnFocusChangeListener(focusListener);

        // 5. XỬ LÝ CLICK BUTTON
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                String token = etToken.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                authViewModel.completeResetPassword(email, token, newPassword, confirmPassword);
            });
        }

        if (tvBack != null) {
            tvBack.setOnClickListener(v -> {
                Navigation.findNavController(v).popBackStack(R.id.loginFragment, false);
            });
        }
    }

    private void observeViewModel() {
        authViewModel.resetCompleteSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công",
                        getString(R.string.reset_password_success), () -> {
                            Navigation.findNavController(requireView()).popBackStack(R.id.loginFragment, false);
                        });
            }
        });

        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", message);
                authViewModel.errorMessage.setValue(null);
            }
        });
    }

    // Hàm tiện ích dùng chung để thay đổi thuộc tính ẩn hiện mật khẩu và icon mắt
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