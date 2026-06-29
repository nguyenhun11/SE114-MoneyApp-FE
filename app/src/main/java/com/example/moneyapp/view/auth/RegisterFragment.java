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


        // Đổi từ MaterialButton sang Button để tránh ClassCastException khi dùng AppCompatButton trong XML
        Button btnRegister = view.findViewById(R.id.btn_register);
        TextView tvGoToLogin = view.findViewById(R.id.tv_go_to_login);
        EditText etName = view.findViewById(R.id.et_name_register);
        EditText etEmail = view.findViewById(R.id.et_email_register);
        EditText etPassword = view.findViewById(R.id.et_password_register);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password_register);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                authViewModel.register(
                        etName.getText().toString(),
                        etEmail.getText().toString(),
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString()
                );
            });
        }

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

        authViewModel.registerSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Đăng ký thành công! Vui lòng đăng nhập.", () -> {
                    Navigation.findNavController(view).navigateUp();
                });
            }
        });
        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
        });
    }
}
