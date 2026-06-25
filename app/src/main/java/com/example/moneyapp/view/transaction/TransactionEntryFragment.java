package com.example.moneyapp.view.transaction;

import android.app.DatePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Mood;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.utils.RewardHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.AccountSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;
import com.example.moneyapp.viewmodel.TransferViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TransactionEntryFragment extends BaseFragment {

    public enum EntryMode { EXPENSE, INCOME, TRANSFER }
    private EntryMode currentMode = EntryMode.EXPENSE;

    private String editTransactionId = null;
    private String editTransferId = null;
    private boolean isEditing = false;
    private boolean isDataInitialized = false;

    private boolean isSaving = false;
    private int pendingOperations = 0;

    private Date selectedDate;
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    // Dữ liệu đang chọn
    private Category selectedCategory = null;
    private Account selectedSourceAccount = null;
    private Account selectedDestAccount = null;
    private String currentCurrencyCode = "VND";

    // region Views
    private EditText etAmount, etDescription;
    private View btnOpenCalculator, btnSelectCurrency;
    private TextView tvCurrency, tvConvertedAmount;

    // Các Block UI cần ẩn hiện
    private View layoutCategory, layoutDestAccount, layoutMoodSelector;
    private TextView tvSourceAccountLabel, tvSelectedCategory;
    private IconicsImageView ivCategoryIcon;
    private AccountSelectorView viewSelectSource, viewSelectDest;

    private LinearLayout btnDateToday, btnDateYesterday, btnDateRecent, btnPickDate;
    private TextView tvTodayValue, tvYesterdayValue, tvRecentValue, tvRecentLabel;
    private Date box3Date;

    private MoodSelectorAdapter moodAdapter;
    private int selectedMoodId = 0;
    // endregion

    // ViewModels
    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;
    private TransferViewModel transferViewModel;

    private final SimpleDateFormat shortDateFmt = new SimpleDateFormat("dd/MM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);

        initViews(view);
        setupDatePickers();
        setupComboboxes(view);
        observeViewModels();

        if (getArguments() != null) {
            if (getArguments().containsKey("transactionId")) {
                editTransactionId = getArguments().getString("transactionId");
                transactionViewModel.loadTransactionById(editTransactionId);
                isEditing = true;
            } else if (getArguments().containsKey("transferId")) {
                editTransferId = getArguments().getString("transferId");
                transferViewModel.loadTransferById(editTransferId);
                currentMode = EntryMode.TRANSFER;
                isEditing = true;
            }
        }

        // CHỈ KHỞI TẠO TABS NGAY LẦN ĐẦU NẾU LÀ TẠO MỚI
        if (!isEditing) {
            String[] tabs = {"Chi tiêu", "Thu nhập", "Chuyển khoản"};
            setupHeaderTabs(view, tabs, 0, index -> {
                hideKeyboard();
                if (index == 0) currentMode = EntryMode.EXPENSE;
                else if (index == 1) currentMode = EntryMode.INCOME;
                else currentMode = EntryMode.TRANSFER;

                updateUIByMode();
            });

            updateUIByMode();
        }

        accountViewModel.loadAccounts();
        view.post(this::checkSaveConditions);
    }

    private void updateUIByMode() {
        String headerTitle = "";

        if (currentMode == EntryMode.EXPENSE || currentMode == EntryMode.INCOME) {
            if (currentMode == EntryMode.EXPENSE) {
                headerTitle = isEditing ? "Sửa chi tiêu" : "Chi tiêu mới";
            } else {
                headerTitle = isEditing ? "Sửa thu nhập" : "Thu nhập mới";
            }
            setupHeader(requireView(), headerTitle, true);

            layoutCategory.setVisibility(View.VISIBLE);
            layoutDestAccount.setVisibility(View.GONE);
            tvSourceAccountLabel.setText("Nguồn tiền");

            CategoryType type = (currentMode == EntryMode.EXPENSE) ? CategoryType.EXPENSE : CategoryType.INCOME;
            categoryViewModel.loadCategories(type);

            etAmount.setTextColor(ContextCompat.getColor(requireContext(),
                    type == CategoryType.EXPENSE ? R.color.colorDanger : R.color.colorSuccess));

            layoutMoodSelector.setVisibility(View.VISIBLE);

            if (selectedCategory == null || selectedCategory.getType() != type) {
                selectedCategory = null;
                tvSelectedCategory.setText("Chọn hạng mục...");
                ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), "gmd_category"));
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
            }

        } else {
            headerTitle = isEditing ? "Sửa chuyển khoản" : "Chuyển khoản mới";
            setupHeader(requireView(), headerTitle, true);

            layoutCategory.setVisibility(View.GONE);
            layoutMoodSelector.setVisibility(View.GONE);
            layoutDestAccount.setVisibility(View.VISIBLE);
            tvSourceAccountLabel.setText("Từ tài khoản");
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorInfo));
        }

        updateConvertedAmountFromInput();
        checkSaveConditions();
    }

    private void checkSaveConditions() {
        if (isSaving) {
            setFabEnabled(false);
            return;
        }

        boolean isValid = true;
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");

        if (amountStr.isEmpty()) {
            isValid = false;
        } else {
            try {
                if (Double.parseDouble(amountStr) <= 0) isValid = false;
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        if (currentMode == EntryMode.TRANSFER) {
            if (selectedSourceAccount == null || selectedDestAccount == null) isValid = false;
        } else {
            if (selectedCategory == null || selectedSourceAccount == null) isValid = false;
        }

        setFabEnabled(isValid);
    }

    private void initViews(View view) {
        ScrollView mainScrollView = view.findViewById(R.id.main_scroll_view);

        View scrollContent = mainScrollView.getChildAt(0);
        if (scrollContent != null) {
            scrollContent.setFocusable(true);
            scrollContent.setFocusableInTouchMode(true);
        }

        etDescription = view.findViewById(R.id.etDescription);

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

                if (etDescription.hasFocus()) {
                    mainScrollView.postDelayed(() -> {
                        int dp24 = (int) (24 * getResources().getDisplayMetrics().density);
                        int visibleHeight = mainScrollView.getHeight() - overlap;
                        int targetY = etDescription.getBottom() + dp24 - visibleHeight;

                        mainScrollView.smoothScrollTo(0, Math.max(0, targetY));
                    }, 100);
                }
            } else {
                mainScrollView.setPadding(0, 0, 0, 0);
            }
        });

        etAmount = view.findViewById(R.id.etAmount);
        btnOpenCalculator = view.findViewById(R.id.btnOpenCalculator);
        btnSelectCurrency = view.findViewById(R.id.btnSelectCurrency);
        tvCurrency = view.findViewById(R.id.tvCurrency);
        tvConvertedAmount = view.findViewById(R.id.tvConvertedAmount);

        layoutCategory = view.findViewById(R.id.layoutCategory);
        layoutDestAccount = view.findViewById(R.id.layoutDestAccount);
        layoutMoodSelector = view.findViewById(R.id.layoutMoodSelector);
        tvSourceAccountLabel = view.findViewById(R.id.tvSourceAccountLabel);

        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
        ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);

        viewSelectSource = view.findViewById(R.id.viewSelectSource);
        viewSelectDest = view.findViewById(R.id.viewSelectDest);
        viewSelectSource.clear("Chọn nguồn tiền...");
        viewSelectDest.clear("Chọn tài khoản nhận...");

        btnDateToday = view.findViewById(R.id.btnDateToday);
        btnDateYesterday = view.findViewById(R.id.btnDateYesterday);
        btnDateRecent = view.findViewById(R.id.btnDateRecent);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        tvTodayValue = view.findViewById(R.id.tvTodayValue);
        tvYesterdayValue = view.findViewById(R.id.tvYesterdayValue);
        tvRecentLabel = view.findViewById(R.id.tvRecentLabel);
        tvRecentValue = view.findViewById(R.id.tvRecentValue);

        setupMoodSelector(view);

        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
                        } catch (NumberFormatException ignored) { }
                    } else {
                        current = "";
                        etAmount.setText("");
                        if (tvConvertedAmount != null) tvConvertedAmount.setVisibility(View.GONE);
                    }
                    etAmount.addTextChangedListener(this);
                }
                checkSaveConditions();
            }
        });

        btnOpenCalculator.setOnClickListener(v -> {
            hideKeyboard();
            String currentValue = etAmount.getText().toString();
            PopupHelper.showCalculatorPopup(requireContext(), currentValue, result -> {
                etAmount.setText(String.format(Locale.US, "%.0f", result));
            });
        });

        btnSelectCurrency.setOnClickListener(v -> {
            hideKeyboard();
            List<String> allCurrencies = CurrencyFormatter.getSupportedCurrencies();
            if (allCurrencies == null || allCurrencies.isEmpty()) {
                allCurrencies = new ArrayList<>(Arrays.asList("VND", "USD", "EUR", "JPY"));
            }
            PopupHelper.showCurrencyFilterPopup(requireContext(), allCurrencies, selectedCurrency -> {
                currentCurrencyCode = selectedCurrency;
                tvCurrency.setText(currentCurrencyCode);
                updateConvertedAmountFromInput();
            });
        });
    }

    private void setupMoodSelector(View view) {
        androidx.recyclerview.widget.RecyclerView rvMood = view.findViewById(R.id.rvMoodSelector);
        moodAdapter = new MoodSelectorAdapter(Mood.getAllMoods(), selectedMoodId, mood -> {
            hideKeyboard();
            selectedMoodId = mood.getId();
        });
        rvMood.setAdapter(moodAdapter);
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

        if (currentMode == EntryMode.TRANSFER) {
            tvCurrency.setText(sourceCurr);
            if (selectedDestAccount != null) {
                String destCurr = selectedDestAccount.getCurrencyCode() != null ? selectedDestAccount.getCurrencyCode() : "VND";
                if (!sourceCurr.equals(destCurr)) {
                    tvConvertedAmount.setVisibility(View.VISIBLE);
                    double converted = CurrencyFormatter.previewConversion(inputAmount, sourceCurr, destCurr);
                    tvConvertedAmount.setText(String.format(Locale.US, "≈ %s %s (Ví nhận)", CurrencyFormatter.formatVND(converted), destCurr));
                } else {
                    tvConvertedAmount.setVisibility(View.GONE);
                }
            }
        } else {
            if (currentCurrencyCode.equals(sourceCurr)) {
                tvConvertedAmount.setVisibility(View.GONE);
            } else {
                tvConvertedAmount.setVisibility(View.VISIBLE);
                double converted = CurrencyFormatter.previewConversion(inputAmount, currentCurrencyCode, sourceCurr);
                tvConvertedAmount.setText(String.format(Locale.US, "≈ %s %s", CurrencyFormatter.formatVND(converted), sourceCurr));
            }
        }
    }

    private void setupComboboxes(View view) {
        view.findViewById(R.id.btnSelectCategory).setOnClickListener(v -> {
            hideKeyboard();
            showCategoryPopup();
        });
        viewSelectSource.setOnClickListener(v -> {
            hideKeyboard();
            showAccountPopup(true);
        });
        viewSelectDest.setOnClickListener(v -> {
            hideKeyboard();
            showAccountPopup(false);
        });
    }

    private void showCategoryPopup() {
        if (categoryList.isEmpty()) {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Đang tải dữ liệu hạng mục...");
            return;
        }
        PopupHelper.showCategoryFilterPopup(requireContext(), categoryList, false, this::updateSelectedCategory);
    }

    private void showAccountPopup(boolean isSource) {
        if (accountList.isEmpty()) {
            DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Đang tải danh sách tài khoản...");
            return;
        }
        String currentId = isSource ? (selectedSourceAccount != null ? selectedSourceAccount.getAccountId() : null)
                : (selectedDestAccount != null ? selectedDestAccount.getAccountId() : null);

        PopupHelper.showAccountFilterPopup(requireContext(), accountList, currentId, true, account -> {
            if (currentMode == EntryMode.TRANSFER) {
                if (isSource && selectedDestAccount != null && selectedDestAccount.getAccountId().equals(account.getAccountId())) {
                    DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Nguồn và đích không được trùng nhau!");
                    return;
                }
                if (!isSource && selectedSourceAccount != null && selectedSourceAccount.getAccountId().equals(account.getAccountId())) {
                    DialogHelper.showSimpleDialog(requireContext(), "Thông báo", "Nguồn và đích không được trùng nhau!");
                    return;
                }
            }
            if (isSource) updateSelectedSource(account);
            else updateSelectedDest(account);
        });
    }

    public void updateSelectedCategory(Category category) {
        this.selectedCategory = category;
        tvSelectedCategory.setText(category.getCategoryName());
        ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(category.getIcon())));
        ivCategoryIcon.setColorFilter(AppResourceManager.getColor(category.getColor()));
        checkSaveConditions();
    }

    public void updateSelectedSource(Account account) {
        this.selectedSourceAccount = account;
        viewSelectSource.setAccount(account, true);
        if (editTransactionId == null && editTransferId == null && account.getCurrencyCode() != null) {
            currentCurrencyCode = account.getCurrencyCode();
            tvCurrency.setText(currentCurrencyCode);
        }
        updateConvertedAmountFromInput();
        checkSaveConditions();
    }

    public void updateSelectedDest(Account account) {
        this.selectedDestAccount = account;
        viewSelectDest.setAccount(account, true);
        updateConvertedAmountFromInput();
        checkSaveConditions();
    }

    private void upgradeMockAccountsAndCategories() {
        if (selectedSourceAccount != null && !accountList.isEmpty()) {
            for (Account acc : accountList) {
                if (acc.getAccountId().equals(selectedSourceAccount.getAccountId())) {
                    this.selectedSourceAccount = acc;
                    viewSelectSource.setAccount(acc, true);
                    break;
                }
            }
        }
        if (selectedDestAccount != null && !accountList.isEmpty()) {
            for (Account acc : accountList) {
                if (acc.getAccountId().equals(selectedDestAccount.getAccountId())) {
                    this.selectedDestAccount = acc;
                    viewSelectDest.setAccount(acc, true);
                    break;
                }
            }
        }
        if (selectedCategory != null && !categoryList.isEmpty()) {
            for (Category cat : categoryList) {
                if (cat.getCategoryId().equals(selectedCategory.getCategoryId())) {
                    this.selectedCategory = cat;
                    tvSelectedCategory.setText(cat.getCategoryName());
                    ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(cat.getIcon())));
                    ivCategoryIcon.setColorFilter(AppResourceManager.getColor(cat.getColor()));
                    break;
                }
            }
        }
        checkSaveConditions();
    }

    private void observeViewModels() {
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                upgradeMockAccountsAndCategories();
            }
        });

        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
                upgradeMockAccountsAndCategories();
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) handleOperationError(error);
        });

        transferViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) handleOperationError(error);
        });

        transactionViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) handleOperationSuccess();
        });

        transferViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) handleOperationSuccess();
        });

        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), t -> {
            if (t != null && editTransactionId != null && !isDataInitialized) {
                isDataInitialized = true;
                currentMode = t.getType() == CategoryType.EXPENSE ? EntryMode.EXPENSE : EntryMode.INCOME;

                etAmount.setText(String.valueOf((long) Math.abs(t.getOriginalAmount())));
                if (t.getNote() != null) etDescription.setText(t.getNote());
                selectedMoodId = t.getMoodId();
                currentCurrencyCode = t.getCurrencyCode() != null ? t.getCurrencyCode() : "VND";
                tvCurrency.setText(currentCurrencyCode);
                restoreDateToUI(t.getDate());

                Account mockAccount = new Account(t.getAccountId(), t.getAccountName(), 0.0, "VND", t.getAccountColorId(), t.getAccountIconId(), "", true, 0, new Date(), new Date());
                this.selectedSourceAccount = mockAccount;
                viewSelectSource.setAccount(mockAccount, true);

                CategoryType intendedType = t.getType() == CategoryType.EXPENSE ? CategoryType.EXPENSE : CategoryType.INCOME;
                Category mockCategory = new Category(t.getCategoryId(), t.getCategoryName(), intendedType, "", "", 0.0, t.getCategoryColorId(), t.getCategoryIconId(), 0, new Date(), new Date());
                this.selectedCategory = mockCategory;
                tvSelectedCategory.setText(mockCategory.getCategoryName());
                ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(mockCategory.getIcon())));
                ivCategoryIcon.setColorFilter(AppResourceManager.getColor(mockCategory.getColor()));

                String[] tabs = {"Chi tiêu", "Thu nhập", "Chuyển khoản"};
                int targetTab = (currentMode == EntryMode.EXPENSE) ? 0 : 1;
                setupHeaderTabs(requireView(), tabs, targetTab, index -> {
                    hideKeyboard();
                    if (isEditing) return; // ĐÃ THÊM: Chặn chuyển Tab khi đang Edit
                    if (index == 0) currentMode = EntryMode.EXPENSE;
                    else if (index == 1) currentMode = EntryMode.INCOME;
                    else currentMode = EntryMode.TRANSFER;
                    updateUIByMode();
                });

                setTabsEnabled(false); // ĐÃ THÊM: Tái sử dụng hàm vô hiệu hóa UI thanh Tab từ BaseFragment

                updateUIByMode();
                upgradeMockAccountsAndCategories();
            }
        });

        transferViewModel.getSelectedTransfer().observe(getViewLifecycleOwner(), transfer -> {
            if (transfer != null && editTransferId != null && !isDataInitialized) {
                isDataInitialized = true;
                currentMode = EntryMode.TRANSFER;

                etAmount.setText(String.valueOf(transfer.getSourceAmount().longValue()));
                if (transfer.getDescription() != null) etDescription.setText(transfer.getDescription());
                restoreDateToUI(transfer.getDate());

                Account mockSrc = new Account(transfer.getSourceAccountId(), transfer.getSourceAccountName(), 0.0, "VND", transfer.getSourceAccountColor(), transfer.getSourceAccountIcon(), "", true, 0, new Date(), new Date());
                this.selectedSourceAccount = mockSrc;
                viewSelectSource.setAccount(mockSrc, true);

                Account mockDest = new Account(transfer.getDestinationAccountId(), transfer.getDestinationAccountName(), 0.0, "VND", transfer.getDestinationAccountColor(), transfer.getDestinationAccountIcon(), "", true, 0, new Date(), new Date());
                this.selectedDestAccount = mockDest;
                viewSelectDest.setAccount(mockDest, true);

                String[] tabs = {"Chi tiêu", "Thu nhập", "Chuyển khoản"};
                setupHeaderTabs(requireView(), tabs, 2, index -> {
                    hideKeyboard();
                    if (isEditing) return; // ĐÃ THÊM: Chặn chuyển Tab khi đang Edit
                    if (index == 0) currentMode = EntryMode.EXPENSE;
                    else if (index == 1) currentMode = EntryMode.INCOME;
                    else currentMode = EntryMode.TRANSFER;
                    updateUIByMode();
                });

                setTabsEnabled(false); // ĐÃ THÊM: Tái sử dụng hàm vô hiệu hóa UI thanh Tab từ BaseFragment

                updateUIByMode();
                upgradeMockAccountsAndCategories();
            }
        });
    }

    private void restoreDateToUI(Date date) {
        if (date == null) return;
        Date tDate = truncateTime(date);
        Calendar cal = Calendar.getInstance();
        Date today = truncateTime(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = truncateTime(cal.getTime());

        if (tDate.equals(today)) selectDateBox(0);
        else if (tDate.equals(yesterday)) selectDateBox(1);
        else {
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

        btnDateToday.setOnClickListener(v -> { hideKeyboard(); selectDateBox(0); });
        btnDateYesterday.setOnClickListener(v -> { hideKeyboard(); selectDateBox(1); });
        btnDateRecent.setOnClickListener(v -> { hideKeyboard(); selectDateBox(2); });

        btnPickDate.setOnClickListener(v -> {
            hideKeyboard();
            Calendar c = Calendar.getInstance(); c.setTime(selectedDate);
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                Calendar n = Calendar.getInstance(); n.set(y, m, d);
                box3Date = truncateTime(n.getTime());
                tvRecentValue.setText(shortDateFmt.format(box3Date));
                tvRecentLabel.setText("Đã chọn");
                selectDateBox(2);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        if (editTransactionId == null && editTransferId == null) selectDateBox(0);
    }

    private void selectDateBox(int index) {
        LinearLayout[] boxes = {btnDateToday, btnDateYesterday, btnDateRecent};
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setBackgroundResource(i == index ? R.drawable.bg_date_selected : R.drawable.bg_input_border);
        }
        Calendar cal = Calendar.getInstance();
        if (index == 0) selectedDate = truncateTime(cal.getTime());
        else if (index == 1) { cal.add(Calendar.DAY_OF_YEAR, -1); selectedDate = truncateTime(cal.getTime()); }
        else if (index == 2) selectedDate = box3Date;
    }

    private Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance(); cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void performSave() {
        if (isSaving) return;
        isSaving = true;
        setFabEnabled(false);
        pendingOperations = 0;

        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();
        double amount = Double.parseDouble(amountStr);

        if (currentMode == EntryMode.TRANSFER) {
            TransferRequest request = new TransferRequest(
                    selectedSourceAccount.getAccountId(),
                    selectedDestAccount.getAccountId(),
                    amount, DateConverter.convertDateToString(selectedDate), description
            );

            if (editTransferId != null) {
                pendingOperations = 1;
                transferViewModel.updateTransfer(editTransferId, request);
            } else {
                pendingOperations = 1;
                transferViewModel.createTransfer(request);

                if (editTransactionId != null) {
                    pendingOperations = 2;
                    transactionViewModel.deleteTransaction(editTransactionId);
                }
            }
        } else {
            CategoryType type = (currentMode == EntryMode.EXPENSE) ? CategoryType.EXPENSE : CategoryType.INCOME;
            String finalTransactionId = (editTransactionId != null) ? editTransactionId : UUID.randomUUID().toString();

            Transaction newTransaction = new Transaction(
                    finalTransactionId,
                    selectedSourceAccount.getAccountId(), selectedSourceAccount.getAccountName(),
                    selectedCategory.getCategoryId(), selectedCategory.getCategoryName(),
                    type, amount, currentCurrencyCode, 0.0, 0.0, 1.0,
                    selectedDate, description,
                    selectedCategory.getColor(), selectedCategory.getIcon(),
                    selectedSourceAccount.getColor(), selectedSourceAccount.getIcon(),
                    new ArrayList<>(), selectedMoodId, new Date()
            );

            if (editTransactionId != null) {
                pendingOperations = 1;
                transactionViewModel.updateTransaction(newTransaction);
            } else {
                pendingOperations = 1;
                transactionViewModel.addTransaction(newTransaction);

                if (editTransferId != null) {
                    pendingOperations = 2;
                    transferViewModel.deleteTransfer(editTransferId);
                }
            }
        }
    }

    private void handleOperationError(String error) {
        if (!isSaving) return;
        isSaving = false;
        DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        checkSaveConditions();
    }

    private void handleOperationSuccess() {
        if (!isSaving) return;

        pendingOperations--;

        if (pendingOperations <= 0) {
            isSaving = false;

            if (editTransactionId == null && editTransferId == null) {
                RewardHelper.showSmallReward(requireView(), "+1 SP - Thói quen tốt!");
            }
            
            DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Lưu giao dịch thành công!", () -> {
                try {
                    Navigation.findNavController(requireView()).navigateUp();

                    boolean isCrossMutated = (editTransferId != null && currentMode != EntryMode.TRANSFER) ||
                            (editTransactionId != null && currentMode == EntryMode.TRANSFER);
                    if (isCrossMutated) {
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override protected boolean shouldShowBottomNavigation() { return false; }
    @Override protected String getFabIcon() { return "gmd_check"; }
    @Override protected String getFabLabel() { return "Lưu lại"; }
    @Override protected void onFabClick() { performSave(); }
}
