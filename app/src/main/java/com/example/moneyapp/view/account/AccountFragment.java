package com.example.moneyapp.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.view.category.CategoryAdapter;
import com.example.moneyapp.view.category.CategoryGroupAdapter;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountFragment extends BaseFragment {

    public enum TabMode {ACCOUNTS, EXPENSE_CATEGORIES, INCOME_CATEGORIES}
    private TabMode currentMode;

    private RecyclerView rvMainList;
    private View tabLayoutHeader;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;

    private AccountAdapter accountAdapter;
    private CategoryGroupAdapter categoryGroupAdapter;

    private String lastDisplayBalance = "0 đ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo ViewModels
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        // 2. Ánh xạ View (Dùng ID mới của kiến trúc Tab)
        rvMainList = view.findViewById(R.id.rv_main_list);
        rvMainList.setLayoutManager(new LinearLayoutManager(getContext()));
        tabLayoutHeader = view.findViewById(R.id.tab_layout_header);

        // 3. Khởi tạo Adapter & Bắt sự kiện Click (Merge từ đoạn code 1)
        // (Nếu bạn có hàm initAdapters() riêng, hãy đưa phần khởi tạo AccountAdapter này vào trong đó)
//        AccountAdapter accountAdapter = new AccountAdapter(new ArrayList<>(), PreferenceManager.getInstance(requireContext()).getDefaultCurrency(), account -> {
//            Bundle args = new Bundle();
//            args.putString("accountId", account.getAccountId());
//
//            // Đã merge nav_graph ở bước trước, gọi action trực tiếp để tự ăn animation!
//            Navigation.findNavController(view).navigate(R.id.action_accountFragment_to_accountDetailFragment, args);
//        });

        // Gọi hàm initAdapters() cho các Adapter còn lại (CategoryAdapter, v.v.) nếu có
        initAdapters();

        // Mặc định gán adapter tài khoản lên (sẽ được updateUIByMode ghi đè lại sau nếu cần)
        rvMainList.setAdapter(accountAdapter);

        // 4. Logic quản lý Tab (Từ đoạn code 2)
        int initialTab = PreferenceManager.getInstance(requireContext()).getLastAccountTab();

        if (initialTab == 0) currentMode = TabMode.ACCOUNTS;
        else if (initialTab == 1) currentMode = TabMode.EXPENSE_CATEGORIES;
        else currentMode = TabMode.INCOME_CATEGORIES;

        String[] tabs = {"Tài khoản", "Chi tiêu", "Thu nhập"};

        setupHeaderTabs(view, tabs, initialTab, index -> {
            PreferenceManager.getInstance(requireContext()).setLastAccountTab(index);

            if (index == 1)
                PreferenceManager.getInstance(requireContext()).setLastTabType(0); // Chi tiêu
            if (index == 2)
                PreferenceManager.getInstance(requireContext()).setLastTabType(1); // Thu nhập

            if (index == 0) currentMode = TabMode.ACCOUNTS;
            else if (index == 1) currentMode = TabMode.EXPENSE_CATEGORIES;
            else currentMode = TabMode.INCOME_CATEGORIES;

            updateUIByMode();
        });

        // 5. Lắng nghe dữ liệu và Cập nhật UI
        observeViewModels();
        updateUIByMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        accountViewModel.loadTotalBalance();
        accountViewModel.loadAccounts();

        if (currentMode != TabMode.ACCOUNTS) {
            categoryViewModel.loadCategories(categoryViewModel.getCurrentType());
        }
    }

    private void initAdapters() {
        accountAdapter = new AccountAdapter(new ArrayList<>(), PreferenceManager.getInstance(requireContext()).getDefaultCurrency(), account -> {
            Bundle args = new Bundle();
            args.putString("accountId", account.getAccountId());
            Navigation.findNavController(requireView()).navigate(R.id.accountDetailFragment, args);
        });

        CategoryAdapter.OnCategoryClickListener catClickListener = category -> {
            if ("Khác".equals(category.getCategoryName())) {
                DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Đây là hạng mục mặc định và không thể chỉnh sửa");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getCategoryId());
            bundle.putString("categoryName", category.getCategoryName());
            bundle.putInt("colorId", category.getColor());
            bundle.putInt("iconId", category.getIcon());
            bundle.putString("groupId", category.getGroupId());
            bundle.putString("groupName", category.getGroupName());
            bundle.putInt("type", category.getType() == CategoryType.EXPENSE ? 0 : 1);

            Navigation.findNavController(requireView()).navigate(R.id.addCategoryFragment, bundle);
        };

        CategoryAdapter.OnCategoryLongClickListener catLongClickListener = this::showCategoryContextMenu;

        categoryGroupAdapter = new CategoryGroupAdapter(catClickListener);
        categoryGroupAdapter.setOnCategoryLongClickListener(catLongClickListener);
    }

    private void updateUIByMode() {
        View layoutBalance = requireView().findViewById(R.id.layout_balance);
        View layoutHeaderTitle = requireView().findViewById(R.id.layout_header);

        if (currentMode == TabMode.ACCOUNTS) {
            layoutBalance.setVisibility(View.VISIBLE);
            layoutHeaderTitle.setVisibility(View.GONE);

            rvMainList.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMainList.setAdapter(accountAdapter);

            setupBalanceSelector(layoutBalance, getString(R.string.total_balance), lastDisplayBalance, false, null, null, null, null);
        } else {
            layoutBalance.setVisibility(View.GONE);
            layoutHeaderTitle.setVisibility(View.VISIBLE);

            rvMainList.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMainList.setAdapter(categoryGroupAdapter);

            CategoryType targetType = (currentMode == TabMode.EXPENSE_CATEGORIES) ? CategoryType.EXPENSE : CategoryType.INCOME;
            categoryViewModel.setCurrentType(targetType);
            categoryViewModel.loadCategories(targetType);

            String modeTitle = (currentMode == TabMode.EXPENSE_CATEGORIES) ? "Hạng mục chi tiêu" : "Hạng mục thu nhập";
            setupHeader(layoutHeaderTitle, modeTitle, null, null, "gmd_edit", v -> navigateToReorderScreen());
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getUiHandler() != null) {
                mainActivity.getUiHandler().updateFAB(getFabIcon(), getFabLabel(), 0,  v -> onFabClick());
            }
        }
    }

    private void navigateToReorderScreen() {
        Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_reorderCategoryFragment);
    }

    private void showCategoryContextMenu(Category category, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        if (!"Khác".equals(category.getCategoryName())) {
            popupMenu.getMenu().add(0, 1, 0, "Xóa danh mục");
        }
        popupMenu.getMenu().add(0, 2, 0, "Thay đổi vị trí");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                DialogHelper.showConfirmDialog(requireContext(), "Xác nhận xóa", "Bạn có muốn xóa danh mục này?", 
                    () -> categoryViewModel.deleteCategory(category.getCategoryId(), "soft_delete", null), null);
                return true;
            } else if (item.getItemId() == 2) {
                navigateToReorderScreen();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    // 💥 Thêm hàm tiện ích dùng chung
    private void toggleUIState(boolean isLoading, boolean isEmpty, String emptyMessage, String emptyIconName) {
        com.facebook.shimmer.ShimmerFrameLayout shimmer = requireView().findViewById(R.id.shimmer_account_list);
        View layoutEmptyState = requireView().findViewById(R.id.layout_empty_state);
        RecyclerView rvMainList = requireView().findViewById(R.id.rv_main_list);

        if (isLoading) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
            rvMainList.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);

            if (isEmpty) {
                rvMainList.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);

                // Cập nhật text và icon tùy tab
                TextView tvEmptyMsg = layoutEmptyState.findViewById(R.id.tv_empty_message);
                com.mikepenz.iconics.view.IconicsImageView ivEmptyIcon = layoutEmptyState.findViewById(R.id.iv_empty_icon);
                tvEmptyMsg.setText(emptyMessage);
                ivEmptyIcon.setIcon(new com.mikepenz.iconics.IconicsDrawable(requireContext(), emptyIconName));
            } else {
                rvMainList.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
        }
    }

    // 💥 Cập nhật hàm observe
    private void observeViewModels() {
        // --- 1. Lắng nghe tổng số dư ---
        accountViewModel.getTotalBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                lastDisplayBalance = String.format(Locale.getDefault(), "%,.0f đ", balance).replace(",", ".");
            }
            if (currentMode == TabMode.ACCOUNTS) {
                View layoutBalance = requireView().findViewById(R.id.layout_balance);
                setupBalanceSelector(layoutBalance, getString(R.string.total_balance), lastDisplayBalance, false, null, null, null, null);
            }
        });

        // --- 2. Lắng nghe Loading của Tab Tài Khoản ---
        accountViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (currentMode == TabMode.ACCOUNTS) {
                if (isLoading) {
                    toggleUIState(true, false, "", "");
                } else {
                    // Khi vừa hết Loading, kiểm tra ngay dữ liệu hiện tại để chốt hạ UI
                    List<Account> currentAccounts = accountViewModel.getAccountsLiveData().getValue();
                    boolean isEmpty = (currentAccounts == null || currentAccounts.isEmpty());
                    toggleUIState(false, isEmpty, "Chưa có tài khoản nào.\nHãy tạo ví hoặc ngân hàng!", "gmd_account_balance_wallet");
                }
            }
        });

        // --- 3. Lắng nghe Dữ liệu Tab Tài Khoản ---
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            // Luôn luôn cập nhật dữ liệu vào adapter trước để chuẩn bị hiển thị
            if (accounts != null) {
                accountAdapter.updateList(accounts);
            }

            if (currentMode == TabMode.ACCOUNTS) {
                Boolean isLoading = accountViewModel.getIsLoading().getValue();
                // Chỉ đổi trạng thái UI ẩn hiện nếu tiến trình loading đã hoàn tất hẳn
                if (isLoading == null || !isLoading) {
                    boolean isEmpty = (accounts == null || accounts.isEmpty());
                    toggleUIState(false, isEmpty, "Chưa có tài khoản nào.\nHãy tạo ví hoặc ngân hàng!", "gmd_account_balance_wallet");
                }
            }
        });

        // --- 4. Lắng nghe Loading của Tab Hạng Mục ---
        categoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (currentMode != TabMode.ACCOUNTS) {
                if (isLoading) {
                    toggleUIState(true, false, "", "");
                } else {
                    // Khi vừa hết Loading, kiểm tra dữ liệu của danh mục để chốt hạ UI
                    List<Category> currentCategories = categoryViewModel.getCategoriesLiveData().getValue();
                    boolean isEmpty = (currentCategories == null || currentCategories.isEmpty());
                    String msg = (currentMode == TabMode.EXPENSE_CATEGORIES) ?
                            "Chưa có hạng mục chi tiêu.\nHãy tạo mới để quản lý chi tiêu!" :
                            "Chưa có hạng mục thu nhập.\nHãy tạo mới để quản lý thu nhập!";
                    toggleUIState(false, isEmpty, msg, "gmd_category");
                }
            }
        });

        // --- 5. Lắng nghe Dữ liệu Tab Hạng Mục ---
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            // Luôn luôn nạp dữ liệu vào adapter trước
            if (categories != null) {
                categoryGroupAdapter.setData(categories);
            }

            if (currentMode != TabMode.ACCOUNTS) {
                Boolean isLoading = categoryViewModel.getIsLoading().getValue();
                if (isLoading == null || !isLoading) {
                    boolean isEmpty = (categories == null || categories.isEmpty());
                    String msg = (currentMode == TabMode.EXPENSE_CATEGORIES) ?
                            "Chưa có hạng mục chi tiêu.\nHãy tạo mới để quản lý chi tiêu!" :
                            "Chưa có hạng mục thu nhập.\nHãy tạo mới để quản lý thu nhập!";
                    toggleUIState(false, isEmpty, msg, "gmd_category");
                }
            }
        });

        // --- 6. Lắng nghe thông báo lỗi (Giữ nguyên) ---
        accountViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        });

        categoryViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        });
    }

    @Override
    protected String getFabLabel() {
        if (currentMode == TabMode.ACCOUNTS) return "Thêm tài khoản";
        return (currentMode == TabMode.EXPENSE_CATEGORIES) ? "Thêm hạng mục chi tiêu" : "Thêm hạng mục thu nhập";
    }

    @Override
    protected void onFabClick() {
        if (currentMode == TabMode.ACCOUNTS) {
            Navigation.findNavController(requireView()).navigate(R.id.action_accountFragment_to_accountDetailFragment);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("type", currentMode == TabMode.EXPENSE_CATEGORIES ? 0 : 1);
            Navigation.findNavController(requireView()).navigate(R.id.addCategoryFragment, bundle);
        }
    }
}
