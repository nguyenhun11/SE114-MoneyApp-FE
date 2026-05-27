package com.example.moneyapp.ui.transaction;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.pojo.TransactionWithDetails;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.model.ListItem;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.ui.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionFragment extends BaseFragment {

    private TransactionAdapter adapter;
    private final List<Transaction> fullList = new ArrayList<>();

    private String selectedTime     = "all";
    private String selectedSource   = "Tất cả";
    private String selectedCategory = "Tất cả";

    private TextView tvTotalBalance;

    private TransactionRepository transactionRepository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionRepository = new TransactionRepository(requireActivity().getApplication());
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        setupBalanceSelector(view, getString(R.string.total_balance), "2.500.000", true);
        setupIncomeExpenseTabs(view, isExpense -> {});

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            args.putString("category",    transaction.getCategory());
            args.putString("amount",      transaction.getAmount());
            args.putString("source",      transaction.getSource());
            args.putString("date",        transaction.getDate());
            args.putString("time",        transaction.getTime());
            args.putString("description", transaction.getDescription());
            args.putString("type",        transaction.getType());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        setupTimeFilters(view);
        setupSpinners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromDatabase();
    }

    // ─── Load từ DB ──────────────────────────────────────────────────────────

    private void loadFromDatabase() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date startDate = cal.getTime();

        transactionRepository.getTransactionsWithDetails(startDate, endDate,
                new TransactionRepository.TransactionWithDetailsCallback() {
                    @Override
                    public void onSuccess(List<TransactionWithDetails> list) {
                        List<Transaction> uiList = mapToUiModel(list);
                        mainHandler.post(() -> {
                            fullList.clear();
                            fullList.addAll(uiList);
                            updateHeaderBalance(calculateBalance());
                            applyFilter();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        mainHandler.post(() ->
                                Toast.makeText(getContext(),
                                        "Lỗi tải dữ liệu: " + message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    // Chuyển TransactionWithDetails (DB) → Transaction (UI model)
    private List<Transaction> mapToUiModel(List<TransactionWithDetails> list) {
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        List<Transaction> result = new ArrayList<>();
        for (TransactionWithDetails t : list) {
            String category = t.categoryName     != null ? t.categoryName     : "Khác";
            String source   = t.sourceAccountName != null ? t.sourceAccountName : "-";
            String amount   = formatAmount((long) t.amount);
            String date     = t.date != null ? dateSdf.format(t.date) : "-";
            String time     = t.date != null ? timeSdf.format(t.date) : "-";
            String note     = t.note != null ? t.note : "";
            // Transaction entity: 1=Expense(chi), 2=Income(thu)
            String type     = t.transactionType == 2 ? "thu" : "chi";

            result.add(new Transaction(category, note, amount, time, date, type, source));
        }
        return result;
    }

    // ─── Header ──────────────────────────────────────────────────────────────

    private void updateHeaderBalance(String balance) {
        if (tvTotalBalance != null) {
            tvTotalBalance.setText(balance);
        }
    }

    private String calculateBalance() {
        long balance = 0;
        for (Transaction t : fullList) {
            try {
                long amount = Long.parseLong(
                        t.getAmount().replace(".", "").replace(",", ""));
                if ("thu".equalsIgnoreCase(t.getType())) balance += amount;
                else balance -= amount;
            } catch (NumberFormatException ignored) {}
        }
        return (balance >= 0 ? "+" : "-") + formatAmount(Math.abs(balance)) + " đ";
    }

    // ─── Group theo ngày ─────────────────────────────────────────────────────

    private List<ListItem> groupByDate(List<Transaction> transactions) {
        LinkedHashMap<String, List<Transaction>> map = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            map.computeIfAbsent(t.getDate(), k -> new ArrayList<>()).add(t);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String today     = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = sdf.format(cal.getTime());

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
            String date = entry.getKey();

            String label;
            if (date.equals(today))          label = "Hôm nay";
            else if (date.equals(yesterday)) label = "Hôm qua";
            else                             label = formatDateLabel(date);

            long totalChi = 0, totalThu = 0;
            for (Transaction t : entry.getValue()) {
                try {
                    long amount = Long.parseLong(
                            t.getAmount().replace(".", "").replace(",", ""));
                    if ("chi".equalsIgnoreCase(t.getType())) totalChi += amount;
                    else                                      totalThu += amount;
                } catch (NumberFormatException ignored) {}
            }

            String summary;
            if (totalThu > 0 && totalChi > 0)
                summary = "+" + formatAmount(totalThu) + " / -" + formatAmount(totalChi) + "đ";
            else if (totalThu > 0)
                summary = "+" + formatAmount(totalThu) + "đ";
            else
                summary = "-" + formatAmount(totalChi) + "đ";

            result.add(new ListItem(label, summary));
            for (Transaction t : entry.getValue()) {
                result.add(new ListItem(t));
            }
        }
        return result;
    }

    // ─── Filter ──────────────────────────────────────────────────────────────

    private void applyFilter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());

        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String weekStart = sdf.format(startOfWeek.getTime());

        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear  = cal.get(Calendar.YEAR);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : fullList) {
            if (!"all".equals(selectedTime)) {
                try {
                    Date date = sdf.parse(t.getDate());
                    if (date == null) continue;

                    Calendar tCal = Calendar.getInstance();
                    tCal.setTime(date);

                    if ("today".equals(selectedTime) && !t.getDate().equals(today)) continue;
                    if ("week".equals(selectedTime)  && t.getDate().compareTo(weekStart) < 0) continue;
                    if ("month".equals(selectedTime) &&
                            (tCal.get(Calendar.MONTH) != currentMonth ||
                                    tCal.get(Calendar.YEAR)  != currentYear)) continue;
                } catch (Exception ignored) {}
            }

            if (!"Tất cả".equals(selectedSource)   && !t.getSource().equals(selectedSource))   continue;
            if (!"Tất cả".equals(selectedCategory) && !t.getCategory().equals(selectedCategory)) continue;

            filtered.add(t);
        }

        if (adapter != null) {
            adapter.updateList(groupByDate(filtered));
        }
    }

    private void setupTimeFilters(View view) {
        TextView btnAll   = view.findViewById(R.id.btnFilterAll);
        TextView btnToday = view.findViewById(R.id.btnFilterToday);
        TextView btnWeek  = view.findViewById(R.id.btnFilterWeek);
        TextView btnMonth = view.findViewById(R.id.btnFilterMonth);

        if (btnAll == null || btnToday == null || btnWeek == null || btnMonth == null) return;

        List<TextView> buttons = List.of(btnAll, btnToday, btnWeek, btnMonth);
        List<String>   values  = List.of("all", "today", "week", "month");

        for (int i = 0; i < buttons.size(); i++) {
            int index = i;
            buttons.get(i).setOnClickListener(v -> {
                selectedTime = values.get(index);
                for (TextView btn : buttons) {
                    btn.setBackgroundResource(R.drawable.bg_filter_unselected);
                    btn.setTextColor(Color.parseColor("#1A1A1A"));
                }
                buttons.get(index).setBackgroundResource(R.drawable.bg_filter_selected);
                buttons.get(index).setTextColor(Color.WHITE);
                applyFilter();
            });
        }
    }

    private void setupSpinners(View view) {
        Spinner spinnerSource = view.findViewById(R.id.spinnerFilterSource);
        String[] sources = {"Tất cả", "Tiền mặt", "Momo", "Ngân hàng"};
        if (spinnerSource != null) {
            spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, sources));
            spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                    selectedSource = sources[pos];
                    applyFilter();
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        }

        Spinner spinnerCategory = view.findViewById(R.id.spinnerFilterCategory);
        String[] categories = {"Tất cả", "Ăn uống", "Sinh hoạt", "Di chuyển",
                "Mua sắm", "Giải trí", "Lương"};
        if (spinnerCategory != null) {
            spinnerCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, categories));
            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                    selectedCategory = categories[pos];
                    applyFilter();
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String formatDateLabel(String date) {
        try {
            String[] parts = date.split("-");
            return parts[2] + " tháng " + parts[1] + ", " + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    private String formatAmount(long amount) {
        StringBuilder sb = new StringBuilder(String.valueOf(amount));
        int i = sb.length() - 3;
        while (i > 0) { sb.insert(i, '.'); i -= 3; }
        return sb.toString();
    }

    // ─── FAB ─────────────────────────────────────────────────────────────────

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}