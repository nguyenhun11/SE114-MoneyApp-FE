package com.example.moneyapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.moneyapp.R;
import com.example.moneyapp.ui.MainActivity;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sử dụng Button thay vì MaterialButton để tránh ClassCastException
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView tvGoToRegister = view.findViewById(R.id.tv_go_to_register);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Save login state
                SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("isLoggedIn", true).apply();

                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            });
        }

        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            });
        }
    }
}
