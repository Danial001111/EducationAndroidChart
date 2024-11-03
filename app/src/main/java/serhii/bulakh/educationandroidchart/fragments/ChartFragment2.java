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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class ChartFragment2 extends Fragment implements AddExpenseDialog.OnExpenseAddedListener {

    private PieChart pieChart;
    private ArrayList<PieEntry> entries = new ArrayList<>();
    private Map<String, String> commentsMap = new HashMap<>(); // Для хранения комментариев
    private static final String PREFS_NAME = "ChartPrefs";
    private static final String CHART_DATA_KEY = "ChartData2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);
        pieChart = view.findViewById(R.id.pieChart);

        Button buttonAddExpense = view.findViewById(R.id.buttonAddExpense);
        buttonAddExpense.setOnClickListener(v -> {
            AddExpenseDialog dialog = new AddExpenseDialog(requireContext(), this);
            dialog.show();
        });

        loadChartData();
        updateChart();
        return view;
    }

    private void loadChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedEntries = sharedPreferences.getStringSet(CHART_DATA_KEY, new HashSet<>());

        entries.clear();
        commentsMap.clear();
        for (String entry : savedEntries) {
            String[] parts = entry.split(",");
            if (parts.length == 3) {
                float value = Float.parseFloat(parts[0]);
                String label = parts[1];
                String comment = parts[2];
                entries.add(new PieEntry(value, label));
                commentsMap.put(label, comment);
            }
        }
    }

    private void saveChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> savedEntries = new HashSet<>();
        for (PieEntry entry : entries) {
            String comment = commentsMap.get(entry.getLabel());
            savedEntries.add(entry.getValue() + "," + entry.getLabel() + "," + comment);
        }

        editor.putStringSet(CHART_DATA_KEY, savedEntries);
        editor.apply();
    }

    private void updateChart() {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onExpenseAdded(String category, float amount) {

    }
    public void onExpenseAdded(String category, float amount, String comment) {
        boolean categoryExists = false;
        for (PieEntry entry : entries) {
            if (entry.getLabel().equals(category)) {
                entry.setY(entry.getValue() + amount); // Суммируем
                categoryExists = true;
                break;
            }
        }
        if (!categoryExists) {
            entries.add(new PieEntry(amount, category));
        }
        commentsMap.put(category, comment); // Добавляем или обновляем комментарий
        saveChartData();
        updateChart();

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
