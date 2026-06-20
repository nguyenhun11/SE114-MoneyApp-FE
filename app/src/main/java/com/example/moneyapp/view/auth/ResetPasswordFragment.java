package com.example.moneyapp.view.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
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

        etToken = view.findViewById(R.id.et_reset_token);
        etNewPassword = view.findViewById(R.id.et_reset_password);
        etConfirmPassword = view.findViewById(R.id.et_reset_confirm_password);
        AppCompatButton btnReset = view.findViewById(R.id.btn_reset_password);
        TextView tvBack = view.findViewById(R.id.tv_reset_back_to_login);

        observeViewModel();

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
                new AlertDialog.Builder(requireContext())
                        .setTitle("Thành công")
                        .setMessage(getString(R.string.reset_password_success))
                        .setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
                            Navigation.findNavController(requireView()).popBackStack(R.id.loginFragment, false);
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Lỗi")
                        .setMessage(message)
                        .setPositiveButton("Thử lại", null)
                        .show();
                authViewModel.errorMessage.setValue(null);
            }
        });

        authViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            // Handle loading state
        });
    }
}
