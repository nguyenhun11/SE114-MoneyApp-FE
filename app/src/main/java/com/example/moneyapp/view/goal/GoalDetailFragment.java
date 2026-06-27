package com.example.moneyapp.view.goal;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast; // Thêm Toast

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.utils.RewardHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.view.components.AccountSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.GoalViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Locale;

public class GoalDetailFragment extends BaseFragment {

    private Goal goal;
    private GoalViewModel viewModel;
    private AccountViewModel accountViewModel;

    private TextView tvName, tvDeadline, tvPercent, tvCurrent, tvTarget;
    private CircularProgressIndicator cpProgress;
    private FrameLayout flIconContainer;
    private IconicsImageView ivIcon;
    private MaterialButton btnDeposit;

    private boolean isGoalJustCompleted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        if (getArguments() != null) {
            goal = (Goal) getArguments().getSerializable("goal");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getUiHandler().setBottomNavigationVisibility(false);
            ((MainActivity) getActivity()).getUiHandler().setFABVisibility(false);
        }

        tvName = view.findViewById(R.id.tv_goal_name);
        tvDeadline = view.findViewById(R.id.tv_goal_deadline);
        tvPercent = view.findViewById(R.id.tv_percent);
        tvCurrent = view.findViewById(R.id.tv_current_amount);
        tvTarget = view.findViewById(R.id.tv_target_amount);
        cpProgress = view.findViewById(R.id.cp_progress);
        flIconContainer = view.findViewById(R.id.fl_icon_container);
        ivIcon = view.findViewById(R.id.iv_goal_icon);
        btnDeposit = view.findViewById(R.id.btn_deposit);

        displayGoal();
        setupHeader(view, "Chi tiết mục tiêu",
                "gmd_arrow_back", v -> Navigation.findNavController(v).navigateUp(),
                "gmd_edit", v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("goal", goal);
                    Navigation.findNavController(v).navigate(R.id.goalAddFragment, bundle);
                });

        btnDeposit.setOnClickListener(v -> showDepositDialog());

        observeViewModel();
        viewModel.fetchGoals(); // Load dữ liệu mới nhất từ server
    }

    private void displayGoal() {
        if (goal == null) return;

        tvName.setText(goal.getName());

        String displayDate = DateConverter.formatToDisplay(goal.getDeadline());
        tvDeadline.setText(getString(R.string.goal_deadline_label, displayDate));

        int percent = goal.getProgressPercent();
        tvPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
        cpProgress.setProgress(percent);

        tvCurrent.setText(CurrencyFormatter.formatVND(goal.getCurrentAmount()));
        tvTarget.setText(CurrencyFormatter.formatVND(goal.getTargetAmount()));

        int color = AppResourceManager.getColor(goal.getColorId());
        flIconContainer.setBackgroundTintList(ColorStateList.valueOf(color));
        ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(requireContext(), goal.getIconId()));

        cpProgress.setIndicatorColor(color);
        tvPercent.setTextColor(color);
        btnDeposit.setBackgroundColor(color);
    }

    private void showDepositDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog_deposit, null);
        EditText etAmount = dialogView.findViewById(R.id.et_deposit_amount);
        AccountSelectorView accountSelector = dialogView.findViewById(R.id.view_select_account);

        accountViewModel.loadAccounts();
        accountSelector.setOnClickListener(v -> {
            PopupHelper.showAccountFilterPopup(requireContext(), accountViewModel.getAccountsLiveData().getValue(), "Chọn nguồn tiền", false, account -> {
                accountSelector.setAccount(account, false);
            });
        });

        etAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    etAmount.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[.,]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = CurrencyFormatter.formatVND(parsed);
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

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.deposit_goal)
                .setView(dialogView)
                .setPositiveButton("Nạp", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
                    if (!amountStr.isEmpty() && accountSelector.getSelectedAccount() != null) {
                        double amount = Double.parseDouble(amountStr);

                        // ĐÃ SỬA: Lấy String AccountID và chỉ truyền GoalID
                        String accountId = accountSelector.getSelectedAccount().getAccountId().toString();

                        // Check trước xem nếu nạp thêm khoản này thì có 100% không để chốt Reward
                        double projectedAmount = goal.getCurrentAmount() + amount;
                        if (projectedAmount >= goal.getTargetAmount() && goal.getProgressPercent() < 100) {
                            isGoalJustCompleted = true;
                        }

                        viewModel.depositToGoal(goal.getId(), amount, accountId);
                    } else {
                        DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Vui lòng nhập số tiền và chọn nguồn tiền hợp lệ.");
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            for (Goal g : goals) {
                if (g.getId() == goal.getId()) {
                    goal = g;
                    displayGoal();
                    break;
                }
            }
        });

        viewModel.getIsOperationSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Giao dịch thành công!", Toast.LENGTH_SHORT).show();

                if (isGoalJustCompleted) {
                    RewardHelper.showBigReward(requireContext(), "+100 PP",
                            "Chúc mừng! Bạn đã hoàn thành mục tiêu '" + goal.getName() + "'. Thành phố của bạn đang phát triển vượt bậc!");
                    isGoalJustCompleted = false; // Reset cờ
                }

                viewModel.resetOperationStatus();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
            }
        });
    }
}