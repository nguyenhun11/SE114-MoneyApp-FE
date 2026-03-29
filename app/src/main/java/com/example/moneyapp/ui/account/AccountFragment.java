package com.example.moneyapp.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;


public class AccountFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view, R.string.account, false);
    }

    @Override
    protected int getFabIcon() {
        return R.drawable.ic_transfer;
    }

    @Override
    protected void onFabClick() {
        // Thực hiện hành động chuyển khoản
        Toast.makeText(getContext(), "Mở màn hình chuyển khoản", Toast.LENGTH_SHORT).show();
    }
}
