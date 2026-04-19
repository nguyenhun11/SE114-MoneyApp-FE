package com.example.moneyapp.ui.transaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.adapter.TransactionAdapter;
import com.example.moneyapp.model.ListItem;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.ui.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBalanceSelector(view, "Tổng cộng", "2.500.000", true);
        setupIncomeExpenseTabs(view, isExpense -> {});

        // Data mẫu — format ngày: yyyy-MM-dd
        fullList.add(new Transaction("Ăn uống",   "Bữa sáng",      "50.000",    "07:30", "2026-04-19", "chi", "Tiền mặt"));
        fullList.add(new Transaction("Di chuyển", "Grab đi làm",   "30.000",    "08:15", "2026-04-19", "chi", "Momo"));
        fullList.add(new Transaction("Lương",     "Lương tháng 4", "5.000.000", "09:00", "2026-04-19", "thu", "Tiền mặt"));
        fullList.add(new Transaction("Mua sắm",   "Siêu thị",      "200.000",   "12:30", "2026-04-18", "chi", "Momo"));
        fullList.add(new Transaction("Giải trí",  "Xem phim",      "80.000",    "19:00", "2026-04-18", "chi", "Tiền mặt"));

        // Setup RecyclerView - Fixed: Changed R.id.recyclerViewTransactions to R.id.rvTransactions
        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(groupByDate(fullList), transaction -> {
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

    // Nhóm list giao dịch theo ngày → list gồm header + item
    private List<ListItem> groupByDate(List<Transaction> transactions) {
        LinkedHashMap<String, List<Transaction>> map = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            List<Transaction> group = map.get(t.getDate());
            if (group == null) {
                group = new ArrayList<>();
                map.put(t.getDate(), group);
            }
            group.add(t);
        }

        // Ngày hôm nay và hôm qua để so sánh
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String today     = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = sdf.format(cal.getTime());

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
            String date = entry.getKey();

            // Label ngày
            String label;
            if (date.equals(today))          label = "Hôm nay";
            else if (date.equals(yesterday)) label = "Hôm qua";
            else                             label = formatDateLabel(date);

            // Tính tổng chi trong ngày
            long totalChi = 0, totalThu = 0;
            for (Transaction t : entry.getValue()) {
                try {
                    long amount = Long.parseLong(t.getAmount().replace(".", "").replace(",", ""));
                    if (t.getType().equals("chi")) totalChi += amount;
                    else                           totalThu += amount;
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

    // "2026-04-18" → "18 tháng 4, 2026"
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

    private void setupTimeFilters(View view) {
        TextView btnAll   = view.findViewById(R.id.btnFilterAll);
        TextView btnToday = view.findViewById(R.id.btnFilterToday);
        TextView btnWeek  = view.findViewById(R.id.btnFilterWeek);
        TextView btnMonth = view.findViewById(R.id.btnFilterMonth);

        List<TextView> buttons = new ArrayList<>();
        buttons.add(btnAll); buttons.add(btnToday);
        buttons.add(btnWeek); buttons.add(btnMonth);

        List<String> values = new ArrayList<>();
        values.add("all"); values.add("today");
        values.add("week"); values.add("month");

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
        String[] categories = {"Tất cả", "Ăn uống", "Sinh hoạt", "Di chuyển", "Mua sắm", "Giải trí", "Lương"};
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

    private void applyFilter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());

        // Tính đầu tuần (thứ 2)
        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String weekStart = sdf.format(startOfWeek.getTime());

        // Tháng và năm hiện tại
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear  = cal.get(Calendar.YEAR);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : fullList) {
            // Lọc thời gian
            if (!selectedTime.equals("all")) {
                try {
                    Calendar tCal = Calendar.getInstance();
                    tCal.setTime(sdf.parse(t.getDate()));

                    if (selectedTime.equals("today") && !t.getDate().equals(today)) continue;
                    if (selectedTime.equals("week") && t.getDate().compareTo(weekStart) < 0) continue;
                    if (selectedTime.equals("month") &&
                            (tCal.get(Calendar.MONTH) != currentMonth ||
                                    tCal.get(Calendar.YEAR)  != currentYear)) continue;
                } catch (Exception ignored) {}
            }

            if (!selectedSource.equals("Tất cả") && !t.getSource().equals(selectedSource)) continue;
            if (!selectedCategory.equals("Tất cả") && !t.getCategory().equals(selectedCategory)) continue;
            filtered.add(t);
        }

        adapter.updateList(groupByDate(filtered));
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}
