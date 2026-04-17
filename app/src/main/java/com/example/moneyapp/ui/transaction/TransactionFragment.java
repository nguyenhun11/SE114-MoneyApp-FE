package com.example.moneyapp.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.adapter.TransactionAdapter;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBalanceSelector(view, "Tổng cộng", "2.500.000", true);
        setupIncomeExpenseTabs(view, isExpense -> {});

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction("Ăn uống",    "Bữa sáng",       "-50.000đ",  "07:30"));
        transactionList.add(new Transaction("Di chuyển",  "Grab đi làm",    "-30.000đ",  "08:15"));
        transactionList.add(new Transaction("Lương",      "Lương tháng 4",  "+5.000.000đ","09:00"));
        transactionList.add(new Transaction("Mua sắm",    "Siêu thị",       "-200.000đ", "12:30"));
        transactionList.add(new Transaction("Giải trí",   "Xem phim",       "-80.000đ",  "19:00"));

        TransactionAdapter adapter = new TransactionAdapter(transactionList, transaction -> {
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment);
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.tv_goto_detail).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.transactionDetailFragment);
        });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}