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

public class RegisterFragment extends Fragment {
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 1. ÁNH XẠ UI
        androidx.core.widget.NestedScrollView rootScrollView = view.findViewById(R.id.root_scroll_view_register);
        Button btnRegister = view.findViewById(R.id.btn_register);
        TextView tvGoToLogin = view.findViewById(R.id.tv_go_to_login);
        EditText etName = view.findViewById(R.id.et_name_register);
        EditText etEmail = view.findViewById(R.id.et_email_register);
        EditText etPassword = view.findViewById(R.id.et_password_register);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password_register);

        com.mikepenz.iconics.view.IconicsImageView ivShowPassword = view.findViewById(R.id.iv_show_password_register);
        com.mikepenz.iconics.view.IconicsImageView ivShowConfirmPassword = view.findViewById(R.id.iv_show_confirm_password_register);

        // 2. XỬ LÝ ẨN/HIỆN MẬT KHẨU
        final boolean[] isPasswordVisible = {false};
        if (ivShowPassword != null) {
            ivShowPassword.setOnClickListener(v -> {
                isPasswordVisible[0] = !isPasswordVisible[0];
                togglePasswordVisibility(requireContext(), etPassword, ivShowPassword, isPasswordVisible[0]);
            });
        }

        final boolean[] isConfirmPasswordVisible = {false};
        if (ivShowConfirmPassword != null) {
            ivShowConfirmPassword.setOnClickListener(v -> {
                isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];
                togglePasswordVisibility(requireContext(), etConfirmPassword, ivShowConfirmPassword, isConfirmPasswordVisible[0]);
            });
        }

        // 3. TỰ ĐỘNG ĐỆM PHẦN ĐÁY VÀ CUỘN KHI BÀN PHÍM HIỆN (THUẬT TOÁN TỪ LOGIN)
        if (rootScrollView != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootScrollView, (v, insets) -> {
                int imeHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom;
                int navHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).bottom;
                int paddingBottom = Math.max(imeHeight, navHeight);

                // Đệm phần đáy bằng đúng chiều cao bàn phím để có không gian cuộn rộng rãi
                v.setPadding(0, 0, 0, paddingBottom);

                // Khi bàn phím mở lên, kiểm tra xem ô đang chọn có bị che khuất không để tự động kéo lên trên bàn phím
                if (imeHeight > 0) {
                    v.postDelayed(() -> {
                        View focus = view.findFocus();
                        if (focus == etName || focus == etEmail || focus == etPassword || focus == etConfirmPassword) {
                            View target = focus;
                            if (focus == etPassword) {
                                target = view.findViewById(R.id.fl_password_register_wrapper);
                            } else if (focus == etConfirmPassword) {
                                target = view.findViewById(R.id.fl_confirm_password_register_wrapper);
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
                    if (v == etPassword) {
                        target = view.findViewById(R.id.fl_password_register_wrapper);
                    } else if (v == etConfirmPassword) {
                        target = view.findViewById(R.id.fl_confirm_password_register_wrapper);
                    }

                    if (target != null) {
                        int visibleHeight = rootScrollView.getHeight() - rootScrollView.getPaddingBottom();
                        int scrollY = target.getBottom() + 80 - visibleHeight;
                        rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                    }
                }, 150);
            }
        };
        etName.setOnFocusChangeListener(focusListener);
        etEmail.setOnFocusChangeListener(focusListener);
        etPassword.setOnFocusChangeListener(focusListener);
        etConfirmPassword.setOnFocusChangeListener(focusListener);

        // 5. XỬ LÝ CLICK ĐĂNG KÝ
        android.text.TextWatcher clearErrorTextWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                View focusedView = view.findFocus();
                if (focusedView instanceof EditText) {
                    ((EditText) focusedView).setError(null);
                }
            }
        };
        etName.addTextChangedListener(clearErrorTextWatcher);
        etEmail.addTextChangedListener(clearErrorTextWatcher);
        etPassword.addTextChangedListener(clearErrorTextWatcher);
        etConfirmPassword.addTextChangedListener(clearErrorTextWatcher);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (name.isEmpty()) {
                    etName.setError("Họ và tên không được để trống");
                    etName.requestFocus();
                    return;
                }
                if (name.length() < 2) {
                    etName.setError("Họ và tên phải từ 2 ký tự trở lên");
                    etName.requestFocus();
                    return;
                }
                if (name.length() > 50) {
                    etName.setError("Họ và tên không được vượt quá 50 ký tự");
                    etName.requestFocus();
                    return;
                }
                if (!name.matches("^[\\p{L}\\s'-]+$")) {
                    etName.setError("Họ và tên chỉ được chứa chữ cái và khoảng trắng");
                    etName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    etEmail.setError("Email không được để trống");
                    etEmail.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Email không đúng định dạng");
                    etEmail.requestFocus();
                    return;
                }

                if (password.length() < 8) {
                    etPassword.setError("Mật khẩu phải từ 8 ký tự trở lên");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.matches(".*[A-Z].*")) {
                    etPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ cái in hoa");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.matches(".*[a-z].*")) {
                    etPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ cái thường");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.matches(".*\\d.*")) {
                    etPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ số");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.matches(".*[^a-zA-Z0-9].*")) {
                    etPassword.setError("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt");
                    etPassword.requestFocus();
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
                    etConfirmPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                    etConfirmPassword.requestFocus();
                    return;
                }

                authViewModel.register(name, email, password, confirmPassword);
            });
        }

        // 6. CHUYỂN SANG TRANG ĐĂNG NHẬP
        if (tvGoToLogin != null) {
            String text = getString(R.string.register_already_have_account);
            android.text.SpannableString ss = new android.text.SpannableString(text);
            int startIndex = text.indexOf("Đăng nhập");
            int length = 9;
            if (startIndex == -1) {
                startIndex = text.indexOf("Login");
                length = 5;
            }
            if (startIndex == -1) {
                startIndex = text.indexOf("?");
                if (startIndex != -1) {
                    startIndex += 1;
                    while (startIndex < text.length() && Character.isWhitespace(text.charAt(startIndex))) {
                        startIndex++;
                    }
                    length = text.length() - startIndex;
                }
            }
            if (startIndex != -1 && startIndex + length <= text.length()) {
                int color = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimary);
                ss.setSpan(new android.text.style.ForegroundColorSpan(color),
                        startIndex, startIndex + length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvGoToLogin.setText(ss);
            tvGoToLogin.setOnClickListener(v -> {
                Navigation.findNavController(v).navigateUp();
            });
        }

        // 7. OBSERVE VIEWMODEL
        authViewModel.registerSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Đăng ký thành công! Vui lòng đăng nhập.", () -> {
                    Navigation.findNavController(view).navigateUp();
                });
            }
        });
        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                String lowercaseMsg = message.toLowerCase();
                if (lowercaseMsg.contains("email")) {
                    etEmail.setError(message);
                    etEmail.requestFocus();
                } else if (lowercaseMsg.contains("họ và tên") || lowercaseMsg.contains("tên")) {
                    etName.setError(message);
                    etName.requestFocus();
                } else if (lowercaseMsg.contains("mật khẩu")) {
                    etPassword.setError(message);
                    etPassword.requestFocus();
                } else {
                    DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
                }
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