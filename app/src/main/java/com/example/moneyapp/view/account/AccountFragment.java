package com.example.moneyapp.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.view.category.CategoryAdapter;
import com.example.moneyapp.view.category.CategoryGroupAdapter;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountFragment extends BaseFragment {

    public enum TabMode { ACCOUNTS, EXPENSE_CATEGORIES, INCOME_CATEGORIES }
    private TabMode currentMode = TabMode.ACCOUNTS;
    private boolean isEditMode = false;

    private RecyclerView rvMainList;
    private View tabLayoutHeader;
    private ItemTouchHelper itemTouchHelper;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;

    private AccountAdapter accountAdapter;
    private CategoryGroupAdapter categoryGroupAdapter;
    private CategoryAdapter categoryFlatAdapter;

    private String lastDisplayBalance = "0 đ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        rvMainList = view.findViewById(R.id.rv_main_list);
        tabLayoutHeader = view.findViewById(R.id.tab_layout_header);

        initAdapters();

        String[] tabs = {"Tài khoản", "Chi tiêu", "Thu nhập"};
        setupHeaderTabs(view, tabs, 0, index -> {
            if (isEditMode) return;

            if (index == 0) currentMode = TabMode.ACCOUNTS;
            else if (index == 1) currentMode = TabMode.EXPENSE_CATEGORIES;
            else currentMode = TabMode.INCOME_CATEGORIES;

            updateUIByMode();
        });

        observeViewModels(view);
        updateUIByMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEditMode) {
            accountViewModel.loadTotalBalance();
            accountViewModel.loadAccounts();

            if (currentMode != TabMode.ACCOUNTS) {
                categoryViewModel.loadCategories(categoryViewModel.getCurrentType());
            }
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
                Snackbar.make(requireView(), "Đây là hạng mục mặc định và không thể chỉnh sửa", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getCategoryId());
            bundle.putString("categoryName", category.getCategoryName());
            bundle.putDouble("monthlyTarget", category.getMonthlyTarget() != null ? category.getMonthlyTarget() : 0.0);
            bundle.putInt("colorId", category.getColor());
            bundle.putInt("iconId", category.getIcon());
            bundle.putString("groupId", category.getGroupId());
            bundle.putInt("type", category.getType() == CategoryType.EXPENSE ? 0 : 1);

            Navigation.findNavController(requireView()).navigate(R.id.addCategoryFragment, bundle);
        };

        CategoryAdapter.OnCategoryLongClickListener catLongClickListener = (category, anchorView) -> {
            if (!categoryFlatAdapter.isEditMode()) {
                showCategoryContextMenu(category, anchorView);
            }
        };

        categoryGroupAdapter = new CategoryGroupAdapter(catClickListener);
        categoryGroupAdapter.setOnCategoryLongClickListener(catLongClickListener);

        categoryFlatAdapter = new CategoryAdapter(new ArrayList<>(), catClickListener);
        categoryFlatAdapter.setOnCategoryLongClickListener(catLongClickListener);
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
            setupHeader(layoutHeaderTitle, modeTitle, null, null, "gmd_edit", v -> enterEditMode());
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getUiHandler() != null) {
                mainActivity.getUiHandler().updateFAB(getFabIcon(), getFabLabel(), v -> onFabClick());
            }
        }
    }

    private void enterEditMode() {
        isEditMode = true;
        setTabsEnabled(false); // Khóa thanh Tab lại

        List<Category> allCategories = categoryGroupAdapter.getAllCategoriesFlattened();
        categoryFlatAdapter.updateData(allCategories);
        categoryFlatAdapter.setEditMode(true);

        rvMainList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvMainList.setAdapter(categoryFlatAdapter);

        setupDragAndDrop();

        View cardMainContent = requireView().findViewById(R.id.card_main_content);
        if (cardMainContent != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardMainContent.getLayoutParams();
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_bottom_margin);
            cardMainContent.setLayoutParams(params);
        }

        View layoutHeaderTitle = requireView().findViewById(R.id.layout_header);
        setupHeader(layoutHeaderTitle, "Sắp xếp", "gmd_navigate_before", v -> exitEditMode(false), null, null);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getUiHandler() != null) {
                mainActivity.getUiHandler().setBottomNavigationVisibility(false);
                mainActivity.getUiHandler().updateFAB("gmd_check", "Hoàn tất", v -> exitEditMode(true));
            }
        }
    }

    private void exitEditMode(boolean saveChanges) {
        isEditMode = false;
        categoryFlatAdapter.setEditMode(false);
        setTabsEnabled(true);

        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
            itemTouchHelper = null;
        }

        // --- ĐÃ THÊM: Trả Card về lại vị trí cũ (32dp) ---
        View cardMainContent = requireView().findViewById(R.id.card_main_content);
        if (cardMainContent != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardMainContent.getLayoutParams();
            params.bottomMargin = (int) (24 * getResources().getDisplayMetrics().density);
            cardMainContent.setLayoutParams(params);
        }
        // ---------------------------------------------------------

        if (saveChanges) {
            List<Category> categories = categoryFlatAdapter.getCategories();
            for (int i = 0; i < categories.size(); i++) {
                categoryViewModel.reorderCategory(categories.get(i), i);
            }
            categoryGroupAdapter.setData(categories);
        } else {
            categoryFlatAdapter.restoreBackup();
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getUiHandler() != null) {
                mainActivity.getUiHandler().setBottomNavigationVisibility(true);
            }
        }
        updateUIByMode();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                categoryFlatAdapter.onItemMove(fromPos, toPos);
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        };
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvMainList);
    }

    private void showCategoryContextMenu(Category category, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        if (!"Khác".equals(category.getCategoryName())) {
            popupMenu.getMenu().add(0, 1, 0, "Xóa danh mục");
        }
        popupMenu.getMenu().add(0, 2, 0, "Thay đổi vị trí");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                categoryViewModel.deleteCategory(category.getCategoryId(), "soft_delete", null);
                return true;
            } else if (item.getItemId() == 2) {
                enterEditMode();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void observeViewModels(View view) {
        accountViewModel.getTotalBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                lastDisplayBalance = String.format(Locale.getDefault(), "%,.0f đ", balance).replace(",", ".");
            }
            if (currentMode == TabMode.ACCOUNTS) {
                View layoutBalance = requireView().findViewById(R.id.layout_balance);
                setupBalanceSelector(layoutBalance, getString(R.string.total_balance), lastDisplayBalance, false, null, null, null, null);
            }
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null && currentMode == TabMode.ACCOUNTS) {
                accountAdapter.updateList(accounts);
            }
        });

        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && currentMode != TabMode.ACCOUNTS && !isEditMode) {
                categoryGroupAdapter.setData(categories);
            }
        });

        accountViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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

    @Override
    protected boolean shouldShowBottomNavigation() {
        return true;
    }
}