package com.example.moneyapp.view.category;

import android.content.res.ColorStateList;
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
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.PopupHelper; // <-- Nhớ import PopupHelper
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Date;

public class AddCategoryFragment extends BaseFragment {

    private EditText etName, etMonthlyTarget;
    private IconicsImageView ivPreviewIcon;
    private View viewPreviewColor;
    private TextView btnDelete;
    private int categoryTypeIndex = 0; // 0 for Expense, 1 for Income
    private CategoryViewModel viewModel;

    private int selectedColorId = 0;
    private int selectedIconId = 0;

    // Edit mode properties
    private String categoryId = null;
    private String currentGroupId = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryTypeIndex = getArguments().getInt("type", 0);
            categoryId = getArguments().getString("categoryId");
            if (categoryId != null) {
                selectedColorId = getArguments().getInt("colorId");
                selectedIconId = getArguments().getInt("iconId");
                currentGroupId = getArguments().getString("groupId");
            }
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
        btnDelete = view.findViewById(R.id.btn_delete_category);

        String title = (categoryTypeIndex == 0) ? getString(R.string.category_expense_title) : getString(R.string.category_income_title);
        if (categoryId != null) {
            title = "Chỉnh sửa hạng mục";
        }

        String label = (categoryTypeIndex == 0) ? getString(R.string.category_expense_budget_label) : getString(R.string.category_income_budget_label);

        setupHeader(view, title, true);
        tvBudgetLabel.setText(label);

        // Pre-fill data if in edit mode
        if (categoryId != null) {
            etName.setText(getArguments().getString("categoryName"));
            double target = getArguments().getDouble("monthlyTarget");
            etMonthlyTarget.setText(String.valueOf((int) target));

            // Hạng mục "Khác" không thể xóa
            if (!"Khác".equals(getArguments().getString("categoryName"))) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> deleteCategory());
            }
        }

        updatePreview();

        view.findViewById(R.id.btn_select_color).setOnClickListener(v -> {
            PopupHelper.showColorPicker(requireContext(), colorId -> {
                selectedColorId = colorId;
                updatePreview();
            });
        });

        view.findViewById(R.id.btn_select_icon).setOnClickListener(v -> {
            PopupHelper.showIconPicker(requireContext(), iconId -> {
                selectedIconId = iconId;
                updatePreview();
            });
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
        ivPreviewIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(selectedIconId)));
        ivPreviewIcon.setImageTintList(ColorStateList.valueOf(colorValue));
        viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(colorValue));
        viewPreviewColor.setBackgroundResource(R.drawable.bg_circle);
    }

    @Override
    protected String getFabIcon() {
        return "gmd_check";
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

        Category category = new Category(
                categoryId,
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

        if (categoryId == null) {
            viewModel.addCategory(category);
        } else {
            viewModel.updateCategory(category);
        }
    }

    private void deleteCategory() {
        if (categoryId != null) {
            viewModel.deleteCategory(categoryId, "soft_delete", null);
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}