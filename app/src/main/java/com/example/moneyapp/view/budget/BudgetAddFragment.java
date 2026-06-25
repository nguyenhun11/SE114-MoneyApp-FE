package com.example.moneyapp.view.budget;

import android.app.DatePickerDialog;
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
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.BudgetViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private Date selectedStartDate;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Các hằng số quy định phạm vi
    private static final int SCOPE_TOTAL = 0;
    private static final int SCOPE_GROUP = 1;
    private static final int SCOPE_CATEGORY = 2;
    private int currentScope = SCOPE_CATEGORY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, "Thêm ngân sách", true);

        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        initViews(view);
        observeViewModels();

        // Load trước danh sách Nhóm và Hạng mục Chi tiêu
        categoryViewModel.loadCategories(CategoryType.EXPENSE);
        setDefaultStartDate(1); // Mặc định là Tháng
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

        // Xử lý sự kiện đổi Chu kỳ
        rgPeriod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbWeekly) setDefaultStartDate(0);
            else if (checkedId == R.id.rbMonthly) setDefaultStartDate(1);
            else if (checkedId == R.id.rbYearly) setDefaultStartDate(2);
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

    private void setDefaultStartDate(int period) {
        Calendar cal = Calendar.getInstance();
        if (period == 0) { // Weekly
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        } else if (period == 1) { // Monthly
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else if (period == 2) { // Yearly
            cal.set(Calendar.DAY_OF_YEAR, 1);
        }
        selectedStartDate = cal.getTime();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedStartDate);
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar newCal = Calendar.getInstance();
            newCal.set(year, month, dayOfMonth);
            selectedStartDate = newCal.getTime();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
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

        // Chuyển List<Group> thành List<String> để dùng Popup cũ của bạn
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
                Toast.makeText(getContext(), "Lưu ngân sách thành công!", Toast.LENGTH_SHORT).show();
                budgetViewModel.resetOperationSuccess();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        budgetViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void saveBudget() {
        String amountStr = etAmount.getText().toString().replaceAll("[.,]", "");
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int period = 1; // Monthly
        if (rbWeekly.isChecked()) period = 0;
        else if (rbYearly.isChecked()) period = 2;

        BudgetRequest request = new BudgetRequest(
                selectedCategory != null ? selectedCategory.getCategoryId() : null,
                selectedGroup != null ? selectedGroup.getId() : null,
                amount, period
        );

        // PHÂN LOẠI REQUEST GỬI LÊN DỰA VÀO SCOPE
        if (currentScope == SCOPE_TOTAL) {
            request.setCategoryGroupId(null);
            request.setCategoryId(null);
        }
        else if (currentScope == SCOPE_GROUP) {
            if (selectedGroup == null) {
                Toast.makeText(getContext(), "Vui lòng chọn nhóm hạng mục", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setCategoryGroupId(selectedGroup.getId());
            request.setCategoryId(null);
        }
        else if (currentScope == SCOPE_CATEGORY) {
            if (selectedCategory == null) {
                Toast.makeText(getContext(), "Vui lòng chọn hạng mục", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setCategoryGroupId(selectedCategory.getGroupId()); // Truyền thêm Group ID phòng hờ
            request.setCategoryId(selectedCategory.getCategoryId());
        }

        budgetViewModel.createBudget(request);
    }

    @Override protected String getFabIcon() { return "gmd_check"; }
    @Override protected void onFabClick() { saveBudget(); }
    @Override protected boolean shouldShowBottomNavigation() { return false; }
}