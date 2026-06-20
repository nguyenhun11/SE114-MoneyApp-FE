package com.example.moneyapp.view.transfer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.TransferViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransferAddFragment extends BaseFragment {

    private Date selectedDate;
    private final List<Account> accountList = new ArrayList<>();
    private Account sourceAccount = null;
    private Account destinationAccount = null;

    private EditText etAmount, etDescription;
    private TextView tvSelectedSource, tvSelectedDestination;
    private IconicsImageView ivSourceIcon, ivDestinationIcon;
    private LinearLayout btnDateToday, btnDateRecent, btnPickDate;
    private TextView tvTodayValue, tvRecentValue, tvRecentLabel;
    private Date box2Date;

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

        setupHeader(view, "Chuyển khoản", true);
        initViews(view);
        setupDatePickers();
        setupComboboxes(view);
        observeViewModels();

        accountViewModel.loadAccounts();
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etDescription = view.findViewById(R.id.etDescription);

        tvSelectedSource = view.findViewById(R.id.tvSelectedSource);
        ivSourceIcon = view.findViewById(R.id.ivSourceIcon);
        tvSelectedDestination = view.findViewById(R.id.tvSelectedDestination);
        ivDestinationIcon = view.findViewById(R.id.ivDestinationIcon);

        btnDateToday = view.findViewById(R.id.btnDateToday);
        btnDateRecent = view.findViewById(R.id.btnDateRecent);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        tvTodayValue = view.findViewById(R.id.tvTodayValue);
        tvRecentLabel = view.findViewById(R.id.tvRecentLabel);
        tvRecentValue = view.findViewById(R.id.tvRecentValue);

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
    }

    private void setupDatePickers() {
        Calendar cal = Calendar.getInstance();
        selectedDate = truncateTime(cal);

        tvTodayValue.setText(shortDateFmt.format(selectedDate));

        cal.add(Calendar.DAY_OF_YEAR, -1);
        box2Date = truncateTime(cal);
        tvRecentValue.setText(shortDateFmt.format(box2Date));
        tvRecentLabel.setText("Hôm qua");

        btnDateToday.setOnClickListener(v -> selectDateBox(0));
        btnDateRecent.setOnClickListener(v -> selectDateBox(1));

        btnPickDate.setOnClickListener(v -> {
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(selectedDate);

            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                Calendar newCal = Calendar.getInstance();
                newCal.set(year, month, day);

                box2Date = truncateTime(newCal);
                tvRecentValue.setText(shortDateFmt.format(box2Date));
                tvRecentLabel.setText("Đã chọn");

                selectDateBox(1);
            }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH)).show();
        });

        selectDateBox(0);
    }

    private void selectDateBox(int index) {
        LinearLayout[] boxes = {btnDateToday, btnDateRecent};
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setBackgroundResource(i == index ? R.drawable.bg_date_selected : R.drawable.bg_input_border);
        }

        if (index == 0) {
            selectedDate = truncateTime(Calendar.getInstance());
        } else {
            selectedDate = box2Date;
        }
    }

    private Date truncateTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void setupComboboxes(View view) {
        view.findViewById(R.id.btnSelectSource).setOnClickListener(v -> showSourceAccountPopup());
        view.findViewById(R.id.btnSelectDestination).setOnClickListener(v -> showDestinationAccountPopup());
    }

    private void showSourceAccountPopup() {
        if (accountList.isEmpty()) {
            accountViewModel.loadAccounts();
            return;
        }
        PopupHelper.showAccountFilterPopup(requireContext(), accountList, sourceAccount != null ? sourceAccount.getAccountId() : null, false, this::updateSourceAccount);
    }

    private void showDestinationAccountPopup() {
        if (accountList.isEmpty()) {
            accountViewModel.loadAccounts();
            return;
        }
        PopupHelper.showAccountFilterPopup(requireContext(), accountList, destinationAccount != null ? destinationAccount.getAccountId() : null, false, this::updateDestinationAccount);
    }

    private void updateSourceAccount(Account account) {
        this.sourceAccount = account;
        tvSelectedSource.setText(account.getAccountName());
        ivSourceIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(account.getIcon())));
        ivSourceIcon.setColorFilter(AppResourceManager.getColor(account.getColor()));
    }

    private void updateDestinationAccount(Account account) {
        this.destinationAccount = account;
        tvSelectedDestination.setText(account.getAccountName());
        ivDestinationIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(account.getIcon())));
        ivDestinationIcon.setColorFilter(AppResourceManager.getColor(account.getColor()));
    }

    private void observeViewModels() {
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                if (sourceAccount == null && accountList.size() > 0) updateSourceAccount(accountList.get(0));
                if (destinationAccount == null && accountList.size() > 1) updateDestinationAccount(accountList.get(1));
            }
        });

        transferViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Chuyển khoản thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transferViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransfer() {
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();

        if (amountStr.isEmpty() || sourceAccount == null || destinationAccount == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sourceAccount.getAccountId().equals(destinationAccount.getAccountId())) {
            Toast.makeText(getContext(), "Tài khoản nguồn và đích không được trùng nhau!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                Toast.makeText(getContext(), "Số tiền chuyển phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > sourceAccount.getBalance()) {
                Toast.makeText(getContext(), "Số dư không đủ để thực hiện chuyển khoản!", Toast.LENGTH_SHORT).show();
                return;
            }

            TransferRequest request = new TransferRequest(
                    sourceAccount.getAccountId(),
                    destinationAccount.getAccountId(),
                    amount,
                    DateConverter.convertDateToString(selectedDate),
                    description
            );
            
            transferViewModel.createTransfer(request);
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