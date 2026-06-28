package com.example.moneyapp.view.quest;

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
import com.example.moneyapp.data.remote.response.QuestResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

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
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(quest.getTitle());
        holder.tvDesc.setText(quest.getDescription());
        holder.tvProgress.setText(quest.getCurrentProgress() + "/" + quest.getTarget());

        int progressPercent = (int) ((float) quest.getCurrentProgress() / quest.getTarget() * 100);
        holder.pbProgress.setProgress(Math.min(progressPercent, 100));

        if (quest.isClaimed()) {
            holder.btnClaim.setText("Đã nhận");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setAlpha(1.0f);

            holder.btnClaim.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarningBgLight)));
            holder.btnClaim.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarning)));
            holder.btnClaim.setStrokeWidth(3);
            holder.btnClaim.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));

            holder.pbProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.colorWarning));

        } else if (quest.isCompleted()) {
            holder.btnClaim.setText("Nhận thưởng");
            holder.btnClaim.setEnabled(true);
            holder.btnClaim.setAlpha(1.0f);

            holder.btnClaim.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarning)));
            holder.btnClaim.setStrokeWidth(0);
            holder.btnClaim.setTextColor(ContextCompat.getColor(context, R.color.white));

            holder.pbProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.colorWarning));

        } else {
            holder.btnClaim.setText("Chưa xong");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setAlpha(1.0f); // Không làm mờ, dùng màu để thể hiện trạng thái

            holder.btnClaim.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNeutralBgLight)));
            holder.btnClaim.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNeutral)));
            holder.btnClaim.setStrokeWidth(3); // Tương đương viền 1dp
            holder.btnClaim.setTextColor(ContextCompat.getColor(context, R.color.colorNeutral));

            holder.pbProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.colorSuccess));
        }

        holder.btnClaim.setOnClickListener(v -> {
            if (listener != null) listener.onClaim(quest);
        });
    }

    @Override
    public int getItemCount() { return quests == null ? 0 : quests.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvProgress;
        LinearProgressIndicator pbProgress;
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