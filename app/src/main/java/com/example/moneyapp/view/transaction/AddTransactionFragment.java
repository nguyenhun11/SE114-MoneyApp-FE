package com.example.moneyapp.view.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.transaction.AccountQuickAdapter;
import com.example.moneyapp.view.transaction.CategoryQuickAdapter;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;

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

    // Danh sách dữ liệu cho Adapter
    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    // Biến lưu trữ người dùng đang chọn cái nào
    private Category selectedCategory = null;
    private Account selectedAccount = null;

    // View
    private EditText etAmount, etDescription;
    private TextView tvDateToday, tvDateYesterday;
    private RecyclerView rvCategoryQuick, rvSourceQuick;

    // Adapter
    private CategoryQuickAdapter categoryAdapter;
    private AccountQuickAdapter accountAdapter;

    // ViewModels
    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo ViewModels
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupHeader(view, "Giao dịch mới", true);

        // 2. Ánh xạ View và Cài đặt RecyclerView/Adapter
        initViewsAndAdapters(view);
        setupDatePickers(view);
        setupButtons(view);

        // 3. Tab Thu nhập / Chi tiêu
        setupIncomeExpenseTabs(view, isExpense -> {
            transactionType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            updateAmountColor(isExpense);

            // Tải lại danh sách Category khi đổi Tab
            selectedCategory = null;
            categoryViewModel.loadCategories(transactionType);
        });

        // 4. Lắng nghe dữ liệu và Tải API/DB
        observeViewModels();
        accountViewModel.loadAccounts();
        categoryViewModel.loadCategories(CategoryType.EXPENSE); // Mặc định load chi tiêu
    }

    private void initViewsAndAdapters(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etDescription = view.findViewById(R.id.etDescription);
        rvCategoryQuick = view.findViewById(R.id.rvCategoryQuick);
        rvSourceQuick = view.findViewById(R.id.rvSourceQuick);
        tvDateToday = view.findViewById(R.id.tvDateToday);
        tvDateYesterday = view.findViewById(R.id.tvDateYesterday);

        updateAmountColor(true); // Mặc định màu đỏ

        // Setup LayoutManager ngang (Tránh lỗi Inflate XML)
        rvCategoryQuick.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSourceQuick.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Khởi tạo Adapter Hạng mục
        categoryAdapter = new CategoryQuickAdapter(categoryList, category -> {
            this.selectedCategory = category;
        });
        rvCategoryQuick.setAdapter(categoryAdapter);

        // Khởi tạo Adapter Nguồn tiền
        accountAdapter = new AccountQuickAdapter(accountList, account -> {
            this.selectedAccount = account;
        });
        rvSourceQuick.setAdapter(accountAdapter);
    }

    private void updateAmountColor(boolean isExpense) {
        if (isExpense) {
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
        } else {
            etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
        }
    }

    private void observeViewModels() {
        // Lắng nghe dữ liệu Tài khoản
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                accountAdapter.notifyDataSetChanged();

                // (Tuỳ chọn) Tự động chọn tài khoản đầu tiên nếu chưa chọn
                if (selectedAccount == null && !accountList.isEmpty()) {
                    selectedAccount = accountList.get(0);
                    // Có thể cần hàm trong adapter để set selected item = 0 nếu muốn UI sáng lên
                }
            }
        });

        // Lắng nghe dữ liệu Hạng mục
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
            }
        });

        // Lắng nghe trạng thái lưu Transaction
        transactionViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Thêm giao dịch thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp(); // Đóng màn hình
            }
        });

        // Lắng nghe lỗi chung
        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePickers(View view) {
        selectedDate = new Date(); // Mặc định hôm nay
        ImageView btnPickDate = view.findViewById(R.id.btnPickDate);
        Calendar calendar = Calendar.getInstance();

        tvDateToday.setOnClickListener(v -> {
            selectedDate = new Date();
            updateDateChips(true);
        });

        tvDateYesterday.setOnClickListener(v -> {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            selectedDate = calendar.getTime();
            updateDateChips(false);
        });

        btnPickDate.setOnClickListener(v -> {
            calendar.setTime(selectedDate);
            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                calendar.set(year, month, day);
                selectedDate = calendar.getTime();

                // Tắt highlight chip
                tvDateToday.setBackgroundResource(R.drawable.bg_chip_unselected);
                tvDateToday.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
                tvDateYesterday.setBackgroundResource(R.drawable.bg_chip_unselected);
                tvDateYesterday.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateDateChips(boolean isTodaySelected) {
        if (isTodaySelected) {
            tvDateToday.setBackgroundResource(R.drawable.bg_chip_selected);
            tvDateToday.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            tvDateYesterday.setBackgroundResource(R.drawable.bg_chip_unselected);
            tvDateYesterday.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
        } else {
            tvDateYesterday.setBackgroundResource(R.drawable.bg_chip_selected);
            tvDateYesterday.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            tvDateToday.setBackgroundResource(R.drawable.bg_chip_unselected);
            tvDateToday.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
        }
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.btnMoreCategory).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở toàn bộ Hạng mục (Đang phát triển)", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnMoreSource).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở toàn bộ Nguồn tiền (Đang phát triển)", Toast.LENGTH_SHORT).show();
        });
    }

    // ==========================================
    // LOGIC LƯU GIAO DỊCH
    // ==========================================
    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // 1. Kiểm tra đầu vào hợp lệ
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(getContext(), "Vui lòng chọn Hạng mục", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAccount == null) {
            Toast.makeText(getContext(), "Vui lòng chọn Nguồn tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amountValue = Double.parseDouble(amountStr);

            // Xử lý số âm/dương theo tab
            if (transactionType == CategoryType.EXPENSE && amountValue > 0) {
                amountValue = -amountValue;
            } else if (transactionType == CategoryType.INCOME && amountValue < 0) {
                amountValue = Math.abs(amountValue);
            }

            // 2. Khởi tạo đối tượng Transaction mới khớp với Constructor
            Transaction newTransaction = new Transaction(
                    UUID.randomUUID().toString(),            // transactionId
                    selectedAccount.getAccountId(),          // accountId
                    selectedAccount.getAccountName(),        // accountName
                    selectedCategory.getCategoryId(),        // categoryId
                    selectedCategory.getCategoryName(),      // categoryName
                    transactionType,                         // type
                    amountValue,                             // amount
                    selectedDate,                            // date
                    description,                             // note
                    selectedCategory.getColor(),           // categoryColorId
                    selectedCategory.getIcon(),            // categoryIconId
                    selectedAccount.getColor(),            // accountColorId
                    selectedAccount.getIcon(),             // accountIconId
                    new ArrayList<>(),                       // imageUrls (Truyền list rỗng vì UI chưa có tính năng thêm ảnh)
                    new Date()                               // createdAt
            );

            // 3. Đẩy vào ViewModel để lưu
            transactionViewModel.addTransaction(newTransaction);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }

    @Override
    protected int getFabIcon() { return R.drawable.ic_check_white; }

    @Override
    protected void onFabClick() {
        saveTransaction();
    }
}