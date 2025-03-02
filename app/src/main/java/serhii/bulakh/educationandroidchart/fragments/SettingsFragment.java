package serhii.bulakh.educationandroidchart.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import serhii.bulakh.educationandroidchart.LocaleHelper;
import serhii.bulakh.educationandroidchart.LoginActivity;
import serhii.bulakh.educationandroidchart.R;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Инициализация SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Отображение email пользователя
        TextView userEmailTextView = view.findViewById(R.id.user_email);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmailTextView.setText(email);
        }

        // Настройка Spinner для выбора языка
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.languages_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        // Установка текущего языка
        String currentLanguage = sharedPreferences.getString("Language", "en");
        setSpinnerToValue(languageSpinner, currentLanguage);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = (String) parentView.getItemAtPosition(position);
                changeLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Настройка Spinner для выбора валюты
        Spinner currencySpinner = view.findViewById(R.id.currency_spinner);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        // Установка текущей валюты
        String currentCurrency = sharedPreferences.getString("Currency", "USD");
        setSpinnerToValue(currencySpinner, currentCurrency);

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCurrency = (String) parentView.getItemAtPosition(position);
                saveCurrency(selectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Кнопка выхода из аккаунта
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }


    private void changeLanguage(String language) {
        String currentLanguage = sharedPreferences.getString("Language", "en");
        String code = getLocaleByLanguage(language);

        // Если текущий язык не совпадает с выбранным
        if (!currentLanguage.equals(code)) {
            // Сохраняем новый язык в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Language", code);
            editor.apply();

            Locale locale = new Locale(code);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            requireActivity().recreate();
            Log.d("SettingsScreen", "Language changed to " + code);

        }
    }

    private String getLocaleByLanguage(String language) {
        switch (language) {
            case "Русский":
                return "ru";
            case "Украинский":
                return "uk";
            case "English":
                return "en";
            default:
                return "en"; // По умолчанию английский
        }
    }


    private void saveCurrency(String currency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Currency", currency);
        editor.apply();
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
