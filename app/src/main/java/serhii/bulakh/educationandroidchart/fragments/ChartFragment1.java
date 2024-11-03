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

public class ChartFragment1 extends Fragment implements AddExpenseDialog.OnExpenseAddedListener, ExpenseAdapter.OnExpenseClickListener {
    private PieChart pieChart;
    private Map<String, Float> categoryTotals = new HashMap<>();
    private ArrayList<ExpenseDetail> expenseDetails = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;

    private static final String PREFS_NAME = "ChartPrefs";
    private static final String CHART_DATA_KEY = "ChartData1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart1, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter(expenseDetails, this);
        recyclerView.setAdapter(adapter);

        loadChartData();
        updateChart();
        setupPieChartListener();

        Button buttonAddExpense = view.findViewById(R.id.buttonAddExpense);
        buttonAddExpense.setOnClickListener(v -> {
            AddExpenseDialog dialog = new AddExpenseDialog(requireContext(), this);
            dialog.show();
        });

        return view;
    }

    private void setupPieChartListener() {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String category = ((PieEntry) e).getLabel();
                showDetailsForCategory(category);
            }

            @Override
            public void onNothingSelected() {
                adapter.updateList(expenseDetails);
            }
        });
    }

    private void loadChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedEntries = sharedPreferences.getStringSet(CHART_DATA_KEY, new HashSet<>());

        categoryTotals.clear();
        expenseDetails.clear();
        for (String entry : savedEntries) {
            String[] parts = entry.split(",");
            if (parts.length == 3) {
                float value = Float.parseFloat(parts[0]);
                String category = parts[1];
                String comment = parts[2];

                expenseDetails.add(new ExpenseDetail(category, value, comment));
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + value);
            }
        }
        adapter.updateList(expenseDetails);
    }

    private void saveChartData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> savedEntries = new HashSet<>();
        for (ExpenseDetail detail : expenseDetails) {
            savedEntries.add(detail.getAmount() + "," + detail.getCategory() + "," + detail.getComment());
        }

        editor.putStringSet(CHART_DATA_KEY, savedEntries);
        editor.apply();
    }

    private void updateChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Расходы");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void showDetailsForCategory(String category) {
        ArrayList<ExpenseDetail> filteredDetails = new ArrayList<>();
        for (ExpenseDetail detail : expenseDetails) {
            if (detail.getCategory().equals(category)) {
                filteredDetails.add(detail);
            }
        }
        adapter.updateList(filteredDetails);
    }

    @Override
    public void onExpenseAdded(String category, float amount, String comment) {
        ExpenseDetail newDetail = new ExpenseDetail(category, amount, comment);
        expenseDetails.add(newDetail);
        categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);

        saveChartData();
        updateChart();
    }
    @Override
    public void onExpenseAdded(String category, float amount) {
        // Передаем пустую строку, если комментарий отсутствует
        onExpenseAdded(category, amount, "");
    }


    @Override
    public void onExpenseEdit(int position, String newCategory, float newAmount, String newComment) {
        ExpenseDetail oldDetail = expenseDetails.get(position);
        categoryTotals.put(oldDetail.getCategory(), categoryTotals.get(oldDetail.getCategory()) - oldDetail.getAmount());

        oldDetail.setCategory(newCategory);
        oldDetail.setAmount(newAmount);
        oldDetail.setComment(newComment);

        categoryTotals.put(newCategory, categoryTotals.getOrDefault(newCategory, 0f) + newAmount);

        saveChartData();
        updateChart();
    }

    @Override
    public void onExpenseDelete(int position) {
        ExpenseDetail detail = expenseDetails.remove(position);
        categoryTotals.put(detail.getCategory(), categoryTotals.get(detail.getCategory()) - detail.getAmount());

        saveChartData();
        updateChart();
    }
}
