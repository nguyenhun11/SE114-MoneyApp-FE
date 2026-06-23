package com.example.moneyapp.view.quest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.QuestResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHolder> {

    private List<QuestResponse> quests;
    private OnQuestClaimListener listener;

    public interface OnQuestClaimListener {
        void onClaim(QuestResponse quest);
    }

    public QuestAdapter(List<QuestResponse> quests, OnQuestClaimListener listener) {
        this.quests = quests;
        this.listener = listener;
    }

    public void updateData(List<QuestResponse> newQuests) {
        this.quests = newQuests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quest, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestResponse quest = quests.get(position);
        holder.tvTitle.setText(quest.getTitle());
        holder.tvDesc.setText(quest.getDescription());
        holder.tvProgress.setText(quest.getCurrentProgress() + "/" + quest.getTarget());
        
        int progressPercent = (int) ((float) quest.getCurrentProgress() / quest.getTarget() * 100);
        holder.pbProgress.setProgress(Math.min(progressPercent, 100));

        if (quest.isClaimed()) {
            holder.btnClaim.setText("Đã nhận");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setAlpha(0.5f);
        } else if (quest.isCompleted()) {
            holder.btnClaim.setText("Nhận thưởng");
            holder.btnClaim.setEnabled(true);
            holder.btnClaim.setAlpha(1.0f);
        } else {
            holder.btnClaim.setText("Chưa xong");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setAlpha(0.3f);
        }

        holder.btnClaim.setOnClickListener(v -> {
            if (listener != null) listener.onClaim(quest);
        });
    }

    @Override
    public int getItemCount() { return quests == null ? 0 : quests.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvProgress;
        ProgressBar pbProgress;
        MaterialButton btnClaim;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuestTitle);
            tvDesc = itemView.findViewById(R.id.tvQuestDesc);
            tvProgress = itemView.findViewById(R.id.tvQuestProgressText);
            pbProgress = itemView.findViewById(R.id.pbQuestProgress);
            btnClaim = itemView.findViewById(R.id.btnClaimReward);
        }
    }
}
