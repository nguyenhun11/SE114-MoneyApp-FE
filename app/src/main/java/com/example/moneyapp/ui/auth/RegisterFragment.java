package com.example.moneyapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.moneyapp.R;
import com.google.android.material.button.MaterialButton;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnRegister = view.findViewById(R.id.btn_register);
        TextView tvGoToLogin = view.findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> {
            // Giả lập đăng ký thành công
            Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        tvGoToLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }
}
