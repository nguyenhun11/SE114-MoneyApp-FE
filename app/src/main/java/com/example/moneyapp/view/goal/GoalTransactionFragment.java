package com.example.moneyapp.view.goal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.utils.RewardHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.AccountSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.GoalViewModel;

public class GoalTransactionFragment extends BaseFragment {

    public static final String TYPE_DEPOSIT = "DEPOSIT";
    public static final String TYPE_WITHDRAW = "WITHDRAW";

    private GoalViewModel goalViewModel;
    private AccountViewModel accountViewModel;

    private Goal currentGoal;
    private String transactionType = TYPE_DEPOSIT;
    private boolean isGoalJustCompleted = false;

    private EditText etAmount;
    private AccountSelectorView viewSelectAccount;
    private TextView tvSourceLabel, tvAvailableHint, tvAmountError;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goalViewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        if (getArguments() != null) {
            currentGoal = (Goal) getArguments().getSerializable("goal");
            transactionType = getArguments().getString("type", TYPE_DEPOSIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = currentGoal != null ? currentGoal.getName() : "Giao dịch Mục tiêu";
        setupHeader(view, title, true);

        etAmount = view.findViewById(R.id.etAmount);
        viewSelectAccount = view.findViewById(R.id.view_select_account);
        tvSourceLabel = view.findViewById(R.id.tv_source_label);
        tvAvailableHint = view.findViewById(R.id.tv_available_balance_hint);
        tvAmountError = view.findViewById(R.id.tv_amount_error);

        String[] tabs = {"Nạp tiền", "Rút tiền"};
        int initialTab = transactionType.equals(TYPE_DEPOSIT) ? 0 : 1;

        setupHeaderTabs(view, tabs, initialTab, index -> {
            hideKeyboard();
            if (index == 0) {
                transactionType = TYPE_DEPOSIT;
            } else {
                transactionType = TYPE_WITHDRAW;
            }
            updateUIByMode();
        });

        setupInputAndAccountSelection();
        observeViewModel();
        updateUIByMode();
    }

    private void setupInputAndAccountSelection() {
        accountViewModel.loadAccounts();
        viewSelectAccount.setOnClickListener(v -> {
            hideKeyboard();
            PopupHelper.showAccountFilterPopup(requireContext(), accountViewModel.getAccountsLiveData().getValue(), "Chọn ví", false, account -> {
                viewSelectAccount.setAccount(account, false);
                checkSaveConditions();
            });
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        current = "";
                        etAmount.setText("");
                    }
                    etAmount.addTextChangedListener(this);
                }
                checkSaveConditions();
            }
        });

        requireView().post(this::checkSaveConditions);
    }

    private void updateUIByMode() {
        if (transactionType.equals(TYPE_WITHDRAW)) {
            tvSourceLabel.setText("Rút về tài khoản");
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWarning));
        } else {
            tvSourceLabel.setText("Nạp từ tài khoản");
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorInfo));
        }
        checkSaveConditions();
    }

    private void checkSaveConditions() {
        boolean isValid = true;
        tvAmountError.setVisibility(View.GONE); // Mặc định ẩn lỗi

        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        double inputAmount = amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr);

        if (inputAmount <= 0) {
            isValid = false;
        }

        var selectedAccount = viewSelectAccount.getSelectedAccount();
        if (selectedAccount == null) {
            isValid = false;
            tvAvailableHint.setText("Vui lòng chọn tài khoản");
        } else {
            if (transactionType.equals(TYPE_WITHDRAW)) {
                // Đang Rút: Check với số dư của Ống heo
                double maxWithdrawable = currentGoal.getCurrentAmount();
                tvAvailableHint.setText(String.format("Có thể rút tối đa: %s VND",
                        CurrencyFormatter.formatVND(maxWithdrawable)));

                if (inputAmount > maxWithdrawable) {
                    isValid = false;
                    tvAmountError.setText("Số tiền rút vượt quá số dư của mục tiêu!");
                    tvAmountError.setVisibility(View.VISIBLE);
                }

            } else {
                double maxDepositable = selectedAccount.getAvailableBalance();
                tvAvailableHint.setText(String.format("Khả dụng trong ví: %s %s",
                        CurrencyFormatter.formatVND(maxDepositable),
                        selectedAccount.getCurrencyCode()));

                if (inputAmount > maxDepositable) {
                    isValid = false;
                    tvAmountError.setText("Số dư khả dụng trong ví không đủ!");
                    tvAmountError.setVisibility(View.VISIBLE);
                }
            }
        }

        setFabEnabled(isValid);
    }

    private void performTransaction() {
        hideKeyboard();
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        double amount = Double.parseDouble(amountStr);
        String accountId = viewSelectAccount.getSelectedAccount().getAccountId().toString();

        if (transactionType.equals(TYPE_DEPOSIT)) {
            double projectedAmount = currentGoal.getCurrentAmount() + amount;
            if (projectedAmount >= currentGoal.getTargetAmount() && currentGoal.getProgressPercent() < 100) {
                isGoalJustCompleted = true;
            }
            goalViewModel.depositToGoal(currentGoal.getId(), amount, accountId);
        } else {
            goalViewModel.withdrawFromGoal(currentGoal.getId(), amount, accountId);
        }
    }

    private void observeViewModel() {
        goalViewModel.getIsOperationSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Giao dịch thành công!", Toast.LENGTH_SHORT).show();

                if (isGoalJustCompleted) {
                    RewardHelper.showBigReward(requireContext(), "+100 PP",
                            "Chúc mừng! Bạn đã hoàn thành mục tiêu '" + currentGoal.getName() + "'.");
                    isGoalJustCompleted = false;
                }

                goalViewModel.resetOperationStatus();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        goalViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
            }
        });
    }

    @Override
    protected String getFabIcon() {
        return "gmd_check";
    }

    @Override
    protected String getFabLabel() {
        return "Xác nhận";
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected void onFabClick() {
        performTransaction();
    }
}