package com.example.moneyapp.view.profile;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.BadgeResponse;
import com.example.moneyapp.utils.RewardHelper;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private List<BadgeResponse> badges;

    public BadgeAdapter(List<BadgeResponse> badges) {
        this.badges = badges;
    }

    public void updateData(List<BadgeResponse> newBadges) {
        this.badges = newBadges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BadgeResponse badge = badges.get(position);
        holder.tvName.setText(badge.getName());
        
        String iconKey = badge.isUnlocked() ? 
                (badge.getIconKey() != null ? badge.getIconKey() : "gmd_stars") : "gmd_lock";
        int iconColor = badge.isUnlocked() ? Color.parseColor("#FFC107") : Color.parseColor("#BDBDBD");
        int bgColor = badge.isUnlocked() ? Color.parseColor("#FFF8E1") : Color.parseColor("#F5F5F5");

        holder.ivIcon.setIcon(new IconicsDrawable(holder.itemView.getContext(), iconKey));
        holder.ivIcon.setColorFilter(iconColor);
        holder.ivIcon.setBackgroundTintList(ColorStateList.valueOf(bgColor));

        if (badge.isUnlocked()) {
            holder.tvName.setTextColor(holder.itemView.getContext().getColor(R.color.colorOnSurface));
        } else {
            holder.tvName.setTextColor(holder.itemView.getContext().getColor(R.color.colorOnSurfaceVariant));
        }

        holder.itemView.setOnClickListener(v -> {
            RewardHelper.showBadgeDetail(v.getContext(), badge);
        });
    }

    @Override
    public int getItemCount() { return badges == null ? 0 : badges.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        IconicsImageView ivIcon;
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivBadgeIcon);
            tvName = itemView.findViewById(R.id.tvBadgeName);
        }
    }
}
