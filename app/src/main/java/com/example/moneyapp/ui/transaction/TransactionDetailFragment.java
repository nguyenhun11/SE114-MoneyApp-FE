package com.example.moneyapp.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;

public class TransactionDetailFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, R.string.transaction_detail_title, true);

        // Đọc dữ liệu được truyền từ TransactionFragment
        Bundle args = getArguments();
        if (args == null) return;

        String category    = args.getString("category", "-");
        String amount      = args.getString("amount", "0");
        String source      = args.getString("source", "-");
        String date        = args.getString("date", "-");
        String time        = args.getString("time", "-");
        String description = args.getString("description", "-");
        String type        = args.getString("type", "chi");

        // Gán vào các TextView
        TextView tvCategory    = view.findViewById(R.id.tvDetailCategoryLabel);
        TextView tvAmount      = view.findViewById(R.id.tvDetailAmount);
        TextView tvSource      = view.findViewById(R.id.tvDetailSource);
        TextView tvDate        = view.findViewById(R.id.tvDetailDate);
        TextView tvTime        = view.findViewById(R.id.tvDetailTime);
        TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
        TextView tvBadge       = view.findViewById(R.id.tvDetailBadge);

        tvCategory.setText(category);
        tvSource.setText(source);
        tvDate.setText(date);
        tvTime.setText(time);
        tvDescription.setText(description.isEmpty() ? "-" : description);

        // Hiển thị số tiền + màu theo loại
        if ("chi".equals(type)) {
            tvAmount.setText("- " + amount + "đ");
            tvAmount.setTextColor(0xFFE8435A); // đỏ
            tvBadge.setText("Chi tiêu");
        } else {
            tvAmount.setText("+ " + amount + "đ");
            tvAmount.setTextColor(0xFF4CAF50); // xanh
            tvBadge.setText("Thu nhập");
        }

    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}