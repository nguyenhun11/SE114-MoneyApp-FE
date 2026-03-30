package com.example.moneyapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.moneyapp.R;

public class ForgotPasswordFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSend = view.findViewById(R.id.btn_send_instructions);
        TextView tvBack = view.findViewById(R.id.tv_back_to_login);

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Hướng dẫn đã được gửi đến email của bạn!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigateUp();
            });
        }

        if (tvBack != null) {
            tvBack.setOnClickListener(v -> {
                Navigation.findNavController(v).navigateUp();
            });
        }
    }
}
