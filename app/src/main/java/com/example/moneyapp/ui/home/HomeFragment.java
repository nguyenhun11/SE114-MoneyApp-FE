package com.example.moneyapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;

public class HomeFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sử dụng hàm dùng chung từ BaseFragment
        setupIncomeExpenseTabs(view, isExpense -> {
            // Xử lý logic riêng của HomeFragment khi chuyển tab nếu cần
            if (isExpense) {
                // Show expense data
            } else {
                // Show income data
            }
        });
    }
}
