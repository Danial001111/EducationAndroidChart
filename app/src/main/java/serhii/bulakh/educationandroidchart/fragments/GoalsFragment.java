package serhii.bulakh.educationandroidchart.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import serhii.bulakh.educationandroidchart.R;

public class GoalsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Допустим, это место для отображения текущей валюты
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String currency = sharedPreferences.getString("Currency", "USD");

        // Вы можете показать информацию о валюте или цели, связанной с валютой, здесь
        return view;
    }
}
