package com.example.moneyapp.view.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
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

        // 1. ÉP KÍCH THƯỚC ẢNH CHUẨN 50% MÀN HÌNH (Đồng bộ với Splash)
        View cvHeader = view.findViewById(R.id.cv_login_header);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams params = cvHeader.getLayoutParams();
        params.height = (int) (screenHeight * 0.5); // Ép đúng 50% màn hình
        cvHeader.setLayoutParams(params);

        // 2. KÍCH HOẠT THANH CUỘN CHO BÀN PHÍM
        View rootView = view.findViewById(R.id.root_scroll_view);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                int bottomPadding = insets.getInsets(WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.systemBars()).bottom;
                v.setPadding(0, 0, 0, bottomPadding); // Đẩy nội dung lên khi có bàn phím
                return insets;
            });
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Ánh xạ UI - Đã cập nhật thành MaterialButton
        MaterialButton btnLogin = view.findViewById(R.id.btn_login);
        MaterialButton btnGoogleLogin = view.findViewById(R.id.btn_google_login);
        TextView tvGoToRegister = view.findViewById(R.id.tv_go_to_register);
        TextView tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPassword = view.findViewById(R.id.et_password);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String loginInput = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                authViewModel.login(loginInput, password);
            });
        }

        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(v -> {
                // Buộc Google Sign-In hiện hộp thoại chọn tài khoản bằng cách Logout trước khi tiến hành Launch
                googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                    googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
                });
            });
        }

        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            });
        }

        authViewModel.loginSuccess.observe(getViewLifecycleOwner(), user -> {
            Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", true).apply();
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });

        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            if (idToken != null) {
                authViewModel.loginWithGoogle(idToken);
            } else {
                Toast.makeText(requireContext(), "Google ID Token is null", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(requireContext(), "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
}