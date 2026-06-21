package com.example.moneyapp.view.goal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goals = new ArrayList<>();
    private final OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public GoalAdapter(OnGoalClickListener listener) {
        this.listener = listener;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        holder.bind(goals.get(position));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout flIconContainer;
        private final IconicsImageView ivIcon;
        private final TextView tvName;
        private final TextView tvDeadline;
        private final TextView tvPercent;
        private final LinearProgressIndicator progressBar;
        private final TextView tvAmount;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_goal_icon);
            tvName = itemView.findViewById(R.id.tv_goal_name);
            tvDeadline = itemView.findViewById(R.id.tv_goal_deadline);
            tvPercent = itemView.findViewById(R.id.tv_goal_percent);
            progressBar = itemView.findViewById(R.id.pb_goal_progress);
            tvAmount = itemView.findViewById(R.id.tv_goal_amount);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onGoalClick(goals.get(pos));
                }
            });
        }

        public void bind(Goal goal) {
            Context context = itemView.getContext();
            tvName.setText(goal.getName());
            
            String displayDate = DateConverter.formatToDisplay(goal.getDeadline());
            tvDeadline.setText(context.getString(R.string.goal_deadline_label, displayDate));
            
            int percent = goal.getProgressPercent();
            tvPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
            progressBar.setProgress(percent);

            String amountText = String.format("%s / %s", 
                CurrencyFormatter.formatVND(goal.getCurrentAmount()), 
                CurrencyFormatter.formatVND(goal.getTargetAmount()));
            tvAmount.setText(amountText);

            int color = AppResourceManager.getColor(goal.getColorId());
            flIconContainer.setBackgroundTintList(ColorStateList.valueOf(color));
            
            ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, goal.getIconId()));

            progressBar.setIndicatorColor(color);
            tvPercent.setTextColor(color);
        }
    }
}
