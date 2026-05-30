package com.example.moneyapp.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

public class AddCategoryFragment extends BaseFragment {

    private EditText etName, etMonthlyTarget;
    private int categoryType = 2; // Default is Expense
    private CategoryViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryType = getArguments().getInt("type", 2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        etName = view.findViewById(R.id.et_category_name);
        etMonthlyTarget = view.findViewById(R.id.et_monthly_target);
        TextView tvBudgetLabel = view.findViewById(R.id.tv_budget_label);

        String title = (categoryType == 2) ? getString(R.string.category_expense_title) : getString(R.string.category_income_title);
        String label = (categoryType == 2) ? getString(R.string.category_expense_budget_label) : getString(R.string.category_income_budget_label);

        setupHeader(view, title, true);
        tvBudgetLabel.setText(label);

        // Giả lập chọn màu/icon
        view.findViewById(R.id.btn_select_color).setOnClickListener(v -> Toast.makeText(getContext(), R.string.menu_item_default, Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.btn_select_icon).setOnClickListener(v -> Toast.makeText(getContext(), R.string.menu_item_default, Toast.LENGTH_SHORT).show());

        // Observe kết quả lưu
        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), R.string.category_save_success, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    @Override
    protected int getFabIcon() {
        return R.drawable.ic_check_white;
    }

    @Override
    protected void onFabClick() {
        saveCategory();
    }

    private void saveCategory() {
//        String name = etName.getText().toString().trim();
//        String targetStr = etMonthlyTarget.getText().toString().trim();
//
//        if (name.isEmpty()) {
//            etName.setError(getString(R.string.category_error_empty_name));
//            return;
//        }
//
//        double target = targetStr.isEmpty() ? 0.0 : Double.parseDouble(targetStr);
//
//        Category newCategory = new Category(
//                null, // userId
//                name,
//                target,
//                "ic_transaction",
//                "#7F3DFF", // Mặc định màu tím
//                (categoryType == 2) ? getString(R.string.category_group_other) : getString(R.string.category_group_income),
//                categoryType,
//                true, // isFrequent
//                true  // canDelete
//        );
//
//        viewModel.addCategory(newCategory);
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}
