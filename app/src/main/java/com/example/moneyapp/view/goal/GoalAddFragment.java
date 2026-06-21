package com.example.moneyapp.view.goal;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.GoalViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GoalAddFragment extends BaseFragment {

    private EditText etName, etTargetAmount;
    private TextView tvDeadline, btnDelete;
    private IconicsImageView ivPreviewIcon;
    private View viewPreviewColor;
    
    private GoalViewModel viewModel;
    private int selectedColorId = 0;
    private int selectedIconId = 16; // Mặc định là gmd-star
    private String selectedDeadline = "";
    private Goal existingGoal = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        if (getArguments() != null) {
            existingGoal = (Goal) getArguments().getSerializable("goal");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_goal_name);
        etTargetAmount = view.findViewById(R.id.et_target_amount);
        tvDeadline = view.findViewById(R.id.tv_deadline);
        ivPreviewIcon = view.findViewById(R.id.iv_preview_icon);
        viewPreviewColor = view.findViewById(R.id.view_preview_color);
        btnDelete = view.findViewById(R.id.btn_delete_goal);

        setupHeader(view, existingGoal == null ? R.string.add_goal_title : R.string.edit_goal_title, true);
        setupPickers(view);
        setupAmountFormatter();
        
        if (existingGoal != null) {
            fillData();
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> deleteGoal());
        } else {
            updatePreview();
        }

        observeViewModel();
    }

    private void setupPickers(View view) {
        view.findViewById(R.id.btn_select_color).setOnClickListener(v -> {
            PopupHelper.showColorPicker(requireContext(), colorId -> {
                selectedColorId = colorId;
                updatePreview();
            });
        });

        view.findViewById(R.id.btn_select_icon).setOnClickListener(v -> {
            PopupHelper.showGoalIconPicker(requireContext(), iconId -> {
                selectedIconId = iconId;
                updatePreview();
            });
        });

        tvDeadline.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày hạn định")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDeadline = sdf.format(calendar.getTime());
                tvDeadline.setText(selectedDeadline);
            });

            datePicker.show(getChildFragmentManager(), "DATE_PICKER");
        });
    }

    private void setupAmountFormatter() {
        etTargetAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    etTargetAmount.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[.,]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = CurrencyFormatter.formatVND(parsed);
                            current = formatted;
                            etTargetAmount.setText(formatted);
                            etTargetAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) { }
                    } else {
                        current = "";
                        etTargetAmount.setText("");
                    }
                    etTargetAmount.addTextChangedListener(this);
                }
            }
        });
    }

    private void fillData() {
        etName.setText(existingGoal.getName());
        etTargetAmount.setText(CurrencyFormatter.formatVND(existingGoal.getTargetAmount()));
        tvDeadline.setText(existingGoal.getDeadline());
        selectedDeadline = existingGoal.getDeadline();
        selectedColorId = existingGoal.getColorId();
        selectedIconId = existingGoal.getIconId();
        updatePreview();
    }

    private void updatePreview() {
        int color = AppResourceManager.getColor(selectedColorId);
        
        ivPreviewIcon.post(() -> {
            ivPreviewIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(selectedIconId)));
            ivPreviewIcon.setImageTintList(ColorStateList.valueOf(color));
            viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(color));
        });
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    public String getFabIcon() {
        return "gmd_check";
    }

    @Override
    public void onFabClick() {
        saveGoal();
    }

    private void saveGoal() {
        String name = etName.getText().toString().trim();
        String targetStr = etTargetAmount.getText().toString().trim().replaceAll("[.,]", "");

        if (name.isEmpty() || targetStr.isEmpty() || selectedDeadline.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double targetAmount = Double.parseDouble(targetStr);
        GoalRequest request = new GoalRequest(name, targetAmount, selectedDeadline, selectedIconId, selectedColorId);

        if (existingGoal == null) {
            viewModel.addGoal(request);
        } else {
            viewModel.updateGoal(existingGoal.getId(), request);
        }
    }

    private void deleteGoal() {
        if (existingGoal != null) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage(R.string.confirm_delete_goal)
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        viewModel.deleteGoal(existingGoal.getId());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    private void observeViewModel() {
        viewModel.getIsOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                viewModel.resetOperationStatus();
                
                // Nếu đang ở màn hình Sửa (có existingGoal), cần quay lại màn hình Danh sách (về 2 cấp)
                if (existingGoal != null) {
                    Navigation.findNavController(requireView()).popBackStack(R.id.goalFragment, false);
                } else {
                    Navigation.findNavController(requireView()).navigateUp();
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
