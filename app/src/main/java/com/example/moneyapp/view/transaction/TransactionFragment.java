package com.example.moneyapp.view.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionFragment extends BaseFragment {

    private TransactionGroupAdapter adapter;
    private TransactionViewModel transactionViewModel;

    private String selectedTime = "all";
    private String selectedSource = "Tất cả";
    private String selectedCategory = "Tất cả";
    private CategoryType selectedTransactionType = null;

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

        // Hiển thị số dư lên thẻ Account
        setupBalanceSelector(view, getString(R.string.total_balance), "0", true);
        setupThreeTabs(view, index -> {
            if (index == 0) {
                selectedTransactionType = null;
            } else if (index == 1) {
                selectedTransactionType = CategoryType.EXPENSE;
            } else if (index == 2) {
                selectedTransactionType = CategoryType.INCOME;
            }
            loadData();
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // KHỞI TẠO ADAPTER NHÓM
        adapter = new TransactionGroupAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            // Đảm bảo dùng đúng phương thức lấy ID (thường là getId() hoặc getTransactionId())
            args.putString("transactionId", transaction.getTransactionId());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);
        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            // Component đã tính sẵn chính xác đến từng mili-giây, bạn chỉ việc quăng thẳng vào ViewModel!

            transactionViewModel.loadTransactions(startDate, endDate, selectedTransactionType, null, null);
            transactionViewModel.loadTotalBalance();
        });

        // GỌI CÁC HÀM SETUP UI
        setupTimeFilters(view);
        setupCategoryFilter(view); // Đã thêm dòng gọi hàm này để nút Hạng mục có tác dụng

        observeViewModel();
    }

    private void observeViewModel() {
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            // Đổ dữ liệu phân nhóm (DailyTransactionGroup) vào Adapter
            adapter.updateList(items);
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        transactionViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            setupBalanceSelector(requireView(), getString(R.string.total_balance),
                    String.format(java.util.Locale.getDefault(), "%,.0f", balance).replace(",", "."), true);
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

        transactionViewModel.loadTransactions(startDate, endDate, selectedTransactionType, null, null);
        transactionViewModel.loadTotalBalance();
    }

    private void setupCategoryFilter(View view) {
        LinearLayout btnCategoryFilter = view.findViewById(R.id.btn_category_filter);
        TextView tvSelectedCategory = view.findViewById(R.id.tv_selected_category);

        btnCategoryFilter.setOnClickListener(v -> {
            // Mở BottomSheetDialog chọn hạng mục
            Toast.makeText(getContext(), "Mở menu chọn hạng mục", Toast.LENGTH_SHORT).show();
        });
    }
    private void setupTimeFilters(View view) {
//        TextView btnAll = view.findViewById(R.id.btnFilterAll);
//        TextView btnToday = view.findViewById(R.id.btnFilterToday);
//        TextView btnWeek = view.findViewById(R.id.btnFilterWeek);
//        TextView btnMonth = view.findViewById(R.id.btnFilterMonth);
//
//        List<TextView> buttons = List.of(btnAll, btnToday, btnWeek, btnMonth);
//        List<String> values = List.of("all", "today", "week", "month");
//
//        for (int i = 0; i < buttons.size(); i++) {
//            int index = i;
//            buttons.get(i).setOnClickListener(v -> {
//                selectedTime = values.get(index);
//                for (TextView btn : buttons) {
//                    btn.setBackgroundResource(R.drawable.bg_filter_unselected);
//                    // Dùng màu tĩnh hoặc đổi thành getResources().getColor() tùy thích
//                    btn.setTextColor(Color.parseColor("#2B3674"));
//                }
//                buttons.get(index).setBackgroundResource(R.drawable.bg_filter_selected);
//                buttons.get(index).setTextColor(Color.WHITE);
//                loadData();
//            });
//        }
    }

//    private void setupSpinners(View view) {
//        Spinner spinnerSource = view.findViewById(R.id.spinnerFilterSource);
//        Spinner spinnerCategory = view.findViewById(R.id.spinnerFilterCategory);
//
//        String[] sources = {"Tất cả", "Tiền mặt", "Momo", "Ngân hàng"};
//        spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, sources));
//        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
//                selectedSource = sources[pos];
//                // Gọi loadData() với filter sourceId
//            }
//            @Override public void onNothingSelected(AdapterView<?> p) {}
//        });
//    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}