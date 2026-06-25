package com.example.moneyapp.view.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

            public class AddCategoryFragment extends BaseFragment {

                private EditText etName, etGroupName, etMonthlyTarget;
                private IconicsImageView ivPreviewIcon;
                private View viewPreviewColor;

                private int categoryTypeIndex = 0;
                private CategoryViewModel viewModel;

                private int selectedColorId = 0;
                private int selectedIconId = 0;

                private String categoryId = null;
                private String currentGroupId = null;
                private boolean isDefaultCategory = false;

                private final List<CategoryGroupResponse> cachedGroups = new ArrayList<>();

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
                            isDefaultCategory = "Khác".equals(getArguments().getString("categoryName"));
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
                    etGroupName = view.findViewById(R.id.et_group_name);
                    etMonthlyTarget = view.findViewById(R.id.et_monthly_target);
                    TextView tvBudgetLabel = view.findViewById(R.id.tv_budget_label);
                    ivPreviewIcon = view.findViewById(R.id.iv_preview_icon);
                    viewPreviewColor = view.findViewById(R.id.view_preview_color);

                    // =========================================================
                    // SETUP HEADER (TIÊU ĐỀ THEO LOẠI & TRẠNG THÁI MỚI/SỬA)
                    // =========================================================
                    String titlePrefix = (categoryId == null) ? "Thêm " : "Sửa ";
                    String titleSuffix = (categoryTypeIndex == 0) ? "hạng mục chi tiêu" : "hạng mục thu nhập";
                    String title = titlePrefix + titleSuffix;

                    if (categoryId != null) {
                        if (!isDefaultCategory) {
                            setupHeader(view, title,
                                    "gmd_navigate_before", v -> Navigation.findNavController(v).navigateUp(),
                                    "gmd_delete_outline", v -> showDeleteConfirmDialog());
                        } else {
                            setupHeader(view, title, true);
                        }
                    } else {
                        setupHeader(view, title, true);
                    }

                    // =========================================================
                    // SETUP LABEL KẾ HOẠCH/THU NHẬP (THÊM ĐƠN VỊ TIỀN TỆ)
                    // =========================================================
                    String defaultCurrency = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();
                    if (defaultCurrency == null) defaultCurrency = "VND";

                    String labelText = (categoryTypeIndex == 0) ? "Kế hoạch chi tiêu tháng (" + defaultCurrency + ")"
                            : "Thu nhập dự kiến trong tháng (" + defaultCurrency + ")";
                    tvBudgetLabel.setText(labelText);

                    ScrollView mainScrollView = view.findViewById(R.id.main_scroll_view);
                    view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                        Rect r = new Rect();
                        view.getWindowVisibleDisplayFrame(r);
                        int screenHeight = view.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) {
                            int[] svLocation = new int[2];
                            mainScrollView.getLocationOnScreen(svLocation);
                            int svBottom = svLocation[1] + mainScrollView.getHeight();
                            int overlap = Math.max(0, svBottom - r.bottom);
                            mainScrollView.setPadding(0, 0, 0, overlap);

                            if (etMonthlyTarget.hasFocus() || etGroupName.hasFocus()) {
                                mainScrollView.postDelayed(() -> {
                                    int targetY = mainScrollView.getChildAt(0).getHeight();
                                    mainScrollView.smoothScrollTo(0, targetY);
                                }, 100);
                            }
                        } else {
                            mainScrollView.setPadding(0, 0, 0, 0);
                        }
                    });

                    setupCurrencyFormatter();
                    setupGroupSelector(view);

                    if (categoryId != null) {
                        etName.setText(getArguments().getString("categoryName"));

                        String passedGroupName = getArguments().getString("groupName");
                        if (passedGroupName != null && !passedGroupName.isEmpty()) {
                            etGroupName.setText(passedGroupName);
                        }

                        etGroupName.setFocusable(false);
                        etGroupName.setFocusableInTouchMode(false);

                        double target = getArguments().getDouble("monthlyTarget");
                        if (target > 0) {
                            etMonthlyTarget.setText(String.format(Locale.US, "%.0f", target));
                        }

                        if (isDefaultCategory) {
                            etName.setEnabled(false);
                            etName.setTextColor(getResources().getColor(R.color.colorOnSurfaceVariant, null));
                        }
                    }

                    updatePreview();

                    view.findViewById(R.id.btn_select_color).setOnClickListener(v -> {
                        hideKeyboard();
                        PopupHelper.showColorPicker(requireContext(), colorId -> {
                            selectedColorId = colorId;
                            updatePreview();
                        });
                    });

                    view.findViewById(R.id.btn_select_icon).setOnClickListener(v -> {
                        hideKeyboard();
                        PopupHelper.showIconPicker(requireContext(), iconId -> {
                            selectedIconId = iconId;
                            updatePreview();
                        });
                    });

                    CategoryType currentType = (categoryTypeIndex == 0) ? CategoryType.EXPENSE : CategoryType.INCOME;
                    viewModel.loadCategories(currentType);

                    observeViewModel();
                }

                private void observeViewModel() {
                    viewModel.getGroupsLiveData().observe(getViewLifecycleOwner(), groups -> {
                        if (groups != null) {
                            cachedGroups.clear();
                            cachedGroups.addAll(groups);
                        }
                    });

                    viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
                        if (success) {
                            Toast.makeText(getContext(), R.string.category_save_success, Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        }
                    });

                    viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
                        if (error != null) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                private void showDeleteConfirmDialog() {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Xóa hạng mục")
                            .setMessage("Bạn có chắc chắn muốn xóa hạng mục này không?")
                            .setPositiveButton("Xóa", (dialog, which) -> deleteCategory())
                            .setNegativeButton("Hủy", null)
                            .show();
                }

                private void setupGroupSelector(View view) {
                    ScrollView mainScrollView = view.findViewById(R.id.main_scroll_view);

                    etGroupName.setFocusable(false);
                    etGroupName.setFocusableInTouchMode(false);
                    etGroupName.setClickable(true);

                    View.OnClickListener groupClickListener = v -> {
                        if (etGroupName.isFocusable()) return;

                        hideKeyboard();

                        List<String> displayList = new ArrayList<>();
                        for (CategoryGroupResponse group : cachedGroups) {
                            if (group.getGroupName() != null && !displayList.contains(group.getGroupName())) {
                                displayList.add(group.getGroupName());
                            }
                        }

                        PopupHelper.showGroupSelectorPopup(requireContext(), displayList, (groupName, isNewGroup) -> {
                            if (isNewGroup) {
                                etGroupName.setFocusable(true);
                                etGroupName.setFocusableInTouchMode(true);
                                etGroupName.setText("");
                                etGroupName.requestFocus();
                                currentGroupId = null;

                                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) imm.showSoftInput(etGroupName, InputMethodManager.SHOW_IMPLICIT);
                            } else {
                                if (mainScrollView != null) {
                                    mainScrollView.requestFocus();
                                }

                                etGroupName.setFocusable(false);
                                etGroupName.setFocusableInTouchMode(false);
                                etGroupName.setText(groupName);

                                for (CategoryGroupResponse group : cachedGroups) {
                                    if (groupName.equals(group.getGroupName())) {
                                        currentGroupId = group.getId();
                                        break;
                                    }
                                }
                            }
                        });
                    };

                    view.findViewById(R.id.btn_select_group).setOnClickListener(groupClickListener);
                    etGroupName.setOnClickListener(groupClickListener);
                    view.findViewById(R.id.iv_group_arrow).setOnClickListener(groupClickListener);
                }

                private void setupCurrencyFormatter() {
                    etMonthlyTarget.addTextChangedListener(new TextWatcher() {
                        private String current = "";
                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().equals(current)) {
                                etMonthlyTarget.removeTextChangedListener(this);
                                String cleanString = s.toString().replaceAll("[.,]", "");

                                if (!cleanString.isEmpty()) {
                                    try {
                                        double parsed = Double.parseDouble(cleanString);
                                        String formatted = CurrencyFormatter.formatVND(parsed);
                                        current = formatted;
                                        etMonthlyTarget.setText(formatted);
                                        etMonthlyTarget.setSelection(formatted.length());
                                    } catch (NumberFormatException e) { }
                                } else {
                                    current = "";
                                    etMonthlyTarget.setText("");
                                }
                                etMonthlyTarget.addTextChangedListener(this);
                            }
                        }
        });
    }

    private void updatePreview() {
        int colorValue = AppResourceManager.getColor(selectedColorId);
        ivPreviewIcon.setImageDrawable(AppResourceManager.getWhiteIcon(requireContext(), selectedIconId));
        ivPreviewIcon.setImageTintList(ColorStateList.valueOf(android.graphics.Color.WHITE));

        viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(colorValue));
        viewPreviewColor.setBackgroundResource(R.drawable.bg_circle);
    }

    @Override protected String getFabIcon() { return "gmd_check"; }
    @Override protected String getFabLabel() { return "Lưu hạng mục"; }
    @Override protected void onFabClick() { saveCategory(); }

    private void saveCategory() {
        String name = etName.getText().toString().trim();
        String groupName = etGroupName.getText().toString().trim();
        String targetStr = etMonthlyTarget.getText().toString().trim().replaceAll("[.,]", "");

        if (name.isEmpty()) {
            etName.setError(getString(R.string.category_error_empty_name));
            return;
        }
        if (groupName.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn hoặc nhập tên nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        double target = targetStr.isEmpty() ? 0.0 : Double.parseDouble(targetStr);
        CategoryType type = (categoryTypeIndex == 0) ? CategoryType.EXPENSE : CategoryType.INCOME;

        Category category = new Category(
                categoryId,
                name,
                type,
                currentGroupId,
                groupName,
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