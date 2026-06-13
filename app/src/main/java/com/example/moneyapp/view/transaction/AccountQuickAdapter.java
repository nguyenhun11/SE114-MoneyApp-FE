package com.example.moneyapp.view.transaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class AccountQuickAdapter extends RecyclerView.Adapter<AccountQuickAdapter.ViewHolder> {
    private List<Account> list;
    private int selectedPosition = -1;
    private OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(Account account);
    }

    public AccountQuickAdapter(List<Account> list, OnAccountClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Account account = list.get(position);

        holder.tvName.setText(account.getAccountName());
        holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, account.getIcon()));
        
        int colorValue = AppResourceManager.getColor(account.getColor());
        holder.viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));

        if (selectedPosition == position) {
            holder.cardBg.setStrokeWidth(3);
            holder.cardBg.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        } else {
            holder.cardBg.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (oldPos != -1) notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onAccountClick(list.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public Account getSelectedAccount() {
        if (selectedPosition != -1 && selectedPosition < list.size()) return list.get(selectedPosition);
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        IconicsImageView ivIcon;
        View viewColorCircle;
        com.google.android.material.card.MaterialCardView cardBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
            cardBg = itemView.findViewById(R.id.card_item_bg);
        }
    }
}
