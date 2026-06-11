package com.example.moneyapp.view.transaction;

import android.app.AlertDialog;
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
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends BaseFragment {

    private TransactionGroupAdapter adapter;
    private TransactionViewModel transactionViewModel;
    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;

    // Danh sách dữ liệu dùng cho Popup
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    // View text để hiển thị tên filter đã chọn
    private TextView tvAccountFilter;
    private TextView tvCategoryFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo các ViewModels
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupHeader(view, "Lịch sử giao dịch", false);

        setupThreeTabs(view, index -> {
            CategoryType type = null;
            if (index == 1) type = CategoryType.EXPENSE;
            else if (index == 2) type = CategoryType.INCOME;

            transactionViewModel.setTypeAndReload(type);

            // Khi đổi tab, cần tải lại danh sách Hạng mục tương ứng cho Popup Filter
            if (type != null) {
                categoryViewModel.loadCategories(type);
            } else {
                categoryViewModel.loadCategories(CategoryType.EXPENSE); // Fallback cho tab "Tất cả"
            }

            // Đặt lại text filter hạng mục về mặc định khi chuyển tab
            if (tvCategoryFilter != null) {
                tvCategoryFilter.setText("Hạng mục");
                transactionViewModel.setCategoryFilterAndReload(null);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionGroupAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            args.putString("transactionId", transaction.getTransactionId());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);
        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            transactionViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        setupFilters(view);
        observeViewModels();

        // Tải danh sách tài khoản ngay khi vào màn hình
        accountViewModel.loadAccounts();
    }

    private void observeViewModels() {
        // Lắng nghe dữ liệu Lịch sử giao dịch
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            adapter.updateList(items);
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Lắng nghe dữ liệu Tài khoản đổ vào mảng
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
            }
        });

        // Lắng nghe dữ liệu Hạng mục đổ vào mảng
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionViewModel.reloadTransactions();
    }

    // 2. Thiết lập bộ lọc (Popup Dialog)
    private void setupFilters(View view) {
        LinearLayout btnAccountFilter = view.findViewById(R.id.btn_account_filter);
        LinearLayout btnCategoryFilter = view.findViewById(R.id.btn_category_filter);

        // Cố gắng tìm TextView bên trong LinearLayout để đổi chữ (Giả định ID là tv_account_filter_name)
        // Nếu bạn chưa đặt ID cho TextView bên trong, hãy tìm theo index: (TextView) btnAccountFilter.getChildAt(0);
        tvAccountFilter = view.findViewById(R.id.tv_selected_account);
        tvCategoryFilter = view.findViewById(R.id.tv_selected_category);

        // Dự phòng nếu không tìm thấy ID, lấy trực tiếp View con
        if (tvAccountFilter == null && btnAccountFilter.getChildCount() > 0) {
            tvAccountFilter = (TextView) btnAccountFilter.getChildAt(0);
        }
        if (tvCategoryFilter == null && btnCategoryFilter.getChildCount() > 0) {
            tvCategoryFilter = (TextView) btnCategoryFilter.getChildAt(0);
        }

        btnAccountFilter.setOnClickListener(v -> showAccountFilterPopup());
        btnCategoryFilter.setOnClickListener(v -> showCategoryFilterPopup());
    }

    private void showAccountFilterPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi Popup xịn xò từ PopupHelper
        com.example.moneyapp.utils.PopupHelper.showAccountFilterPopup(requireContext(), accountList, selectedAcc -> {
            // Khi người dùng chọn xong, thực hiện filter
            if (tvAccountFilter != null) tvAccountFilter.setText(selectedAcc.getAccountName());
            transactionViewModel.setAccountFilterAndReload(selectedAcc.getAccountId());
        });
    }

    private void showCategoryFilterPopup() {
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu hạng mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi Popup xịn xò từ PopupHelper
        com.example.moneyapp.utils.PopupHelper.showCategoryFilterPopup(requireContext(), categoryList, selectedCat -> {
            // Khi người dùng chọn xong, thực hiện filter
            if (tvCategoryFilter != null) tvCategoryFilter.setText(selectedCat.getCategoryName());
            transactionViewModel.setCategoryFilterAndReload(selectedCat.getCategoryId());
        });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}