package com.example.moneyapp.view.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.Date;

public class AddCategoryFragment extends BaseFragment {

    private EditText etName, etMonthlyTarget;
    private ImageView ivPreviewIcon;
    private View viewPreviewColor;
    private int categoryTypeIndex = 0; // 0 for Expense, 1 for Income
    private CategoryViewModel viewModel;

    private int selectedColorId = 0;
    private int selectedIconId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryTypeIndex = getArguments().getInt("type", 0);
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
        ivPreviewIcon = view.findViewById(R.id.iv_preview_icon);
        viewPreviewColor = view.findViewById(R.id.view_preview_color);

        String title = (categoryTypeIndex == 0) ? getString(R.string.category_expense_title) : getString(R.string.category_income_title);
        String label = (categoryTypeIndex == 0) ? getString(R.string.category_expense_budget_label) : getString(R.string.category_income_budget_label);

        setupHeader(view, title, true);
        tvBudgetLabel.setText(label);

        updatePreview();

        view.findViewById(R.id.btn_select_color).setOnClickListener(v -> {
            ColorSelectorBottomSheet.newInstance(colorId -> {
                selectedColorId = colorId;
                updatePreview();
            }).show(getChildFragmentManager(), "ColorSelector");
        });

        view.findViewById(R.id.btn_select_icon).setOnClickListener(v -> {
            IconSelectorBottomSheet.newInstance(iconId -> {
                selectedIconId = iconId;
                updatePreview();
            }).show(getChildFragmentManager(), "IconSelector");
        });

        // Observe kết quả lưu
        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), R.string.category_save_success, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private void updatePreview() {
        int colorValue = AppResourceManager.getColor(selectedColorId);
        ivPreviewIcon.setImageResource(AppResourceManager.getIconRes(selectedIconId));
        ivPreviewIcon.setImageTintList(ColorStateList.valueOf(colorValue));
        viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(colorValue));
        viewPreviewColor.setBackgroundResource(R.drawable.bg_circle);
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
        String name = etName.getText().toString().trim();
        String targetStr = etMonthlyTarget.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.category_error_empty_name));
            return;
        }

        double target = targetStr.isEmpty() ? 0.0 : Double.parseDouble(targetStr);
        CategoryType type = (categoryTypeIndex == 0) ? CategoryType.EXPENSE : CategoryType.INCOME;

        Category newCategory = new Category(
                null, 
                name,
                type,
                null,
                null,
                target,
                selectedColorId,
                selectedIconId,
                0,
                new Date(),
                new Date()
        );

        viewModel.addCategory(newCategory);
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}
