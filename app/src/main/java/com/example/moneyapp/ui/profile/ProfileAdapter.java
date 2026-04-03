package com.example.moneyapp.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneyapp.R;
import com.example.moneyapp.data.local.model.ProfileOption;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<ProfileOption> options;
    private OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(ProfileOption option);
    }

    public ProfileAdapter(List<ProfileOption> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_option, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileOption option = options.get(position);
        holder.tvTitle.setText(option.getTitle());
        holder.ivIcon.setImageResource(option.getIconRes());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOptionClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_option_icon);
            tvTitle = itemView.findViewById(R.id.tv_option_title);
        }
    }
}
