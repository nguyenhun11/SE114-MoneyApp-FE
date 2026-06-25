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
import com.example.moneyapp.model.AdjustBalance;
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
    private TextView tvDestAccountFilter;
    private LinearLayout btnDestAccountFilter;
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
        setupFilters(view);

        // =========================================================
        // BƯỚC 1: XÁC ĐỊNH TAB KHỞI ĐẦU (ĐỒNG BỘ SHAREDPREFS)
        // =========================================================
        int initialTab = 0; // Mặc định là Tab 0 (Tất cả)

        if (getArguments() != null && getArguments().containsKey("tabType")) {
            // Ưu tiên 1: Lấy từ lệnh điều hướng (VD: Bấm từ Home sang)
            initialTab = getArguments().getInt("tabType", 0);
        } else {
            // Ưu tiên 2: Lấy bộ nhớ đệm toàn cục
            int globalType = PreferenceManager.getInstance(requireContext()).getLastTabType();
            // Dịch ngược từ Chuẩn chung (0: Chi, 1: Thu, 2: Chuyển khoản) sang Tab của History
            if (globalType == 0) initialTab = 1;      // Chi tiêu
            else if (globalType == 1) initialTab = 2; // Thu nhập
            else if (globalType == 2) initialTab = 3; // Chuyển khoản
        }

        // =========================================================
        // BƯỚC 2: CẤU HÌNH TABS VÀ XỬ LÝ SỰ KIỆN ĐỔI TAB
        // =========================================================
        String[] historyTabs = { "Tất cả", "Chi tiêu", "Thu nhập", "Chuyển khoản", "Điều chỉnh số dư", "Tiết kiệm" };

        setupHeaderTabs(view, historyTabs, initialTab, index -> {
            // CHỈ LƯU VÀO BỘ NHỚ NẾU LÀ THU/CHI/CHUYỂN KHOẢN (Để đồng bộ với Home/Entry)
            if (index >= 1 && index <= 3) {
                int globalTypeToSave = index - 1; // Dịch lại: 1->0, 2->1, 3->2
                PreferenceManager.getInstance(requireContext()).setLastTabType(globalTypeToSave);
            }

            // Xử lý UI Filter theo Tab
            if (index == 3) { // Tab Chuyển khoản
                btnCategoryFilter.setVisibility(View.GONE);
                btnDestAccountFilter.setVisibility(View.VISIBLE);

                historyViewModel.setCategoryFilter(null);
                if (tvCategoryFilter != null) tvCategoryFilter.setText("Tất cả hạng mục");

            } else if (index == 4 || index == 0) { // Điều chỉnh số dư hoặc Tất cả
                btnCategoryFilter.setVisibility(View.GONE);
                btnDestAccountFilter.setVisibility(View.GONE);

                historyViewModel.setCategoryFilter(null);
                historyViewModel.setDestAccountFilter(null);
                if (tvCategoryFilter != null) tvCategoryFilter.setText("Tất cả hạng mục");
                if (tvDestAccountFilter != null) tvDestAccountFilter.setText("Tài khoản đến");

            } else {
                // Tab Thu/Chi/Tiết kiệm -> Hiện Hạng mục, Ẩn Tài khoản đến
                btnCategoryFilter.setVisibility(View.VISIBLE);
                btnDestAccountFilter.setVisibility(View.GONE);

                historyViewModel.setDestAccountFilter(null);
                if (tvDestAccountFilter != null) tvDestAccountFilter.setText("Tài khoản đến");

                if (index == 1 || index == 5) {
                    categoryViewModel.loadCategories(CategoryType.EXPENSE);
                } else if (index == 2) {
                    categoryViewModel.loadCategories(CategoryType.INCOME);
                }
            }

            // Tải lại dữ liệu theo Tab
            historyViewModel.setFilterAndReload(index);
        });

        // =========================================================
        // BƯỚC 3: XỬ LÝ LỌC THỜI GIAN & HẠNG MỤC TỪ ARGUMENTS
        // =========================================================
        timeSelector = view.findViewById(R.id.time_selector);

        if (getArguments() != null) {
            // ĐÃ XÓA BỎ BIẾN "preSelectedTab" THỪA THÃI Ở ĐÂY!

            String categoryId = getArguments().getString("categoryId");
            String categoryName = getArguments().getString("categoryName");
            if (categoryId != null) {
                historyViewModel.setCategoryFilter(categoryId);
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

        // =========================================================
        // BƯỚC 4: CẤU HÌNH RECYCLERVIEW
        // =========================================================
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
                AdjustBalance adjust = item.getAdjustBalance();
                if (adjust != null) {
                    args.putString("adjustId", adjust.getId());
                    args.putString("accountId", adjust.getAccountId());
                    args.putString("accountName", adjust.getAccountName());
                    args.putDouble("amount", adjust.getAmount());

                    long timeInMillis = (adjust.getCreatedAt() != null) ? adjust.getCreatedAt().getTime() : 0;
                    args.putLong("createdAt", timeInMillis);

                    Navigation.findNavController(view).navigate(R.id.adjustBalanceDetailFragment, args);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        // =========================================================
        // BƯỚC 5: LẮNG NGHE SỰ KIỆN & LOAD DỮ LIỆU CHUNG
        // =========================================================
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
        btnDestAccountFilter = view.findViewById(R.id.btn_dest_account_filter);

        tvAccountFilter = view.findViewById(R.id.tv_selected_account);
        tvCategoryFilter = view.findViewById(R.id.tv_selected_category);
        tvDestAccountFilter = view.findViewById(R.id.tv_selected_dest_account);

        btnAccountFilter.setOnClickListener(v -> showAccountFilterPopup());
        btnCategoryFilter.setOnClickListener(v -> showCategoryFilterPopup());
        btnDestAccountFilter.setOnClickListener(v -> showDestAccountFilterPopup());

        btnCategoryFilter.setVisibility(View.VISIBLE);
        btnDestAccountFilter.setVisibility(View.GONE);
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
                    if (selectedAcc != null) {
                        String destAccountId = historyViewModel.getCurrentDestAccountId();
                        if (destAccountId != null && destAccountId.equals(selectedAcc.getAccountId())) {
                            Toast.makeText(getContext(), "Tài khoản nguồn không được trùng với tài khoản đến!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (selectedAcc == null) {
                        if (tvAccountFilter != null) tvAccountFilter.setText("Tất cả tài khoản");
                        historyViewModel.setAccountFilterAndReload(null);
                    } else {
                        if (tvAccountFilter != null) tvAccountFilter.setText(selectedAcc.getAccountName());
                        historyViewModel.setAccountFilterAndReload(selectedAcc.getAccountId());
                    }
                });
    }

    private void showDestAccountFilterPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentDestAccountId = historyViewModel.getCurrentDestAccountId();
        PopupHelper.showAccountFilterPopup(requireContext(), accountList,
                currentDestAccountId,
                true,
                selectedAcc -> {
                    if (selectedAcc != null) {
                        String sourceAccountId = historyViewModel.getCurrentAccountId();
                        if (sourceAccountId != null && sourceAccountId.equals(selectedAcc.getAccountId())) {
                            Toast.makeText(getContext(), "Tài khoản đến không được trùng với tài khoản nguồn!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (selectedAcc == null) {
                        if (tvDestAccountFilter != null)
                            tvDestAccountFilter.setText("Tất cả tài khoản đến");
                        historyViewModel.setDestAccountFilter(null);
                    } else {
                        if (tvDestAccountFilter != null)
                            tvDestAccountFilter.setText(selectedAcc.getAccountName());
                        historyViewModel.setDestAccountFilter(selectedAcc.getAccountId());
                    }
                    historyViewModel.reloadTransactions();
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
        Navigation.findNavController(requireView()).navigate(R.id.transactionEntryFragment);
    }
}