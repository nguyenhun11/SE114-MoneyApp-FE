package com.example.moneyapp.view.transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.AccountActivityItem;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class AccountActivityChildAdapter extends RecyclerView.Adapter<AccountActivityChildAdapter.ViewHolder> {

    private final List<AccountActivityItem> items;

    public AccountActivityChildAdapter(List<AccountActivityItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_activity_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountActivityItem item = items.get(position);

        if (item.getType() == AccountActivityItem.Type.TRANSFER) {
            holder.ivTypeIcon.setIcon(new IconicsDrawable(holder.itemView.getContext(), "gmd_swap_vert"));
            holder.tvTitle.setText(item.getDestinationName());
            holder.tvSubtitle.setText(item.getSourceName());
            holder.tvAmount.setText(CurrencyFormatter.formatVND(item.getAmount()));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorOnSurface));
        } else {
            holder.ivTypeIcon.setIcon(new IconicsDrawable(holder.itemView.getContext(), "gmd_edit"));
            holder.tvTitle.setText(item.getSourceName());
            holder.tvSubtitle.setText("Điều chỉnh số dư");
            
            String sign = item.getAmount() >= 0 ? "+" : "";
            holder.tvAmount.setText(sign + CurrencyFormatter.formatVND(item.getAmount()));
            
            int color = item.getAmount() >= 0 ? R.color.colorSuccess : R.color.colorDanger;
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), color));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        IconicsImageView ivTypeIcon;
        TextView tvTitle, tvSubtitle, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTypeIcon = itemView.findViewById(R.id.iv_type_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
