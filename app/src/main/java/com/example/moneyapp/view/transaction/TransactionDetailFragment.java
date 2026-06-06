package com.example.moneyapp.view.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneyapp.R;
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
            // Bạn có thể cần bổ sung method loadTransactionById vào TransactionViewModel nếu chưa có
            // transactionViewModel.loadTransactionById(transactionId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã giao dịch", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel(View view) {
        // Giả sử bạn cập nhật TransactionViewModel để có LiveData cho 1 transaction duy nhất
        // Ở đây tôi lấy ví dụ sử dụng dữ liệu từ danh sách đã load hoặc bạn có thể bổ sung API getById
        
        TextView tvCategory    = view.findViewById(R.id.tvDetailCategoryLabel);
        TextView tvAmount      = view.findViewById(R.id.tvDetailAmount);
        TextView tvSource      = view.findViewById(R.id.tvDetailSource);
        TextView tvDate        = view.findViewById(R.id.tvDetailDate);
        TextView tvTime        = view.findViewById(R.id.tvDetailTime);
        TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
        TextView tvBadge       = view.findViewById(R.id.tvDetailBadge);

        // Logic cập nhật UI khi có dữ liệu từ TransactionViewModel...
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}
