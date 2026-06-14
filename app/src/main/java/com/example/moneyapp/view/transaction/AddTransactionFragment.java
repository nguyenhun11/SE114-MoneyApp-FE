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
import com.example.moneyapp.view.BaseFragment;
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

public class AddTransactionFragment extends BaseFragment {

    private CategoryType transactionType = CategoryType.EXPENSE;
    private Date selectedDate;
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory = null;
    private Account selectedAccount = null;

    // region Views
    private EditText etAmount, etDescription;
    private TextView tvSelectedCategory, tvSelectedSource;
    private IconicsImageView ivCategoryIcon, ivSourceIcon;
    private LinearLayout btnDateToday, btnDateYesterday, btnDateRecent, btnPickDate;
    private TextView tvTodayValue, tvYesterdayValue, tvRecentValue, tvRecentLabel;
    private Date box3Date; // Ngày cho ô số 3
    // endregion

    // region  ViewModels
    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;
    // endregion
    private final SimpleDateFormat shortDateFmt = new SimpleDateFormat("dd/MM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupHeader(view, "Giao dịch mới", true);

        initViews(view);
        setupDatePickers();
        setupComboboxes(view);

        setupIncomeExpenseTabs(view, isExpense -> {
            transactionType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            updateAmountColor(isExpense);

            selectedCategory = null;
            tvSelectedCategory.setText("Chọn hạng mục...");
            ivCategoryIcon.setIcon(new com.mikepenz.iconics.IconicsDrawable(requireContext(), "gmd_category"));
            ivCategoryIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
            categoryViewModel.loadCategories(transactionType);
        });

        observeViewModels();
        accountViewModel.loadAccounts();
        categoryViewModel.loadCategories(CategoryType.EXPENSE);
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etDescription = view.findViewById(R.id.etDescription);

        // Combobox Views
        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);
        ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);
        tvSelectedSource = view.findViewById(R.id.tvSelectedSource);
        ivSourceIcon = view.findViewById(R.id.ivSourceIcon);

        // Date Views
        btnDateToday = view.findViewById(R.id.btnDateToday);
        btnDateYesterday = view.findViewById(R.id.btnDateYesterday);
        btnDateRecent = view.findViewById(R.id.btnDateRecent);
        btnPickDate = view.findViewById(R.id.btnPickDate);

        tvTodayValue = view.findViewById(R.id.tvTodayValue);
        tvYesterdayValue = view.findViewById(R.id.tvYesterdayValue);
        tvRecentLabel = view.findViewById(R.id.tvRecentLabel);
        tvRecentValue = view.findViewById(R.id.tvRecentValue);

        updateAmountColor(true); // Mặc định màu đỏ

        etAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
                            etAmount.setSelection(formatted.length()); // Đẩy con trỏ chuột về cuối
                        } catch (NumberFormatException e) {
                            // Do nothing
                        }
                    } else {
                        current = "";
                        etAmount.setText("");
                    }

                    // Gắn lắng nghe trở lại
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
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                if (selectedAccount == null && !accountList.isEmpty()) {
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
                Toast.makeText(getContext(), "Thêm giao dịch thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePickers() {
        Calendar cal = Calendar.getInstance();
        selectedDate = cal.getTime();

        tvTodayValue.setText(shortDateFmt.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_YEAR, -1);
        tvYesterdayValue.setText(shortDateFmt.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_YEAR, -1); // Lùi thêm 1 ngày nữa (Hôm kia)
        box3Date = cal.getTime(); // Gán mặc định ô 3 là Hôm kia
        tvRecentValue.setText(shortDateFmt.format(box3Date));
        tvRecentLabel.setText("Gần đây");

        // 2. Bắt sự kiện Click cho 3 ô đầu
        btnDateToday.setOnClickListener(v -> selectDateBox(0));
        btnDateYesterday.setOnClickListener(v -> selectDateBox(1));
        btnDateRecent.setOnClickListener(v -> selectDateBox(2));

        // 3. Xử lý nút Chọn Ngày (Ô số 4)
        btnPickDate.setOnClickListener(v -> {
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(selectedDate);

            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                // Người dùng vừa chọn ngày xong
                Calendar newCal = Calendar.getInstance();
                newCal.set(year, month, day);

                // Cập nhật giá trị và giao diện cho ô số 3
                box3Date = newCal.getTime();
                tvRecentValue.setText(shortDateFmt.format(box3Date));
                tvRecentLabel.setText("Đã chọn"); // Đổi nhãn cho rõ nghĩa

                // Kích hoạt ô số 3
                selectDateBox(2);
            }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Mặc định lúc mới vào màn hình sẽ chọn Hôm nay
        selectDateBox(0);
    }

    private void selectDateBox(int index) {
        // QUAN TRỌNG: Chỉ đưa 3 ô đầu tiên vào mảng để xử lý Highlight
        LinearLayout[] boxes = {btnDateToday, btnDateYesterday, btnDateRecent};

        int colorUnselectedMain = ContextCompat.getColor(requireContext(), R.color.colorOnSurface);
        int colorUnselectedSub = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.colorOnPrimary);

        for (int i = 0; i < boxes.length; i++) {
            LinearLayout box = boxes[i];
            boolean isSelected = (i == index);

            // Đổi màu nền của khối
            box.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_input_border);

            // Quét qua tất cả các view con để đổi màu chữ/icon
            for (int j = 0; j < box.getChildCount(); j++) {
                View child = box.getChildAt(j);
                if (child instanceof TextView) {
                    TextView tv = (TextView) child;
                    if (isSelected) {
                        tv.setTextColor(colorSelected);
                    } else {
                        // Nhận diện Text chính (ngày) và Text phụ (nhãn)
                        if (tv.getId() == R.id.tvTodayValue ||
                                tv.getId() == R.id.tvYesterdayValue ||
                                tv.getId() == R.id.tvRecentValue) {
                            tv.setTextColor(colorUnselectedMain);
                        } else {
                            tv.setTextColor(colorUnselectedSub);
                        }
                    }
                }
            }
        }

        // Cập nhật giá trị ngày đang được chọn để lưu DataBase
        Calendar cal = Calendar.getInstance();
        if (index == 0) {
            selectedDate = cal.getTime();
        } else if (index == 1) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            selectedDate = cal.getTime();
        } else if (index == 2) {
            // Lấy trực tiếp từ biến lưu trữ của ô số 3
            selectedDate = box3Date;
        }
    }

    private void setupComboboxes(View view) {
        view.findViewById(R.id.btnSelectCategory).setOnClickListener(v -> {
            showCategoryPopup();
        });

        view.findViewById(R.id.btnSelectSource).setOnClickListener(v -> {
            showAccountPopup();
        });
    }

    private void showCategoryPopup() {
        if (categoryList.isEmpty()) {
            // Thay vì chỉ báo lỗi rồi đứng im, ta sẽ chủ động gọi API thêm lần nữa để "cứu vãn"
            Toast.makeText(getContext(), "Đang tải dữ liệu, vui lòng thử lại sau giây lát...", Toast.LENGTH_SHORT).show();
            categoryViewModel.loadCategories(transactionType);
            return;
        }

        // Gọi Popup xịn xò từ PopupHelper
        com.example.moneyapp.utils.PopupHelper.showCategoryFilterPopup(requireContext(), categoryList, selectedCat -> {
            // Cập nhật lên UI ngay lập tức
            updateSelectedCategory(selectedCat);
        });
    }

    private void showAccountPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải nguồn tiền, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            accountViewModel.loadAccounts();
            return;
        }

        // Gọi Popup xịn xò từ PopupHelper
        com.example.moneyapp.utils.PopupHelper.showAccountFilterPopup(requireContext(), accountList, selectedAcc -> {
            // Cập nhật lên UI ngay lập tức
            updateSelectedAccount(selectedAcc);
        });
    }

    // Hàm gọi khi User đã chọn xong từ Popup
    public void updateSelectedCategory(Category category) {
        this.selectedCategory = category;
        tvSelectedCategory.setText(category.getCategoryName());

        // Lấy tên và màu từ AppResourceManager
        int color = AppResourceManager.getColor(category.getColor());
        String iconName = AppResourceManager.getIconName(category.getIcon());

        // Khởi tạo IconicsDrawable và set vào View
        ivCategoryIcon.setIcon(new IconicsDrawable(requireContext(), iconName));
        ivCategoryIcon.setColorFilter(color);

        requireView().clearFocus();
    }

    public void updateSelectedAccount(Account account) {
        this.selectedAccount = account;
        tvSelectedSource.setText(account.getAccountName());

        int color = AppResourceManager.getColor(account.getColor());
        String iconName = AppResourceManager.getIconName(account.getIcon());

        ivSourceIcon.setIcon(new IconicsDrawable(requireContext(), iconName));
        ivSourceIcon.setColorFilter(color);

        requireView().clearFocus();
    }

    // ==========================================
    // LOGIC LƯU GIAO DỊCH
    // ==========================================
    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();

        if (amountStr.isEmpty() || selectedCategory == null || selectedAccount == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ số tiền, hạng mục và nguồn tiền!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amountValue = Double.parseDouble(amountStr);
            if (transactionType == CategoryType.EXPENSE && amountValue > 0)
                amountValue = -amountValue;
            else if (transactionType == CategoryType.INCOME && amountValue < 0)
                amountValue = Math.abs(amountValue);

            Transaction newTransaction = new Transaction(
                    UUID.randomUUID().toString(),
                    selectedAccount.getAccountId(), selectedAccount.getAccountName(),
                    selectedCategory.getCategoryId(), selectedCategory.getCategoryName(),
                    transactionType, amountValue, selectedDate, description,
                    selectedCategory.getColor(), selectedCategory.getIcon(),
                    selectedAccount.getColor(), selectedAccount.getIcon(),
                    new ArrayList<>(), new Date()
            );

            transactionViewModel.addTransaction(newTransaction);
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