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
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionFragment extends BaseFragment {

    private TransactionGroupAdapter adapter;
    private TransactionViewModel transactionViewModel;
    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;

    // region Filters
    private TimeSelectorView timeSelector;
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();
    private TextView tvAccountFilter;
    private TextView tvCategoryFilter;
    private LinearLayout btnCategoryFilter;
    // endregion

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
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupHeader(view, "Lịch sử giao dịch", false);
        setupFilters(view);

        timeSelector = view.findViewById(R.id.time_selector);

        // --- XỬ LÝ DỮ LIỆU TỪ MÀN HÌNH KHÁC TRUYỀN TỚI ---
        int preSelectedTab = 0; // Mặc định Tab 0
        if (getArguments() != null) {
            preSelectedTab = getArguments().getInt("tabType", 0);

            String categoryId = getArguments().getString("categoryId");
            String categoryName = getArguments().getString("categoryName"); // Truyền thêm biến này từ Home
            if (categoryId != null) {
                transactionViewModel.setCategoryFilterAndReload(categoryId);
                if (categoryName != null && tvCategoryFilter != null) {
                    tvCategoryFilter.setText(categoryName); // Ép UI đổi tên
                }
            }

            long startDateLong = getArguments().getLong("startDate", 0);
            long endDateLong = getArguments().getLong("endDate", 0);
            if (startDateLong > 0 && endDateLong > 0) {
                Date startDate = new Date(startDateLong);
                Date endDate = new Date(endDateLong);
                transactionViewModel.setTimeRangeAndReload(startDate, endDate);

                // Ép UI của TimeSelectorView hiển thị đúng thời gian
                timeSelector.setPredefinedDateRange(startDate, endDate);
            }
        }

        // --- CẬP NHẬT HÀM SETUP TABS ---
        // Bạn cần sửa hàm setupThreeTabs trong BaseFragment để nhận thêm tham số preSelectedTab
        setupThreeTabs(view, preSelectedTab, index -> {
            CategoryType type = null;
            if (index == 1) {
                type = CategoryType.EXPENSE;
                btnCategoryFilter.setVisibility(View.VISIBLE);
            } else if (index == 2) {
                type = CategoryType.INCOME;
                btnCategoryFilter.setVisibility(View.VISIBLE);
            } else {
                btnCategoryFilter.setVisibility(View.GONE);
            }

            transactionViewModel.setTypeAndReload(type);

            if (type != null) {
                categoryViewModel.loadCategories(type);
            } else {
                categoryViewModel.loadCategories(CategoryType.EXPENSE);
            }

            if (getArguments() == null) {
                if (tvCategoryFilter != null) {
                    tvCategoryFilter.setText("Tất cả hạng mục");
                    transactionViewModel.setCategoryFilterAndReload(null);
                }
            }
            setArguments(null);
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionGroupAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            args.putString("transactionId", transaction.getTransactionId());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            transactionViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        observeViewModels();
        accountViewModel.loadAccounts();
    }
    private void observeViewModels() {
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            adapter.updateList(items);
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
            }
        });

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

    private void setupFilters(View view) {
        LinearLayout btnAccountFilter = view.findViewById(R.id.btn_account_filter);
        btnCategoryFilter = view.findViewById(R.id.btn_category_filter);

        tvAccountFilter = view.findViewById(R.id.tv_selected_account);
        tvCategoryFilter = view.findViewById(R.id.tv_selected_category);

        btnAccountFilter.setOnClickListener(v -> showAccountFilterPopup());
        btnCategoryFilter.setOnClickListener(v -> showCategoryFilterPopup());
        btnCategoryFilter.setVisibility(View.GONE);
    }

    private void showAccountFilterPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentAccountId = transactionViewModel.getCurrentAccountId();
        PopupHelper.showAccountFilterPopup(requireContext(), accountList,
                currentAccountId,
                true,
                selectedAcc -> {
                    if (selectedAcc == null) {
                        if (tvAccountFilter != null) tvAccountFilter.setText("Tất cả tài khoản");
                        transactionViewModel.setAccountFilterAndReload(null);
                    } else {
                        if (tvAccountFilter != null) tvAccountFilter.setText(selectedAcc.getAccountName());
                        transactionViewModel.setAccountFilterAndReload(selectedAcc.getAccountId());
                    }
                });
    }

    private void showCategoryFilterPopup() {
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu hạng mục", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupHelper.showCategoryFilterPopup(requireContext(),
                categoryList,
                true,
                selectedCat -> {
                    if (selectedCat == null) {
                        if (tvCategoryFilter != null) tvCategoryFilter.setText("Tất cả hạng mục");
                        transactionViewModel.setCategoryFilterAndReload(null);
                    } else {
                        if (tvCategoryFilter != null) tvCategoryFilter.setText(selectedCat.getCategoryName());
                        transactionViewModel.setCategoryFilterAndReload(selectedCat.getCategoryId());
                    }
                });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}