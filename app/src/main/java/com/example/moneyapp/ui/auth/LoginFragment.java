package com.example.moneyapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.moneyapp.ui.MainActivity;
import com.example.moneyapp.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        Button btnLogin = view.findViewById(R.id.btn_login);
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
}
