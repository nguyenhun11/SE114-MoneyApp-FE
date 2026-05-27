package com.example.moneyapp.ui.transaction;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.moneyapp.model.ListItem;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public TransactionAdapter(List<ListItem> items, OnItemClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    public void updateList(List<ListItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder cho header ngày
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateLabel, tvDateSummary;
        HeaderViewHolder(View v) {
            super(v);
            tvDateLabel   = v.findViewById(R.id.tvDateLabel);
            tvDateSummary = v.findViewById(R.id.tvDateSummary);
        }
    }

    // ViewHolder cho item giao dịch
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvTime, tvSource;
        View viewCategoryIcon;
        TransactionViewHolder(View v) {
            super(v);
            tvTitle          = v.findViewById(R.id.tvTitle);
            tvAmount         = v.findViewById(R.id.tvAmount);
            tvTime           = v.findViewById(R.id.tvTime);
            tvSource         = v.findViewById(R.id.tvdetail);
            viewCategoryIcon = v.findViewById(R.id.viewCategoryIcon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ListItem.TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.list_item_transaction, parent, false);
            return new TransactionViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);

        if (item.getType() == ListItem.TYPE_HEADER) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.tvDateLabel.setText(item.getDateLabel());
            h.tvDateSummary.setText(item.getDateSummary());

        } else {
            TransactionViewHolder h = (TransactionViewHolder) holder;
            Transaction t = item.getTransaction();

            h.tvTitle.setText(t.getCategory());
            h.tvSource.setText(t.getSource());
            h.tvTime.setText(t.getTime());

            // Số tiền + màu
            if (t.getType().equals("chi")) {
                h.tvAmount.setText("- " + t.getAmount() + "đ");
                h.tvAmount.setTextColor(Color.parseColor("#E8435A"));
            } else {
                h.tvAmount.setText("+ " + t.getAmount() + "đ");
                h.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
            }

            // Màu icon
            String color;
            switch (t.getCategory()) {
                case "Sinh hoạt": color = "#7B61FF"; break;
                case "Ăn uống":   color = "#FFA726"; break;
                case "Di chuyển": color = "#29B6F6"; break;
                case "Mua sắm":   color = "#EC407A"; break;
                case "Lương":     color = "#4CAF50"; break;
                default:           color = "#9E9E9E"; break;
            }
            h.viewCategoryIcon.getBackground().mutate()
                    .setTint(Color.parseColor(color));

            h.itemView.setOnClickListener(v -> listener.onItemClick(t));
        }
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }
}