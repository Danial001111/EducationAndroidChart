package serhii.bulakh.educationandroidchart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import serhii.bulakh.educationandroidchart.R;
import serhii.bulakh.educationandroidchart.models.Goal;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private final List<Goal> goalsList;
    private final OnGoalDeleteListener deleteListener;

    public GoalsAdapter(List<Goal> goalsList, OnGoalDeleteListener deleteListener) {
        this.goalsList = goalsList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalsList.get(position);
        holder.bind(goal, position, goalsList, this, deleteListener);
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {

        private final TextView goalNameTextView;
        private final ProgressBar goalTargetProgressBar;
        private final TextView goalRemainingTextView;
        private final EditText addAmountInput;
        private final Button addAmountButton;
        private final Button deleteGoalButton;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalNameTextView = itemView.findViewById(R.id.goal_name_text);
            goalTargetProgressBar = itemView.findViewById(R.id.goal_progress_bar);
            goalRemainingTextView = itemView.findViewById(R.id.goal_progress_text);
            addAmountInput = itemView.findViewById(R.id.add_amount_input);
            addAmountButton = itemView.findViewById(R.id.add_amount_button);
            deleteGoalButton = itemView.findViewById(R.id.delete_goal_button);
        }

        public void bind(Goal goal, int position, List<Goal> goalsList, GoalsAdapter adapter, OnGoalDeleteListener deleteListener) {
            goalNameTextView.setText(goal.getName());
            goalTargetProgressBar.setMax((int) goal.getTargetAmount());
            goalTargetProgressBar.setProgress((int) goal.getCurrentAmount());
            goalRemainingTextView.setText("Осталось: " + goal.getRemainingAmount());

            addAmountButton.setOnClickListener(v -> {
                String amountText = addAmountInput.getText().toString();
                if (!amountText.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountText);
                        goal.addAmount(amount);
                        goalTargetProgressBar.setProgress((int) goal.getCurrentAmount());
                        goalRemainingTextView.setText("Осталось: " + goal.getRemainingAmount());
                        addAmountInput.setText("");
                    } catch (NumberFormatException e) {
                        Toast.makeText(itemView.getContext(), "Неверный формат суммы", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            deleteGoalButton.setOnClickListener(v -> {
                deleteListener.onGoalDelete(goal);
                goalsList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, goalsList.size());
            });
        }
    }

    public interface OnGoalDeleteListener {
        void onGoalDelete(Goal goal);
    }
}
