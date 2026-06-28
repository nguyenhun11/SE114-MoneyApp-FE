package com.example.moneyapp.view.transaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.local.entity.PendingTransaction;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.data.repository.PendingTransactionRepository;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.view.BaseFragment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Fragment hiển thị danh sách các Giao dịch chờ duyệt (Pending Transactions) được bóc tách từ thông báo.
 * Hỗ trợ người dùng:
 * 1. Xem danh sách chi tiết các giao dịch nháp.
 * 2. Lưu nhanh (Approve) giao dịch bằng tài khoản và hạng mục mặc định chỉ với 1 chạm.
 * 3. Xóa bỏ (Ignore) giao dịch nháp không muốn lưu.
 * 4. Click trực tiếp vào giao dịch để chuyển sang màn hình TransactionEntryFragment chỉnh sửa thủ công.
 */
public class PendingTransactionsFragment extends BaseFragment {

    private RecyclerView rvPending;
    private LinearLayout layoutEmpty;
    private PendingTransactionRepository pendingRepository;
    private PendingAdapter adapter;
    private final List<PendingTransaction> pendingList = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cài đặt tiêu đề Header và nút Quay lại (Back)
        setupHeader(view, "Giao dịch chờ duyệt", true);

        // Ánh xạ các thành phần UI
        rvPending = view.findViewById(R.id.rv_pending_transactions);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);

        // Khởi tạo repository dữ liệu nháp
        pendingRepository = new PendingTransactionRepository(requireActivity().getApplication());

        // Thiết lập RecyclerView và Adapter
        adapter = new PendingAdapter(pendingList, new OnPendingActionListener() {
            @Override
            public void onItemClick(PendingTransaction pendingTx) {
                // Khi người dùng nhấn vào item: Chuyển sang màn hình TransactionEntryFragment để chỉnh sửa chi tiết
                Bundle bundle = new Bundle();
                bundle.putDouble("amount", pendingTx.getAmount());
                bundle.putInt("type", pendingTx.getTransactionType());
                bundle.putString("note", pendingTx.getNote());
                bundle.putString("accountName", pendingTx.getAccountName());
                bundle.putString("pendingTxId", pendingTx.getId()); // Gửi ID nháp để xóa sau khi lưu thành công

                Navigation.findNavController(requireView()).navigate(R.id.transactionEntryFragment, bundle);
            }

            @Override
            public void onApprove(PendingTransaction pendingTx) {
                // Thao tác Lưu nhanh (Approve) trực tiếp từ danh sách
                approveQuickSave(pendingTx);
            }

            @Override
            public void onDelete(PendingTransaction pendingTx) {
                // Thao tác Xóa/Bỏ qua giao dịch nháp
                deleteDraft(pendingTx);
            }
        });
        rvPending.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Luôn luôn load lại danh sách khi Fragment hiển thị lại
        loadPendingTransactions();
    }

    /**
     * Tải danh sách giao dịch nháp từ Database và hiển thị lên RecyclerView.
     */
    private void loadPendingTransactions() {
        if (pendingRepository == null) return;

        pendingRepository.getAllPendingTransactions(new PendingTransactionRepository.PendingTransactionCallback() {
            @Override
            public void onSuccess(List<PendingTransaction> list) {
                mainHandler.post(() -> {
                    if (getView() == null) return;
                    pendingList.clear();
                    pendingList.addAll(list);
                    adapter.notifyDataSetChanged();

                    // Điều khiển ẩn/hiện Layout Empty State
                    if (list.isEmpty()) {
                        rvPending.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvPending.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onSuccess(PendingTransaction pendingTransaction) {}

            @Override
            public void onError(String message) {
                mainHandler.post(() -> 
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Thực hiện lưu nhanh giao dịch nháp vào Database chính thức qua API.
     */
    private void approveQuickSave(PendingTransaction pendingTx) {
        String userId = PreferenceManager.getInstance(requireContext()).getUserID();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Đang xử lý lưu nhanh...", Toast.LENGTH_SHORT).show();

        AccountRepository accountRepo = new AccountRepository(requireContext());
        CategoryRepository categoryRepo = new CategoryRepository(requireContext());
        TransactionRepository transactionRepo = new TransactionRepository(requireContext());

        accountRepo.getAllAccounts(new AccountRepository.AccountCallback<List<Account>>() {
            @Override
            public void onSuccess(List<Account> accounts) {
                if (accounts.isEmpty()) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Không tìm thấy ví nào để lưu!", Toast.LENGTH_SHORT).show());
                    return;
                }

                Account matchedAccount = null;
                for (Account acc : accounts) {
                    if (acc.getAccountName().toLowerCase().contains(pendingTx.getAccountName().toLowerCase())) {
                        matchedAccount = acc;
                        break;
                    }
                }
                if (matchedAccount == null) {
                    matchedAccount = accounts.get(0);
                }

                final Account selectedAccount = matchedAccount;
                CategoryType categoryType = (pendingTx.getTransactionType() == 1) ? CategoryType.EXPENSE : CategoryType.INCOME;

                CategoryRepository.CategoryCallback<List<Category>> categoryCallback = new CategoryRepository.CategoryCallback<List<Category>>() {
                    @Override
                    public void onSuccess(List<Category> categories) {
                        if (categories.isEmpty()) {
                            mainHandler.post(() -> Toast.makeText(getContext(), "Không tìm thấy hạng mục phù hợp!", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        Category matchedCategory = null;
                        for (Category cat : categories) {
                            if (cat.getCategoryName().equals("Khác") || cat.getCategoryName().equals("Khac")) {
                                matchedCategory = cat;
                                break;
                            }
                        }
                        if (matchedCategory == null) {
                            matchedCategory = categories.get(0);
                        }

                        Transaction officialTx = new Transaction(
                                UUID.randomUUID().toString(),
                                selectedAccount.getAccountId(),
                                selectedAccount.getAccountName(),
                                matchedCategory.getCategoryId(),
                                matchedCategory.getCategoryName(),
                                categoryType,
                                pendingTx.getAmount(),
                                selectedAccount.getCurrencyCode() != null ? selectedAccount.getCurrencyCode() : "VND",
                                0.0, 0.0, 1.0,
                                new Date(),
                                pendingTx.getNote(),
                                matchedCategory.getColor(),
                                matchedCategory.getIcon(),
                                selectedAccount.getColor(),
                                selectedAccount.getIcon(),
                                new ArrayList<>(),
                                0,
                                new Date()
                        );

                        transactionRepo.createTransaction(officialTx, new TransactionRepository.TransactionCallback<Transaction>() {
                            @Override
                            public void onSuccess(Transaction result) {
                                pendingRepository.deletePendingTransaction(pendingTx, new PendingTransactionRepository.ActionCallback() {
                                    @Override
                                    public void onSuccess() {
                                        mainHandler.post(() -> {
                                            Toast.makeText(getContext(), "Đã lưu nhanh giao dịch thành công!", Toast.LENGTH_SHORT).show();
                                            loadPendingTransactions();
                                        });
                                    }

                                    @Override
                                    public void onError(String message) {
                                        mainHandler.post(() -> Toast.makeText(getContext(), "Lỗi khi xóa bản nháp: " + message, Toast.LENGTH_SHORT).show());
                                    }
                                });
                            }

                            @Override
                            public void onError(String message) {
                                mainHandler.post(() -> Toast.makeText(getContext(), "Lỗi khi tạo giao dịch: " + message, Toast.LENGTH_SHORT).show());
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        mainHandler.post(() -> Toast.makeText(getContext(), "Lỗi tải hạng mục: " + message, Toast.LENGTH_SHORT).show());
                    }
                };

                if (categoryType == CategoryType.EXPENSE) {
                    categoryRepo.getExpenseCategories(categoryCallback);
                } else {
                    categoryRepo.getIncomeCategories(categoryCallback);
                }
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> Toast.makeText(getContext(), "Lỗi tải ví: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Bỏ qua và xóa giao dịch nháp.
     */
    private void deleteDraft(PendingTransaction pendingTx) {
        pendingRepository.deletePendingTransaction(pendingTx, new PendingTransactionRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "Đã xóa giao dịch nháp.", Toast.LENGTH_SHORT).show();
                    loadPendingTransactions();
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> 
                    Toast.makeText(getContext(), "Lỗi khi xóa: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // --- Định nghĩa Interface cho các nút hành động trong Adapter ---
    public interface OnPendingActionListener {
        void onItemClick(PendingTransaction pendingTx);
        void onApprove(PendingTransaction pendingTx);
        void onDelete(PendingTransaction pendingTx);
    }

    // --- LỚP ADAPTER RECYCLERVIEW NỘI BỘ (PendingAdapter) ---
    private static class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.PendingViewHolder> {

        private final List<PendingTransaction> list;
        private final OnPendingActionListener actionListener;

        public PendingAdapter(List<PendingTransaction> list, OnPendingActionListener actionListener) {
            this.list = list;
            this.actionListener = actionListener;
        }

        @NonNull
        @Override
        public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_transaction, parent, false);
            return new PendingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PendingViewHolder holder, int position) {
            PendingTransaction pendingTx = list.get(position);
            holder.bind(pendingTx, actionListener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        // Lớp ViewHolder ánh xạ các view trong file item_pending_transaction.xml
        static class PendingViewHolder extends RecyclerView.ViewHolder {

            private final View viewIndicator;
            private final TextView tvAccount;
            private final TextView tvNote;
            private final TextView tvDate;
            private final TextView tvAmount;
            private final ImageButton btnDelete;
            private final ImageButton btnApprove;

            public PendingViewHolder(@NonNull View itemView) {
                super(itemView);
                viewIndicator = itemView.findViewById(R.id.view_type_indicator);
                tvAccount = itemView.findViewById(R.id.tv_pending_account);
                tvNote = itemView.findViewById(R.id.tv_pending_note);
                tvDate = itemView.findViewById(R.id.tv_pending_date);
                tvAmount = itemView.findViewById(R.id.tv_pending_amount);
                btnDelete = itemView.findViewById(R.id.btn_pending_delete);
                btnApprove = itemView.findViewById(R.id.btn_pending_approve);
            }

            public void bind(PendingTransaction pendingTx, OnPendingActionListener listener) {
                // 1. Hiển thị Ví nguồn nhận diện
                tvAccount.setText(pendingTx.getAccountName());
                // 2. Hiển thị ghi chú nội dung
                tvNote.setText(pendingTx.getNote());

                // 3. Định dạng hiển thị ngày giờ nhận
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvDate.setText(pendingTx.getCreatedAt() != null ? sdf.format(pendingTx.getCreatedAt()) : "");

                // 4. Định dạng tiền tệ hiển thị và màu sắc chỉ báo
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String amountStr = currencyFormat.format(pendingTx.getAmount());

                if (pendingTx.getTransactionType() == 1) { // Chi tiêu (Expense)
                    tvAmount.setText("-" + amountStr);
                    int color = androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.colorExpense);
                    tvAmount.setTextColor(color); // Màu đỏ chi tiêu
                    viewIndicator.setBackgroundColor(color);
                } else { // Thu nhập (Income)
                    tvAmount.setText("+" + amountStr);
                    int color = androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.colorIncome);
                    tvAmount.setTextColor(color); // Màu xanh lá thu nhập
                    viewIndicator.setBackgroundColor(color);
                }

                // 5. Đăng ký sự kiện click
                itemView.setOnClickListener(v -> listener.onItemClick(pendingTx));
                btnApprove.setOnClickListener(v -> listener.onApprove(pendingTx));
                btnDelete.setOnClickListener(v -> listener.onDelete(pendingTx));
            }
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false; // Ẩn thanh BottomNavigation khi xem danh sách duyệt để tối ưu diện tích
    }
}

