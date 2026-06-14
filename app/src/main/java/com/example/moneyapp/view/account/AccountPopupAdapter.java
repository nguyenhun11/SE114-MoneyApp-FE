package com.example.moneyapp.view.account;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter; // Nhớ import thư viện format tiền
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AccountPopupAdapter extends RecyclerView.Adapter<AccountPopupAdapter.ViewHolder> {
    private List<Account> list;
    private int selectedPosition = -1;
    private String currentAccountId;
    private OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(Account account);
    }

    public AccountPopupAdapter(List<Account> list,
                               String currentAccountId,
                               OnAccountClickListener listener) {
        this.list = list;
        this.listener = listener;
        this.currentAccountId = currentAccountId;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAccountId().equals(currentAccountId)) {
                this.selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Account account = list.get(position);

        holder.tvName.setText(account.getAccountName());
        holder.tvBalance.setText(CurrencyFormatter.formatVND(account.getBalance()));

        holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, account.getIcon()));
        int colorValue = AppResourceManager.getColor(account.getColor());
        holder.viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));

        if (selectedPosition == position) {
            holder.cardBg.setStrokeWidth(3);
            holder.cardBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBgLight));
            holder.cardBg.setStrokeColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.cardBg.setStrokeWidth(0);
            holder.cardBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorSurface));
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
        TextView tvBalance;
        ImageView ivIcon;
        View viewColorCircle;
        MaterialCardView cardBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBg = itemView.findViewById(R.id.cardBg);
            viewColorCircle = itemView.findViewById(R.id.viewColorCircle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvName = itemView.findViewById(R.id.tvAccountName);
            tvBalance = itemView.findViewById(R.id.tvAccountBalance);
        }
    }
}