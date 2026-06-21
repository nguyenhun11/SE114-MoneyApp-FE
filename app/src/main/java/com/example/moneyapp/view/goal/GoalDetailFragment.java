package com.example.moneyapp.view.goal;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.PopupHelper;
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

public class GoalDetailFragment extends Fragment {

    private Goal goal;
    private GoalViewModel viewModel;
    private AccountViewModel accountViewModel;

    private TextView tvName, tvDeadline, tvPercent, tvCurrent, tvTarget;
    private CircularProgressIndicator cpProgress;
    private FrameLayout flIconContainer;
    private IconicsImageView ivIcon;
    private AppCompatImageButton btnBack, btnEdit;
    private MaterialButton btnDeposit;

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
        btnBack = view.findViewById(R.id.btn_back);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDeposit = view.findViewById(R.id.btn_deposit);

        setupIcons();
        displayGoal();

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("goal", goal);
            Navigation.findNavController(v).navigate(R.id.goalAddFragment, bundle);
        });

        btnDeposit.setOnClickListener(v -> showDepositDialog());

        observeViewModel();
        viewModel.fetchGoals(); // Tải lại dữ liệu khi vào màn hình
    }

    private void setupIcons() {
        int color = ContextCompat.getColor(requireContext(), R.color.white);
        
        IconicsDrawable backDrawable = new IconicsDrawable(requireContext(), "gmd_arrow_back");
        backDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        btnBack.setImageDrawable(backDrawable);
        
        IconicsDrawable editDrawable = new IconicsDrawable(requireContext(), "gmd_edit");
        editDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        btnEdit.setImageDrawable(editDrawable);
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

        // Load accounts and setup popup
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
                        viewModel.depositToGoal(goal, amount, accountSelector.getSelectedAccount().getAccountId());
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập tiền và chọn ví", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            // Cập nhật lại thông tin goal hiện tại từ danh sách mới
            for (Goal g : goals) {
                if (g.getId() == goal.getId()) {
                    goal = g;
                    displayGoal();
                    break;
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
