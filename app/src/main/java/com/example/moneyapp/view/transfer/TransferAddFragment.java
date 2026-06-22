package com.example.moneyapp.view.transfer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.AccountSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.TransferViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransferAddFragment extends BaseFragment {

    private String editTransferId = null;
    private Date selectedDate;
    private final List<Account> accountList = new ArrayList<>();

    private Transfer loadedTransfer = null;
    private String selectedSourceId = null;
    private String selectedDestId = null;

    private Account selectedSourceAccount = null;
    private Account selectedDestAccount = null;

    // Views
    private EditText etAmount, etDescription;
    private View btnOpenCalculator;
    private TextView tvCurrency;

    private TextView tvConvertedAmount;

    private AccountSelectorView viewSelectSource, viewSelectDest;
    private LinearLayout btnDateToday, btnDateYesterday, btnDateRecent, btnPickDate;
    private TextView tvTodayValue, tvYesterdayValue, tvRecentValue, tvRecentLabel;
    private Date box3Date;

    // ViewModels
    private AccountViewModel accountViewModel;
    private TransferViewModel transferViewModel;

    private final SimpleDateFormat shortDateFmt = new SimpleDateFormat("dd/MM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);

        if (getArguments() != null && getArguments().containsKey("transferId")) {
            editTransferId = getArguments().getString("transferId");
            setupHeader(view, "Sửa chuyển khoản", true);
            transferViewModel.loadTransferById(editTransferId);
        } else {
            setupHeader(view, "Chuyển khoản mới", true);
        }

        initViews(view);
        setupDatePickers();
        setupComboboxes();
        observeViewModels();

        accountViewModel.loadAccounts();
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        btnOpenCalculator = view.findViewById(R.id.btnOpenCalculator);
        tvCurrency = view.findViewById(R.id.tvCurrency);

        tvConvertedAmount = view.findViewById(R.id.tvConvertedAmount);
        etDescription = view.findViewById(R.id.etDescription);

        viewSelectSource = view.findViewById(R.id.viewSelectSource);
        viewSelectDest = view.findViewById(R.id.viewSelectDest);
        viewSelectSource.clear("Chọn tài khoản chuyển...");
        viewSelectDest.clear("Chọn tài khoản nhận...");

        btnDateToday = view.findViewById(R.id.btnDateToday);
        btnDateYesterday = view.findViewById(R.id.btnDateYesterday);
        btnDateRecent = view.findViewById(R.id.btnDateRecent);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        tvTodayValue = view.findViewById(R.id.tvTodayValue);
        tvYesterdayValue = view.findViewById(R.id.tvYesterdayValue);
        tvRecentLabel = view.findViewById(R.id.tvRecentLabel);
        tvRecentValue = view.findViewById(R.id.tvRecentValue);

        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

                            updateConvertedAmountUI(parsed);
                        } catch (NumberFormatException e) { }
                    } else {
                        current = "";
                        etAmount.setText("");
                        if (tvConvertedAmount != null) tvConvertedAmount.setVisibility(View.GONE);
                    }
                    etAmount.addTextChangedListener(this);
                }
            }
        });
        etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorInfo));

        btnOpenCalculator.setOnClickListener(v -> {
            String currentValue = etAmount.getText().toString();
            PopupHelper.showCalculatorPopup(requireContext(), currentValue, result -> {
                etAmount.setText(String.format(Locale.US, "%.0f", result));
            });
        });
    }

    private void updateConvertedAmountFromInput() {
        String amountStr = etAmount.getText().toString().replaceAll("[.,]", "");
        if (!amountStr.isEmpty()) {
            try {
                updateConvertedAmountUI(Double.parseDouble(amountStr));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void updateConvertedAmountUI(double inputAmount) {
        if (tvConvertedAmount == null || selectedSourceAccount == null) return;

        // Ép đơn vị tiền tệ giao dịch theo ví nguồn
        String sourceCurr = selectedSourceAccount.getCurrencyCode() != null ? selectedSourceAccount.getCurrencyCode() : "VND";
        tvCurrency.setText(sourceCurr);

        if (selectedDestAccount != null) {
            String destCurr = selectedDestAccount.getCurrencyCode() != null ? selectedDestAccount.getCurrencyCode() : "VND";
            if (!sourceCurr.equals(destCurr)) {
                tvConvertedAmount.setVisibility(View.VISIBLE);

                double converted = CurrencyFormatter.previewConversion(inputAmount, sourceCurr, destCurr);

                String formatted = CurrencyFormatter.formatVND(converted);
                tvConvertedAmount.setText(String.format(Locale.US, "≈ %s %s (Ví nhận)", formatted, destCurr));
            } else {
                tvConvertedAmount.setVisibility(View.GONE);
            }
        }
    }

    private void setupComboboxes() {
        viewSelectSource.setOnClickListener(v -> showSourceAccountPopup());
        viewSelectDest.setOnClickListener(v -> showDestAccountPopup());
    }

    private void showSourceAccountPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải danh sách tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        PopupHelper.showAccountFilterPopup(requireContext(), accountList, selectedSourceId, false, account -> {
            if (selectedDestId != null && selectedDestId.equals(account.getAccountId())) {
                Toast.makeText(getContext(), "Tài khoản nguồn không được trùng với tài khoản đích!", Toast.LENGTH_SHORT).show();
                return;
            }
            updateSelectedSource(account);
        });
    }

    private void showDestAccountPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải danh sách tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        PopupHelper.showAccountFilterPopup(requireContext(), accountList, selectedDestId, false, account -> {
            if (selectedSourceId != null && selectedSourceId.equals(account.getAccountId())) {
                Toast.makeText(getContext(), "Tài khoản đích không được trùng với tài khoản nguồn!", Toast.LENGTH_SHORT).show();
                return;
            }
            updateSelectedDest(account);
        });
    }

    private void updateSelectedSource(Account account) {
        this.selectedSourceAccount = account;
        this.selectedSourceId = account.getAccountId();
        viewSelectSource.setAccount(account, true);

        if (tvCurrency != null && account.getCurrencyCode() != null) {
            tvCurrency.setText(account.getCurrencyCode());
        }
        updateConvertedAmountFromInput();
    }

    private void updateSelectedDest(Account account) {
        this.selectedDestAccount = account;
        this.selectedDestId = account.getAccountId();
        viewSelectDest.setAccount(account, true);
        updateConvertedAmountFromInput();
    }

    private void observeViewModels() {
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);

                if (loadedTransfer != null) {
                    bindAccountsToUI(loadedTransfer);
                }
            }
        });

        transferViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), editTransferId != null ? "Cập nhật thành công!" : "Chuyển khoản thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transferViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        transferViewModel.getSelectedTransfer().observe(getViewLifecycleOwner(), transfer -> {
            if (transfer != null && editTransferId != null) {
                loadedTransfer = transfer;

                etAmount.setText(String.valueOf(transfer.getSourceAmount().longValue()));
                if (transfer.getDescription() != null) etDescription.setText(transfer.getDescription());

                restoreDateToUI(transfer.getDate());

                selectedSourceId = transfer.getSourceAccountId();
                selectedDestId = transfer.getDestinationAccountId();

                viewSelectSource.setPreloadedData(
                        transfer.getSourceAccountName(),
                        transfer.getSourceAccountIcon(),
                        transfer.getSourceAccountColor()
                );

                viewSelectDest.setPreloadedData(
                        transfer.getDestinationAccountName(),
                        transfer.getDestinationAccountIcon(),
                        transfer.getDestinationAccountColor()
                );

                bindAccountsToUI(transfer);
            }
        });
    }

    private void bindAccountsToUI(Transfer transfer) {
        if (accountList.isEmpty() || transfer == null) return;

        for (Account account : accountList) {
            if (account.getAccountId().equalsIgnoreCase(transfer.getSourceAccountId())) {
                updateSelectedSource(account);
            }
            if (account.getAccountId().equalsIgnoreCase(transfer.getDestinationAccountId())) {
                updateSelectedDest(account);
            }
        }
    }

    private void restoreDateToUI(Date transferDate) {
        if (transferDate == null) return;

        Date tDate = truncateTime(transferDate);
        Calendar cal = Calendar.getInstance();
        Date today = truncateTime(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = truncateTime(cal.getTime());

        if (tDate.equals(today)) {
            selectDateBox(0);
        } else if (tDate.equals(yesterday)) {
            selectDateBox(1);
        } else {
            box3Date = tDate;
            tvRecentValue.setText(shortDateFmt.format(box3Date));
            tvRecentLabel.setText("Đã chọn");
            selectDateBox(2);
        }
    }

    private void setupDatePickers() {
        Calendar cal = Calendar.getInstance();
        selectedDate = truncateTime(cal.getTime());

        tvTodayValue.setText(shortDateFmt.format(selectedDate));

        cal.add(Calendar.DAY_OF_YEAR, -1);
        tvYesterdayValue.setText(shortDateFmt.format(truncateTime(cal.getTime())));

        cal.add(Calendar.DAY_OF_YEAR, -1);
        box3Date = truncateTime(cal.getTime());
        tvRecentValue.setText(shortDateFmt.format(box3Date));
        tvRecentLabel.setText("Gần đây");

        btnDateToday.setOnClickListener(v -> selectDateBox(0));
        btnDateYesterday.setOnClickListener(v -> selectDateBox(1));
        btnDateRecent.setOnClickListener(v -> selectDateBox(2));

        btnPickDate.setOnClickListener(v -> {
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(selectedDate);
            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                Calendar newCal = Calendar.getInstance();
                newCal.set(year, month, day);
                box3Date = truncateTime(newCal.getTime());
                tvRecentValue.setText(shortDateFmt.format(box3Date));
                tvRecentLabel.setText("Đã chọn");
                selectDateBox(2);
            }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH)).show();
        });

        if (editTransferId == null) {
            selectDateBox(0);
        }
    }

    private void selectDateBox(int index) {
        LinearLayout[] boxes = {btnDateToday, btnDateYesterday, btnDateRecent};
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setBackgroundResource(i == index ? R.drawable.bg_date_selected : R.drawable.bg_input_border);
        }

        Calendar cal = Calendar.getInstance();
        if (index == 0) selectedDate = truncateTime(cal.getTime());
        else if (index == 1) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            selectedDate = truncateTime(cal.getTime());
        } else if (index == 2) {
            selectedDate = box3Date;
        }
    }

    private Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void saveTransfer() {
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();

        if (amountStr.isEmpty() || selectedSourceAccount == null || selectedDestAccount == null) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền và chọn đủ 2 tài khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double sourceAmount = Double.parseDouble(amountStr);
            if (sourceAmount <= 0) {
                Toast.makeText(getContext(), "Số tiền chuyển phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            TransferRequest request = new TransferRequest(
                    selectedSourceId,
                    selectedDestId,
                    sourceAmount,
                    DateConverter.convertDateToString(selectedDate),
                    description
            );

            if (editTransferId != null) {
                transferViewModel.updateTransfer(editTransferId, request);
            } else {
                transferViewModel.createTransfer(request);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }

    @Override
    protected String getFabIcon() { return "gmd_check"; }

    @Override
    protected void onFabClick() {
        saveTransfer();
    }
}