package com.example.moneyapp.view.goal;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.GoalViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GoalAddFragment extends BaseFragment {

    private EditText etName, etTargetAmount;
    private TextView tvDeadline;
    private IconicsImageView ivPreviewIcon;
    private View viewPreviewColor;

    private GoalViewModel viewModel;
    private int selectedColorId = 0;
    private int selectedIconId = 16; // Mặc định là gmd-star
    private String selectedDeadline = ""; // Chuỗi gốc lưu xuống DB (yyyy-MM-dd)
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

        if (existingGoal != null) {
            setupHeader(view, "Sửa mục tiêu",
                    "gmd_arrow_back", v -> Navigation.findNavController(v).navigateUp(),
                    "gmd_delete_outline", v -> deleteGoal());
        } else {
            setupHeader(view, "Thêm mục tiêu mới", true);
        }

        setupPickers(view);
        setupAmountFormatter();

        if (existingGoal != null) {
            fillData();
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
                // FIX LỖI 1: Bắt buộc dùng TimeZone UTC để không bị trừ lùi mất 1 ngày ở VN
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);

                // Format để gửi lên Backend
                SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDeadline = sdfApi.format(calendar.getTime());

                // Format để hiển thị cho User xem
                SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvDeadline.setText(sdfDisplay.format(calendar.getTime()));
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

        selectedDeadline = existingGoal.getDeadline(); // Giả sử Backend trả về "2026-12-31"

        try {
            SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date parsedDate = sdfApi.parse(selectedDeadline);
            if(parsedDate != null) {
                tvDeadline.setText(sdfDisplay.format(parsedDate)); // Hiển thị 31/12/2026
            } else {
                tvDeadline.setText(selectedDeadline);
            }
        } catch (Exception e) {
            tvDeadline.setText(selectedDeadline);
        }

        selectedColorId = existingGoal.getColorId();
        selectedIconId = existingGoal.getIconId();
        updatePreview();
    }

    private void updatePreview() {
        ivPreviewIcon.post(() -> {
            ivPreviewIcon.setImageDrawable(AppResourceManager.getWhiteIcon(requireContext(), selectedIconId));
            if (selectedColorId == 0) {
                viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorEmpty)));
            } else {
                int color = AppResourceManager.getColor(selectedColorId);
                viewPreviewColor.setBackgroundTintList(ColorStateList.valueOf(color));
            }
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

    @Override
    protected String getFabLabel(){
        return "Lưu mục tiêu";
    }

    private void saveGoal() {
        String name = etName.getText().toString().trim();
        String targetStr = etTargetAmount.getText().toString().trim().replaceAll("[.,]", "");

        if (name.isEmpty() || targetStr.isEmpty() || selectedDeadline.isEmpty()) {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Vui lòng nhập đầy đủ thông tin");
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
            if (existingGoal.getCurrentAmount() > 0) {
                DialogHelper.showSimpleDialog(requireContext(), "Thông báo", 
                    "Mục tiêu này vẫn còn số dư. Vui lòng rút hết tiền trước khi xóa.");
                return;
            }

            DialogHelper.showConfirmDialog(requireContext(), "Xác nhận xóa", getString(R.string.confirm_delete_goal), () -> {
                viewModel.deleteGoal(existingGoal.getId());
            }, null);
        }
    }

    private void observeViewModel() {
        viewModel.getIsOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Thao tác thành công", () -> {
                    viewModel.resetOperationStatus();

                    if (existingGoal != null) {
                        Navigation.findNavController(requireView()).popBackStack(R.id.goalFragment, false);
                    } else {
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                });
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
            }
        });
    }
}