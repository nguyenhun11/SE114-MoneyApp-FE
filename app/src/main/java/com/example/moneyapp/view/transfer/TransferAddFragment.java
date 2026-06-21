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

    // Lưu trữ object Account để lấy thông tin CurrencyCode
    private Account selectedSourceAccount = null;
    private Account selectedDestAccount = null;

    // Views
    private EditText etAmount, etDescription;
    private View btnOpenCalculator, btnSelectCurrency;
    private TextView tvCurrency;

    // TEXTVIEW MỚI: Hiển thị số tiền thực nhận ở ví đích
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
        btnSelectCurrency = view.findViewById(R.id.btnSelectCurrency);
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

                            // Tự động tính toán khi gõ
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

        // LOGIC MỚI: Khóa nút chọn tiền tệ vì Chuyển khoản bắt buộc tính theo tiền của Ví nguồn
        btnSelectCurrency.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đơn vị tiền tệ được xác định theo Ví nguồn", Toast.LENGTH_SHORT).show();
        });
    }

    // Tỷ giá giả lập
    private double getMockExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null || fromCurrency.equals(toCurrency)) return 1.0;

        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) return 25000.0;
        if (fromCurrency.equals("EUR") && toCurrency.equals("VND")) return 27000.0;
        if (fromCurrency.equals("JPY") && toCurrency.equals("VND")) return 160.0;
        if (fromCurrency.equals("VND") && toCurrency.equals("USD")) return 1.0 / 25000.0;

        return 1.0;
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

        String sourceCurr = selectedSourceAccount.getCurrencyCode() != null ? selectedSourceAccount.getCurrencyCode() : "VND";
        tvCurrency.setText(sourceCurr);

        if (selectedDestAccount != null) {
            String destCurr = selectedDestAccount.getCurrencyCode() != null ? selectedDestAccount.getCurrencyCode() : "VND";
            if (!sourceCurr.equals(destCurr)) {
                tvConvertedAmount.setVisibility(View.VISIBLE);
                double rate = getMockExchangeRate(sourceCurr, destCurr);
                double converted = inputAmount * rate;

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
        updateConvertedAmountFromInput(); // Tính lại tỷ giá khi đổi ví
    }

    private void updateSelectedDest(Account account) {
        this.selectedDestAccount = account;
        this.selectedDestId = account.getAccountId();
        viewSelectDest.setAccount(account, true);
        updateConvertedAmountFromInput(); // Tính lại tỷ giá khi đổi ví
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
            }
        });
    }

    private void bindAccountsToUI(Transfer transfer) {
        if (accountList.isEmpty() || transfer == null) return;

        for (Account account : accountList) {
            if (account.getAccountId().equals(transfer.getSourceAccountId())) {
                updateSelectedSource(account);
            }
            if (account.getAccountId().equals(transfer.getDestinationAccountId())) {
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
        // ... (Giữ nguyên như code cũ của bạn)
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
        // ... (Giữ nguyên như code cũ của bạn)
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

            String sourceCurr = selectedSourceAccount.getCurrencyCode() != null ? selectedSourceAccount.getCurrencyCode() : "VND";
            String destCurr = selectedDestAccount.getCurrencyCode() != null ? selectedDestAccount.getCurrencyCode() : "VND";
            String systemCurr = "VND"; // Thay bằng cấu hình của User sau này

            double destRate = getMockExchangeRate(sourceCurr, destCurr);
            double destinationAmount = sourceAmount * destRate;

            double baseRate = getMockExchangeRate(sourceCurr, systemCurr);
            double baseAmount = sourceAmount * baseRate;

            TransferRequest request = new TransferRequest(
                    selectedSourceId,
                    selectedDestId,
                    sourceAmount,
                    destinationAmount,
                    baseAmount,
                    1.0, // Vì mình lấy ví nguồn làm mốc chuẩn
                    destRate,
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