package serhii.bulakh.educationandroidchart;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;

public class AddExpenseDialog extends Dialog {

    private EditText categoryInput, amountInput, commentInput;
    private final OnExpenseAddedListener listener;

    public AddExpenseDialog(@NonNull Context context, OnExpenseAddedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_expense);

        categoryInput = findViewById(R.id.categoryInput);
        amountInput = findViewById(R.id.amountInput);
        commentInput = findViewById(R.id.commentInput);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            String category = categoryInput.getText().toString();
            float amount = Float.parseFloat(amountInput.getText().toString());
            String comment = commentInput.getText().toString();
            listener.onExpenseAdded(category, amount, comment);
            dismiss();
        });
    }

    public interface OnExpenseAddedListener {
        void onExpenseAdded(String category, float amount);

        void onExpenseAdded(String category, float amount, String comment);
    }
}
