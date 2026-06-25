package com.example.moneyapp.view.auth;

import android.os.Bundle;
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

public class ForgotPasswordFragment extends Fragment {
    private AuthViewModel authViewModel;
    private EditText etEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        etEmail = view.findViewById(R.id.et_email_forgot);
        Button btnSend = view.findViewById(R.id.btn_send_instructions);
        TextView tvBack = view.findViewById(R.id.tv_back_to_login);

        observeViewModel();

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                authViewModel.resetPassword(email);
            });
        }

        if (tvBack != null) {
            tvBack.setOnClickListener(v -> {
                Navigation.findNavController(v).navigateUp();
            });
        }
    }

    private void observeViewModel() {
        authViewModel.resetPasswordSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", 
                        "Yêu cầu đã được gửi! Vui lòng kiểm tra email của bạn để lấy mã xác nhận.", () -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", etEmail.getText().toString().trim());
                    Navigation.findNavController(requireView()).navigate(R.id.action_forgotPasswordFragment_to_resetPasswordFragment, bundle);
                });
            }
        });

        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
                // Xóa message sau khi hiển thị để tránh hiện lại khi xoay màn hình
                authViewModel.errorMessage.setValue(null);
            }
        });
    }
}
