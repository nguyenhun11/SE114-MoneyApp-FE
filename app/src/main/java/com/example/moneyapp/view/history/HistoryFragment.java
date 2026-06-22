package com.example.moneyapp.view.history;

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
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.HistoryViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends BaseFragment {

    private HistoryGroupAdapter adapter;
    private HistoryViewModel historyViewModel;
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupHeader(view, "Lịch sử giao dịch", false);

        String[] historyTabs = {
                "Tất cả",
                "Chi tiêu",
                "Thu nhập",
                "Chuyển khoản",
                "Điều chỉnh số dư",
                "Tiết kiệm"
        };
        setupHeaderTabs(view, historyTabs,0, index -> {
            CategoryType type = null;

            switch (index) {
                case 0: // Tất cả
                    type = null;
                    break;
                case 1: // Chi tiêu
                    type = CategoryType.EXPENSE;
                    break;
                case 2: // Thu nhập
                    type = CategoryType.INCOME;
                    break;
                case 3: // Chuyển khoản (Có thể lọc bằng logic bên ViewModel sau)
                    break;
                case 4: // Điều chỉnh số dư
                    break;
                case 5: // Tiết kiệm (Ăn gian vào chi tiêu)
                    break;
            }
            historyViewModel.setTypeAndReload(type);
        });

        setupFilters(view);

        timeSelector = view.findViewById(R.id.time_selector);

        int preSelectedTab = 0; // Mặc định Tab 0
        if (getArguments() != null) {
            preSelectedTab = getArguments().getInt("tabType", 0);

            String categoryId = getArguments().getString("categoryId");
            String categoryName = getArguments().getString("categoryName");
            if (categoryId != null) {
                historyViewModel.setCategoryFilterAndReload(categoryId);
                if (categoryName != null && tvCategoryFilter != null) {
                    tvCategoryFilter.setText(categoryName);
                }
            }

            long startDateLong = getArguments().getLong("startDate", 0);
            long endDateLong = getArguments().getLong("endDate", 0);
            if (startDateLong > 0 && endDateLong > 0) {
                Date startDate = new Date(startDateLong);
                Date endDate = new Date(endDateLong);
                historyViewModel.setTimeRangeAndReload(startDate, endDate);

                timeSelector.setPredefinedDateRange(startDate, endDate);
            }
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String systemCurrency = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();

        adapter = new HistoryGroupAdapter(new ArrayList<>(), accountList, systemCurrency, item -> {
            Bundle args = new Bundle();
            if (item.getType() == HistoryItem.TYPE_TRANSACTION) {
                args.putString("transactionId", item.getTransaction().getTransactionId());
                Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
            } else if (item.getType() == HistoryItem.TYPE_TRANSFER) {
                args.putString("transferId", item.getTransfer().getId());
                Navigation.findNavController(view).navigate(R.id.transferDetailFragment, args);
            } else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
                // Điều chỉnh số dư thường không có trang chi tiết, chỉ cần toast nhẹ hoặc bỏ qua
                Toast.makeText(getContext(), "Đây là bản ghi điều chỉnh số dư hệ thống", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            historyViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        observeViewModels();
        accountViewModel.loadAccounts();
    }

    private void observeViewModels() {
        historyViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            adapter.updateData(items, accountList);
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                if (adapter != null && historyViewModel.getGroupedTransactions().getValue() != null) {
                    adapter.updateData(historyViewModel.getGroupedTransactions().getValue(), accountList);
                }
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
        historyViewModel.reloadTransactions();
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
        String currentAccountId = historyViewModel.getCurrentAccountId();
        PopupHelper.showAccountFilterPopup(requireContext(), accountList,
                currentAccountId,
                true,
                selectedAcc -> {
                    if (selectedAcc == null) {
                        if (tvAccountFilter != null) tvAccountFilter.setText("Tất cả tài khoản");
                        historyViewModel.setAccountFilterAndReload(null);
                    } else {
                        if (tvAccountFilter != null) tvAccountFilter.setText(selectedAcc.getAccountName());
                        historyViewModel.setAccountFilterAndReload(selectedAcc.getAccountId());
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
                        historyViewModel.setCategoryFilterAndReload(null);
                    } else {
                        if (tvCategoryFilter != null) tvCategoryFilter.setText(selectedCat.getCategoryName());
                        historyViewModel.setCategoryFilterAndReload(selectedCat.getCategoryId());
                    }
                });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}