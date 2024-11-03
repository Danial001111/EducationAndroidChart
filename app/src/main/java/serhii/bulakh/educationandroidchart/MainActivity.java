package serhii.bulakh.educationandroidchart;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import serhii.bulakh.educationandroidchart.fragments.ChartFragment1;
import serhii.bulakh.educationandroidchart.fragments.ChartFragment2;
import serhii.bulakh.educationandroidchart.fragments.GoalsFragment;
import serhii.bulakh.educationandroidchart.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация BottomNavigationView
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        // Загрузка первого фрагмента при запуске
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChartFragment1())
                    .commit();
        }
    }

    // Слушатель навигации
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_chart1:
                        selectedFragment = new ChartFragment1();
                        break;
                    case R.id.navigation_chart2:
                        selectedFragment = new ChartFragment2();
                        break;
                    case R.id.navigation_goals:
                        selectedFragment = new GoalsFragment();
                        break;
                    case R.id.navigation_settings:
                        selectedFragment = new SettingsFragment();
                        break;
                }

                // Замена текущего фрагмента
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            };
}
