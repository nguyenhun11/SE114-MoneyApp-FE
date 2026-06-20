package com.example.moneyapp.view.transaction;

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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.AccountSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TransactionAddFragment extends BaseFragment {

    private String editTransactionId = null;
    private CategoryType transactionType = CategoryType.EXPENSE;
    private Date selectedDate;
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    private Category selectedCategory = null;
    private Account selectedAccount = null;

    // region Views
    private EditText etAmount, etDescription;
    private TextView tvSelectedCategory;
    private IconicsImageView ivCategoryIcon;

    private AccountSelectorView viewSelectSource;

    private LinearLayout btnDateToday, btnDateYesterday, btnDateRecent, btnPickDate;
    private TextView tvTodayValue, tvYesterdayValue, tvRecentValue, tvRecentLabel;
    private Date box3Date;
    // endregion

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;

    private final SimpleDateFormat shortDateFmt = new SimpleDateFormat("dd/MM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        if (getArguments() != null && getArguments().containsKey("transactionId")) {
            editTransactionId = getArguments().getString("transactionId");
            setupHeader(view, "Sửa giao dịch", true);
            transactionViewModel.loadTransactionById(editTransactionId);
        } else {
            setupHeader(view, "Giao dịch mới", true);
        }

        initViews(view);
        setupDatePickers();
        setupComboboxes(view);

        setupIncomeExpenseTabs(view, isExpense -> {
            transactionType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            updateAmountColor(isExpense);

            if (editTransactionId == null || categoryList.isEmpty()) {
                selectedCategory = null;
                tvSelectedCategory.setText("Chọn hạng mục...");
                ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), "gmd_category"));
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
            }
            categoryViewModel.loadCategories(transactionType);
        });

        observeViewModels();
        accountViewModel.loadAccounts();
        categoryViewModel.loadCategories(CategoryType.EXPENSE);
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etDescription = view.findViewById(R.id.etDescription);

        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
        ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);

        viewSelectSource = view.findViewById(R.id.viewSelectSource);
        viewSelectSource.clear("Chọn nguồn tiền...");

        btnDateToday = view.findViewById(R.id.btnDateToday);
        btnDateYesterday = view.findViewById(R.id.btnDateYesterday);
        btnDateRecent = view.findViewById(R.id.btnDateRecent);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        tvTodayValue = view.findViewById(R.id.tvTodayValue);
        tvYesterdayValue = view.findViewById(R.id.tvYesterdayValue);
        tvRecentLabel = view.findViewById(R.id.tvRecentLabel);
        tvRecentValue = view.findViewById(R.id.tvRecentValue);

        updateAmountColor(true);

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
                            String formatted = com.example.moneyapp.utils.CurrencyFormatter.formatVND(parsed);
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

    private void updateAmountColor(boolean isExpense) {
        if (isExpense) {
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
        } else {
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
        }
    }

    private void observeViewModels() {
        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), t -> {
            if (t != null && editTransactionId != null) {
                etAmount.setText(String.valueOf((long) Math.abs(t.getAmount())));
                if (t.getNote() != null) etDescription.setText(t.getNote());

                box3Date = truncateTime(t.getDate());
                tvRecentValue.setText(shortDateFmt.format(box3Date));
                tvRecentLabel.setText("Ngày GD");
                selectDateBox(2);

                Account mockAccount = new Account(
                        t.getAccountId(), t.getAccountName(), 0.0,
                        t.getAccountColorId(), t.getAccountIconId(), "", true, 0, new Date(), new Date()
                );
                updateSelectedAccount(mockAccount);

                Category mockCategory = new Category(
                        t.getCategoryId(), t.getCategoryName(), t.getType(), "", "", 0.0,
                        t.getCategoryColorId(), t.getCategoryIconId(), 0, new Date(), new Date()
                );
                updateSelectedCategory(mockCategory);

                transactionType = t.getType();
                updateAmountColor(transactionType == CategoryType.EXPENSE);
            }
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                if (selectedAccount == null && !accountList.isEmpty() && editTransactionId == null) {
                    updateSelectedAccount(accountList.get(0));
                }
            }
        });

        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
            }
        });

        transactionViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                String msg = (editTransactionId != null) ? "Cập nhật giao dịch thành công!" : "Thêm giao dịch thành công!";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date truncateTime(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
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

        if (editTransactionId == null) {
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

    private void setupComboboxes(View view) {
        view.findViewById(R.id.btnSelectCategory).setOnClickListener(v -> showCategoryPopup());
        viewSelectSource.setOnClickListener(v -> showAccountPopup());
    }

    private void showCategoryPopup() {
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải dữ liệu", Toast.LENGTH_SHORT).show();
            categoryViewModel.loadCategories(transactionType);
            return;
        }
        PopupHelper.showCategoryFilterPopup(requireContext(), categoryList, false, this::updateSelectedCategory);
    }

    private void showAccountPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải nguồn tiền", Toast.LENGTH_SHORT).show();
            accountViewModel.loadAccounts();
            return;
        }
        String currentAccountId = selectedAccount != null ? selectedAccount.getAccountId() : null;
        PopupHelper.showAccountFilterPopup(requireContext(), accountList, currentAccountId, true, this::updateSelectedAccount);
    }

    public void updateSelectedCategory(Category category) {
        this.selectedCategory = category;
        tvSelectedCategory.setText(category.getCategoryName());

        int color = AppResourceManager.getColor(category.getColor());
        String iconName = AppResourceManager.getIconName(category.getIcon());

        ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), iconName));
        ivCategoryIcon.setColorFilter(color);

        requireView().clearFocus();
    }

    public void updateSelectedAccount(Account account) {
        this.selectedAccount = account;
        viewSelectSource.setAccount(account, false);
        requireView().clearFocus();
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();

        if (amountStr.isEmpty() || selectedCategory == null || selectedAccount == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ số tiền, hạng mục và nguồn tiền!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amountValue = Double.parseDouble(amountStr);
            amountValue = Math.abs(amountValue);

            Transaction newTransaction = new Transaction(
                    editTransactionId != null ? editTransactionId : UUID.randomUUID().toString(),
                    selectedAccount.getAccountId(), selectedAccount.getAccountName(),
                    selectedCategory.getCategoryId(), selectedCategory.getCategoryName(),
                    transactionType, amountValue, selectedDate, description,
                    selectedCategory.getColor(), selectedCategory.getIcon(),
                    selectedAccount.getColor(), selectedAccount.getIcon(),
                    new ArrayList<>(), new Date()
            );

            if (editTransactionId != null) {
                transactionViewModel.updateTransaction(newTransaction);
            } else {
                transactionViewModel.addTransaction(newTransaction);
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
        saveTransaction();
    }
}