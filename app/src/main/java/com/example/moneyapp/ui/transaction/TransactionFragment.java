package com.example.moneyapp.ui.transaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionFragment extends BaseFragment {

    private TransactionAdapter adapter;
    private TransactionViewModel transactionViewModel;
    private TextView tvTotalBalance;

    private String selectedTime = "all";
    private String selectedSource = "Tất cả";
    private String selectedCategory = "Tất cả";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);

        setupBalanceSelector(view, getString(R.string.total_balance), "0", true);
        setupIncomeExpenseTabs(view, isExpense -> {
            // Lọc theo loại chi phí/thu nhập nếu cần
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            args.putString("transactionId", transaction.getTransactionId());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        setupTimeFilters(view);
        setupSpinners(view);
        observeViewModel();
    }

    private void observeViewModel() {
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            adapter.updateList(items);
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        
        if ("today".equals(selectedTime)) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
        } else if ("week".equals(selectedTime)) {
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        } else if ("month".equals(selectedTime)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            cal.add(Calendar.YEAR, -1);
        }
        Date startDate = cal.getTime();

        transactionViewModel.loadTransactions(startDate, endDate, null, null, null);
    }

    private void setupTimeFilters(View view) {
        TextView btnAll = view.findViewById(R.id.btnFilterAll);
        TextView btnToday = view.findViewById(R.id.btnFilterToday);
        TextView btnWeek = view.findViewById(R.id.btnFilterWeek);
        TextView btnMonth = view.findViewById(R.id.btnFilterMonth);

        List<TextView> buttons = List.of(btnAll, btnToday, btnWeek, btnMonth);
        List<String> values = List.of("all", "today", "week", "month");

        for (int i = 0; i < buttons.size(); i++) {
            int index = i;
            buttons.get(i).setOnClickListener(v -> {
                selectedTime = values.get(index);
                for (TextView btn : buttons) {
                    btn.setBackgroundResource(R.drawable.bg_filter_unselected);
                    btn.setTextColor(Color.parseColor("#1A1A1A"));
                }
                buttons.get(index).setBackgroundResource(R.drawable.bg_filter_selected);
                buttons.get(index).setTextColor(Color.WHITE);
                loadData();
            });
        }
    }

    private void setupSpinners(View view) {
        Spinner spinnerSource = view.findViewById(R.id.spinnerFilterSource);
        Spinner spinnerCategory = view.findViewById(R.id.spinnerFilterCategory);

        // Tạm thời để dữ liệu tĩnh, sau này có thể lấy từ AccountViewModel/CategoryViewModel
        String[] sources = {"Tất cả", "Tiền mặt", "Momo", "Ngân hàng"};
        spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, sources));
        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedSource = sources[pos];
                // Gọi loadData() với filter sourceId
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}
