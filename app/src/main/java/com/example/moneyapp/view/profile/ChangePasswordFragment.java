package com.example.moneyapp.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.viewmodel.ProfileViewModel;

public class ChangePasswordFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // UI Components
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        Button btnUpdate = view.findViewById(R.id.btn_update_password);
        EditText etCurrentPass = view.findViewById(R.id.et_current_password);
        EditText etNewPass = view.findViewById(R.id.et_new_password);
        EditText etConfirmPass = view.findViewById(R.id.et_confirm_new_password);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> {
                String oldPass = etCurrentPass.getText().toString().trim();
                String newPass = etNewPass.getText().toString().trim();
                String confirmPass = etConfirmPass.getText().toString().trim();
                
                profileViewModel.updatePassword(oldPass, newPass, confirmPass);
            });
        }

        // Observe kết quả
        profileViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                if (message.equals("SUCCESS")) {
                    DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Đổi mật khẩu thành công!", () -> {
                        Navigation.findNavController(requireView()).navigateUp();
                    });
                } else {
                    DialogHelper.showSimpleDialog(requireContext(), "Thông báo", message);
                }
                // Reset message để không bị Dialog lặp lại khi xoay màn hình
                profileViewModel.errorMessage.setValue(null);
            }
        });
    }
}
