package serhii.bulakh.educationandroidchart.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        // Инициализируем SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Найти TextView для отображения email
        TextView userEmailTextView = view.findViewById(R.id.user_email);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmailTextView.setText(email);
        }

        // Настроить Spinner для выбора языка
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.languages_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        // Установить текущий язык в Spinner
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

        // Настроить Spinner для выбора валюты
        Spinner currencySpinner = view.findViewById(R.id.currency_spinner);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        // Установить текущую валюту в Spinner
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

        // Обработчик кнопки выхода из аккаунта
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void changeLanguage(String language) {
        String currentLanguage = sharedPreferences.getString("Language", "en");
        if (!currentLanguage.equals(language)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Language", language);
            editor.apply();

            LocaleHelper.setLocale(requireContext(), language);
            requireActivity().recreate();
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
