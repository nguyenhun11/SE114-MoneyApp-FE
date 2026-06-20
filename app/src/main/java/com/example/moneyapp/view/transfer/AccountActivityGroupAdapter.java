package com.example.moneyapp.view.transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.AccountActivityGroup;

import java.util.List;

public class AccountActivityGroupAdapter extends RecyclerView.Adapter<AccountActivityGroupAdapter.ViewHolder> {

    private List<AccountActivityGroup> groups;

    public AccountActivityGroupAdapter(List<AccountActivityGroup> groups) {
        this.groups = groups;
    }

    public void updateList(List<AccountActivityGroup> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_activity_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountActivityGroup group = groups.get(position);
        holder.tvDateLabel.setText(group.getDateLabel());
        
        AccountActivityChildAdapter childAdapter = new AccountActivityChildAdapter(group.getActivities());
        holder.rvDailyActivities.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvDailyActivities.setAdapter(childAdapter);
        holder.rvDailyActivities.setNestedScrollingEnabled(false);

        // Add dividers between items
        if (holder.rvDailyActivities.getItemDecorationCount() == 0) {
            holder.rvDailyActivities.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(holder.itemView.getContext(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));
        }
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateLabel;
        RecyclerView rvDailyActivities;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateLabel = itemView.findViewById(R.id.tvDateLabel);
            rvDailyActivities = itemView.findViewById(R.id.rvDailyActivities);
        }
    }
}
