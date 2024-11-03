package serhii.bulakh.educationandroidchart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Найдем поля ввода и кнопки
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        // Регистрация нового пользователя
        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            registerUser(email, password);
        });

        // Вход в систему
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            loginUser(email, password);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Если пользователь уже авторизован, перенаправляем на MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void registerUser(String email, String password) {
        if (email.isEmpty()) {
            emailField.setError("Введите email");
            emailField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Введите пароль");
            passwordField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordField.setError("Пароль должен содержать минимум 6 символов");
            passwordField.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Регистрация успешна
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {
        if (email.isEmpty()) {
            emailField.setError("Введите email");
            emailField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Введите пароль");
            passwordField.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Авторизация успешна
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ошибка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
