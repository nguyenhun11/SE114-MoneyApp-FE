package com.example.moneyapp.view.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {
    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. ÉP KÍCH THƯỚC ẢNH ĐỒNG BỘ VỚI SPLASH
        View cvHeader = view.findViewById(R.id.cv_login_header);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams params = cvHeader.getLayoutParams();
        params.height = (int) (screenHeight * 0.5); // Chỉnh 0.35 - 0.4 để khớp 100% với Splash
        cvHeader.setLayoutParams(params);

        // 2. KHỞI TẠO VIEWMODEL VÀ GOOGLE CLIENT
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // 3. ÁNH XẠ UI
        androidx.core.widget.NestedScrollView rootScrollView = view.findViewById(R.id.root_scroll_view);
        androidx.appcompat.widget.AppCompatButton btnLogin = view.findViewById(R.id.btn_login);
        MaterialButton btnGoogleLogin = view.findViewById(R.id.btn_google_login);
        TextView tvGoToRegister = view.findViewById(R.id.tv_go_to_register);
        TextView tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPassword = view.findViewById(R.id.et_password);
        com.mikepenz.iconics.view.IconicsImageView ivShowPassword = view.findViewById(R.id.iv_show_password);

        // 4. XỬ LÝ NÚT HIỆN/ẨN MẬT KHẨU
        final boolean[] isPasswordVisible = {false};
        ivShowPassword.setOnClickListener(v -> {
            isPasswordVisible[0] = !isPasswordVisible[0];

            int iconColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

            if (isPasswordVisible[0]) {
                // Hiện mật khẩu
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                com.mikepenz.iconics.IconicsDrawable icon = new com.mikepenz.iconics.IconicsDrawable(requireContext(), "gmd-visibility");
                icon.setColorFilter(iconColor, android.graphics.PorterDuff.Mode.SRC_IN);
                ivShowPassword.setIcon(icon);
            } else {
                // Ẩn mật khẩu
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                com.mikepenz.iconics.IconicsDrawable icon = new com.mikepenz.iconics.IconicsDrawable(requireContext(), "gmd-visibility-off");
                icon.setColorFilter(iconColor, android.graphics.PorterDuff.Mode.SRC_IN);
                ivShowPassword.setIcon(icon);
            }

            // Đặt lại font chữ và đưa con trỏ về cuối dòng
            etPassword.setTypeface(android.graphics.Typeface.DEFAULT);
            etPassword.setSelection(etPassword.getText().length());
        });

        // 4. BƠM KHÔNG GIAN VÀ TỰ ĐỘNG CUỘN CHÍNH XÁC KHI BÀN PHÍM HIỆN
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootScrollView, (v, insets) -> {
            int imeHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom;
            int navHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).bottom;
            int paddingBottom = Math.max(imeHeight, navHeight);

            // 💥 Bí quyết ở đây: Đệm phần đáy bằng đúng chiều cao bàn phím để có dư không gian cuộn
            v.setPadding(0, 0, 0, paddingBottom);

            // Khi bàn phím mở, tự động đưa ô đang gõ lên trên bàn phím
            if (imeHeight > 0) {
                v.postDelayed(() -> {
                    View focus = view.findFocus();
                    if (focus == etEmail || focus == etPassword) {
                        View target = (focus == etPassword) ? view.findViewById(R.id.fl_password_wrapper) : focus;
                        if (target != null) {
                            int visibleHeight = v.getHeight() - paddingBottom;
                            // Tính khoảng cách: Lấy đáy của ô + 80px đệm - vùng hiển thị
                            int scrollY = target.getBottom() + 80 - visibleHeight;
                            rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                        }
                    }
                }, 100);
            }
            return insets;
        });

        // 5. XỬ LÝ KHI NGƯỜI DÙNG TỰ BẤM CHUYỂN QUA LẠI GIỮA EMAIL VÀ PASSWORD
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus) {
                rootScrollView.postDelayed(() -> {
                    View target = (v == etPassword) ? view.findViewById(R.id.fl_password_wrapper) : v;
                    if (target != null) {
                        int visibleHeight = rootScrollView.getHeight() - rootScrollView.getPaddingBottom();
                        int scrollY = target.getBottom() + 80 - visibleHeight;
                        rootScrollView.smoothScrollTo(0, Math.max(0, scrollY));
                    }
                }, 150);
            }
        };
        etEmail.setOnFocusChangeListener(focusListener);
        etPassword.setOnFocusChangeListener(focusListener);

        // 6. GÕ XONG BẤM "HOÀN TẤT" LÀ ĐĂNG NHẬP LUÔN
        if (etPassword != null && btnLogin != null) {
            etPassword.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    // Đóng bàn phím
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Kích hoạt click nút Đăng nhập
                    btnLogin.performClick();
                    return true;
                }
                return false;
            });
        }

        // 7. XỬ LÝ CLICK ĐĂNG NHẬP
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String loginInput = etEmail != null ? etEmail.getText().toString() : "";
                String password = etPassword != null ? etPassword.getText().toString() : "";
                authViewModel.login(loginInput, password);
            });
        }

        // 8. XỬ LÝ CLICK GOOGLE LOGIN
        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(v -> {
                googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                    googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
                });
            });
        }

        // 9. CHUYỂN SANG TRANG ĐĂNG KÝ (Tô màu chữ động)
        if (tvGoToRegister != null) {
            String text = getString(R.string.login_no_account);
            android.text.SpannableString ss = new android.text.SpannableString(text);
            int startIndex = text.indexOf("Đăng ký");
            int length = 7;
            if (startIndex == -1) {
                startIndex = text.indexOf("Register");
                length = 8;
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
            tvGoToRegister.setText(ss);
            tvGoToRegister.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            });
        }

        // 10. QUÊN MẬT KHẨU
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            });
        }

        // 11. OBSERVE VIEWMODEL
        authViewModel.loginSuccess.observe(getViewLifecycleOwner(), user -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", true).apply();
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            requireActivity().finish();
        });

        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            if (idToken != null) {
                authViewModel.loginWithGoogle(idToken);
            } else {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", "Google ID Token is null");
            }
        } catch (ApiException e) {
            DialogHelper.showSimpleDialog(requireContext(), "Lỗi đăng nhập", "Google Sign-In failed: " + e.getStatusCode());
        }
    }
}