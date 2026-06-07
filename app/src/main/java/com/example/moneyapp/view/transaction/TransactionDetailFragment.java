package com.example.moneyapp.view.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.TransactionViewModel;

public class TransactionDetailFragment extends BaseFragment {

    private TransactionViewModel transactionViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, R.string.transaction_detail_title, true);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        Bundle args = getArguments();
        if (args != null && args.containsKey("transactionId")) {
            String transactionId = args.getString("transactionId");
            observeViewModel(view);
            transactionViewModel.loadTransactionById(transactionId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã giao dịch", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel(View view) {
        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), t -> {
            if (t == null) return;

            TextView tvCategory    = view.findViewById(R.id.tvDetailCategoryLabel);
            TextView tvAmount      = view.findViewById(R.id.tvDetailAmount);
            TextView tvSource      = view.findViewById(R.id.tvDetailSource);
            TextView tvDate        = view.findViewById(R.id.tvDetailDate);
            TextView tvTime        = view.findViewById(R.id.tvDetailTime);
            TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
            TextView tvBadge       = view.findViewById(R.id.tvDetailBadge);

            tvCategory.setText(t.getCategoryName() != null ? t.getCategoryName() : "Hạng mục");
            tvSource.setText(t.getAccountName() != null ? t.getAccountName() : "Ví");
            tvDate.setText(t.getFormattedDate());
            tvTime.setText(t.getFormattedTime());
            tvDescription.setText(t.getDescription() != null && !t.getDescription().isEmpty() ? t.getDescription() : "-");

            if (t.getAmount() != null && t.getAmount() < 0) {
                tvAmount.setText(t.getFormattedAmount() + "đ");
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                tvBadge.setText("Chi tiêu");
            } else {
                tvAmount.setText("+" + t.getFormattedAmount() + "đ");
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
                tvBadge.setText("Thu nhập");
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}
