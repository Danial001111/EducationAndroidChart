package serhii.bulakh.educationandroidchart.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import serhii.bulakh.educationandroidchart.R;
import serhii.bulakh.educationandroidchart.adapters.GoalsAdapter;
import serhii.bulakh.educationandroidchart.models.Goal;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment implements GoalsAdapter.OnGoalDeleteListener {

    private SharedPreferences sharedPreferences;
    private List<Goal> goalsList;
    private GoalsAdapter goalsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        sharedPreferences = requireContext().getSharedPreferences("GoalsPrefs", Context.MODE_PRIVATE);
        goalsList = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.goals_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goalsAdapter = new GoalsAdapter(goalsList, this);
        recyclerView.setAdapter(goalsAdapter);

        EditText goalNameInput = view.findViewById(R.id.goal_name_input);
        EditText goalAmountInput = view.findViewById(R.id.goal_amount_input);
        Button addGoalButton = view.findViewById(R.id.add_goal_button);

        addGoalButton.setOnClickListener(v -> {
            String goalName = goalNameInput.getText().toString();
            String goalAmountStr = goalAmountInput.getText().toString();
            if (!goalName.isEmpty() && !goalAmountStr.isEmpty()) {
                int goalAmount = Integer.parseInt(goalAmountStr);
                Goal goal = new Goal(goalName, goalAmount, 0);
                goalsList.add(goal);
                goalsAdapter.notifyItemInserted(goalsList.size() - 1);
                saveGoal(goalName, goalAmount, 0);
            } else {
                Toast.makeText(getContext(), "Введите название и сумму цели", Toast.LENGTH_SHORT).show();
            }
        });

        loadGoals();

        return view;
    }

    private void saveGoal(String goalName, int goalAmount, int progress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(goalName + "_amount", goalAmount);
        editor.putInt(goalName + "_progress", progress);
        editor.apply();
    }

    private void loadGoals() {
        sharedPreferences.getAll().forEach((key, value) -> {
            if (key.endsWith("_amount")) {
                String goalName = key.replace("_amount", "");
                int goalAmount = (int) value;
                int progress = sharedPreferences.getInt(goalName + "_progress", 0);
                Goal goal = new Goal(goalName, goalAmount, progress);
                goalsList.add(goal);
            }
        });
        goalsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGoalDelete(Goal goal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(goal.getName() + "_amount");
        editor.remove(goal.getName() + "_progress");
        editor.apply();
        Toast.makeText(getContext(), "Цель удалена", Toast.LENGTH_SHORT).show();
    }
}
