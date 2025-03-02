package serhii.bulakh.educationandroidchart.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import serhii.bulakh.educationandroidchart.AddExpenseDialog;
import serhii.bulakh.educationandroidchart.R;
import serhii.bulakh.educationandroidchart.adapters.ExpenseAdapter;
import serhii.bulakh.educationandroidchart.models.ExpenseDetail;

public class ChartFragment2 extends Fragment implements AddExpenseDialog.OnExpenseAddedListener {

    private PieChart pieChart;
    private ArrayList<ExpenseDetail> expenses = new ArrayList<>();
    private ExpenseAdapter expenseAdapter;
    private static final String PREFS_NAME = "ChartPrefs";
    private static final String CHART_DATA_KEY = "ChartData2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        Button buttonAddExpense = view.findViewById(R.id.buttonAddExpense);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        expenseAdapter = new ExpenseAdapter(expenses, new ExpenseAdapter.OnExpenseClickListener() {
            @Override
            public void onExpenseEdit(int position, String category, float amount, String comment) {
                ExpenseDetail expense = expenses.get(position);
                expense.setCategory(category);
                expense.setAmount(amount);
                expense.setComment(comment);
                saveChartData();
                updateChart();
                expenseAdapter.notifyItemChanged(position);
            }

            @Override
            public void onExpenseDelete(int position) {
                expenses.remove(position);
                saveChartData();
                updateChart();
                expenseAdapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(expenseAdapter);

        buttonAddExpense.setOnClickListener(v -> {
            AddExpenseDialog dialog = new AddExpenseDialog(requireContext(), this);
            dialog.show();
        });

        loadChartData();
        updateChart();
        setupPieChartListener();
        return view;
    }

    private void setupPieChartListener() {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String selectedCategory = ((PieEntry) e).getLabel();
                showExpensesForCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected() {
                expenseAdapter.updateList(expenses); // Отображение всех расходов, если ничего не выбрано
            }
        });
    }

    private void showExpensesForCategory(String category) {
        ArrayList<ExpenseDetail> filteredExpenses = new ArrayList<>();
        for (ExpenseDetail expense : expenses) {
            if (expense.getCategory().equals(category)) {
                filteredExpenses.add(expense);
            }
        }
        expenseAdapter.updateList(filteredExpenses);
    }

    private void loadChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedEntries = sharedPreferences.getStringSet(CHART_DATA_KEY, new HashSet<>());

        expenses.clear();
        for (String entry : savedEntries) {
            String[] parts = entry.split(",");
            if (parts.length == 3) {
                float value = Float.parseFloat(parts[0]);
                String label = parts[1];
                String comment = parts[2];
                expenses.add(new ExpenseDetail(label, value, comment));
            }
        }
        expenseAdapter.notifyDataSetChanged();
    }

    private void saveChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> savedEntries = new HashSet<>();
        for (ExpenseDetail expense : expenses) {
            savedEntries.add(expense.getAmount() + "," + expense.getCategory() + "," + expense.getComment());
        }

        editor.putStringSet(CHART_DATA_KEY, savedEntries);
        editor.apply();
    }

    private void updateChart() {
        Map<String, Float> categorySums = new HashMap<>();
        for (ExpenseDetail expense : expenses) {
            String category = expense.getCategory();
            float amount = expense.getAmount();
            categorySums.put(category, categorySums.getOrDefault(category, 0f) + amount);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categorySums.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onExpenseAdded(String category, float amount) {
        // Передаем пустую строку, если комментарий отсутствует
        onExpenseAdded(category, amount, "");
    }

    @Override
    public void onExpenseAdded(String category, float amount, String comment) {
        expenses.add(new ExpenseDetail(category, amount, comment));
        saveChartData();
        updateChart();
        expenseAdapter.notifyDataSetChanged();

        Snackbar.make(getView(), getString(R.string.added) + ": " + category + " - " + formatPrice(amount), Snackbar.LENGTH_SHORT).show();
    }

    private String formatPrice(float amount) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String currency = sharedPreferences.getString("Currency", "USD");

        switch (currency) {
            case "EUR":
                return String.format(Locale.getDefault(), "€%.2f", amount);
            case "UAH":
                return String.format(Locale.getDefault(), "₴%.2f", amount);
            default:
                return String.format(Locale.getDefault(), "$%.2f", amount);
        }
    }
}
