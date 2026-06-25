package com.example.moneyapp.view.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.BudgetRequest;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.BudgetViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetAddFragment extends BaseFragment {

    private EditText etAmount;
    private TextView tvSelectedCategory, tvSelectedGroup;
    private IconicsImageView ivCategoryIcon;
    private RadioGroup rgPeriod, rgScope;
    private RadioButton rbWeekly, rbMonthly, rbYearly;
    private View layoutGroupSelector, layoutCategorySelector;

    private BudgetViewModel budgetViewModel;
    private CategoryViewModel categoryViewModel;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<CategoryGroupResponse> groupList = new ArrayList<>();

    private Category selectedCategory = null;
    private CategoryGroupResponse selectedGroup = null;

    // Các biến phân luồng Sửa/Thêm
    private Integer budgetId = null;
    private static final int SCOPE_TOTAL = 0;
    private static final int SCOPE_GROUP = 1;
    private static final int SCOPE_CATEGORY = 2;
    private int currentScope = SCOPE_CATEGORY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bắt dữ liệu nếu được chuyển sang từ danh sách (Chế độ SỬA)
        if (getArguments() != null && getArguments().containsKey("budgetId")) {
            budgetId = getArguments().getInt("budgetId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        initViews(view);
        observeViewModels();

        categoryViewModel.loadCategories(CategoryType.EXPENSE);

        // PHÂN NHÁNH: THÊM MỚI hoặc CHỈNH SỬA
        if (budgetId != null) {
            setupHeader(view, "Sửa ngân sách",
                    "gmd_navigate_before", v -> Navigation.findNavController(v).navigateUp(),
                    "gmd_delete_outline", v -> showDeleteConfirmDialog()); // Icon xóa

            fillExistingData(getArguments());
        } else {
            setupHeader(view, "Thêm ngân sách", true);
            rbMonthly.setChecked(true); // Mặc định là Tháng
        }
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
        tvSelectedGroup = view.findViewById(R.id.tvSelectedGroup);
        ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);

        rgPeriod = view.findViewById(R.id.rgPeriod);
        rbWeekly = view.findViewById(R.id.rbWeekly);
        rbMonthly = view.findViewById(R.id.rbMonthly);
        rbYearly = view.findViewById(R.id.rbYearly);

        rgScope = view.findViewById(R.id.rgScope);
        layoutGroupSelector = view.findViewById(R.id.layoutGroupSelector);
        layoutCategorySelector = view.findViewById(R.id.layoutCategorySelector);

        view.findViewById(R.id.btnSelectCategory).setOnClickListener(v -> showCategoryPopup());
        view.findViewById(R.id.btnSelectGroup).setOnClickListener(v -> showGroupPopup());

        // Xử lý sự kiện đổi Phạm vi
        rgScope.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbScopeTotal) {
                currentScope = SCOPE_TOTAL;
                layoutGroupSelector.setVisibility(View.GONE);
                layoutCategorySelector.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbScopeGroup) {
                currentScope = SCOPE_GROUP;
                layoutGroupSelector.setVisibility(View.VISIBLE);
                layoutCategorySelector.setVisibility(View.GONE);
            } else {
                currentScope = SCOPE_CATEGORY;
                layoutGroupSelector.setVisibility(View.GONE);
                layoutCategorySelector.setVisibility(View.VISIBLE);
            }
        });

        // Formatting cho EditText số tiền
        etAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    etAmount.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[.,]", "");
                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = String.format(Locale.getDefault(), "%,.0f", parsed).replace(",", ".");
                            current = formatted;
                            etAmount.setText(formatted);
                            etAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) { }
                    } else {
                        current = "";
                        etAmount.setText("");
                    }
                    etAmount.addTextChangedListener(this);
                }
            }
        });
    }

    // Đổ dữ liệu vào form khi ở chế độ Chỉnh Sửa
    private void fillExistingData(Bundle args) {
        double amount = args.getDouble("amount", 0);
        etAmount.setText(String.format(Locale.US, "%.0f", amount));

        int period = args.getInt("period", 1);
        if (period == 0) rbWeekly.setChecked(true);
        else if (period == 1) rbMonthly.setChecked(true);
        else rbYearly.setChecked(true);

        String catId = args.getString("categoryId");
        String groupId = args.getString("categoryGroupId");
        String name = args.getString("categoryName");

        // Khóa không cho đổi Hạng mục/Nhóm khi đang sửa (Tránh nhầm lẫn logic)
        for (int i = 0; i < rgScope.getChildCount(); i++) {
            rgScope.getChildAt(i).setEnabled(false);
        }
        getView().findViewById(R.id.btnSelectCategory).setEnabled(false);
        getView().findViewById(R.id.btnSelectGroup).setEnabled(false);

        if (catId != null) {
            rgScope.check(R.id.rbScopeCategory);
            currentScope = SCOPE_CATEGORY;
            tvSelectedCategory.setText(name != null ? name : "Đang tải...");
        } else if (groupId != null) {
            rgScope.check(R.id.rbScopeGroup);
            currentScope = SCOPE_GROUP;
            tvSelectedGroup.setText(name != null ? name : "Đang tải...");
            layoutGroupSelector.setVisibility(View.VISIBLE);
            layoutCategorySelector.setVisibility(View.GONE);
        } else {
            rgScope.check(R.id.rbScopeTotal);
            currentScope = SCOPE_TOTAL;
            layoutGroupSelector.setVisibility(View.GONE);
            layoutCategorySelector.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa ngân sách")
                .setMessage("Bạn có chắc chắn muốn xóa kế hoạch ngân sách này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (budgetId != null) {
                        budgetViewModel.deleteBudget(budgetId);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showCategoryPopup() {
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
            return;
        }
        PopupHelper.showCategoryFilterPopup(requireContext(), categoryList, false, category -> {
            selectedCategory = category;
            tvSelectedCategory.setText(category.getCategoryName());
            ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(category.getIcon())));
            ivCategoryIcon.setColorFilter(AppResourceManager.getColor(category.getColor()));
        });
    }

    private void showGroupPopup() {
        if (groupList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> displayList = new ArrayList<>();
        for (CategoryGroupResponse group : groupList) {
            displayList.add(group.getGroupName());
        }

        PopupHelper.showGroupSelectorPopup(requireContext(), displayList, (groupName, isNewGroup) -> {
            if (!isNewGroup) {
                tvSelectedGroup.setText(groupName);
                for (CategoryGroupResponse group : groupList) {
                    if (groupName.equals(group.getGroupName())) {
                        selectedGroup = group;
                        break;
                    }
                }
            }
        });
    }

    private void observeViewModels() {
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
            }
        });

        categoryViewModel.getGroupsLiveData().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                groupList.clear();
                groupList.addAll(groups);
            }
        });

        budgetViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Lưu ngân sách thành công!", () -> {
                    budgetViewModel.resetOperationSuccess();
                    Navigation.findNavController(requireView()).navigateUp();
                });
            }
        });

        budgetViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        });
    }

    private void saveBudget() {
        String amountStr = etAmount.getText().toString().replaceAll("[.,]", "");
        if (amountStr.isEmpty() || selectedCategory == null) {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Vui lòng nhập đủ thông tin");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int period = 1; // Monthly
        if (rbWeekly.isChecked()) period = 0;
        else if (rbYearly.isChecked()) period = 2;

        BudgetRequest request = new BudgetRequest(null, null, amount, period);

        if (currentScope == SCOPE_TOTAL) {
            request.setCategoryGroupId(null);
            request.setCategoryId(null);
        }
        else if (currentScope == SCOPE_GROUP) {
            if (budgetId == null && selectedGroup == null) {
                Toast.makeText(getContext(), "Vui lòng chọn nhóm hạng mục", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setCategoryGroupId(budgetId != null ? getArguments().getString("categoryGroupId") : selectedGroup.getId());
            request.setCategoryId(null);
        }
        else if (currentScope == SCOPE_CATEGORY) {
            if (budgetId == null && selectedCategory == null) {
                Toast.makeText(getContext(), "Vui lòng chọn hạng mục", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setCategoryGroupId(budgetId != null ? getArguments().getString("categoryGroupId") : selectedCategory.getGroupId());
            request.setCategoryId(budgetId != null ? getArguments().getString("categoryId") : selectedCategory.getCategoryId());
        }

        if (budgetId != null) {
            budgetViewModel.updateBudget(budgetId, request);
        } else {
            budgetViewModel.createBudget(request);
        }
    }

    @Override protected String getFabIcon() { return "gmd_check"; }
    @Override protected void onFabClick() { saveBudget(); }
    @Override protected boolean shouldShowBottomNavigation() { return false; }
}