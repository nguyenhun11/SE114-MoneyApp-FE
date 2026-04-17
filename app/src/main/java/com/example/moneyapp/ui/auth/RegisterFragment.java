package com.example.moneyapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.moneyapp.R;
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
        EditText etEmail = view.findViewById(R.id.et_email_register);
        EditText etPassword = view.findViewById(R.id.et_password_register);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password_register);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                authViewModel.register(
                        etEmail.getText().toString(),
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString()
                );
            });
        }

        if (tvGoToLogin != null) {
            tvGoToLogin.setOnClickListener(v -> {
                Navigation.findNavController(v).navigateUp();
            });
        }

        authViewModel.registerSuccess.observe(getViewLifecycleOwner(), user -> {
            Toast.makeText(requireContext(), "Register successful", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });
        authViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}
