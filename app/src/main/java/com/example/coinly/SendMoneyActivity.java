package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SendMoneyActivity extends AppCompatActivity {

    private ImageView backArrow, arrowButton;
    private EditText editTextNumber, editTextAmount;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        // Bind views
        backArrow = findViewById(R.id.backArrow);
        arrowButton = findViewById(R.id.arrowButton);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextAmount = findViewById(R.id.editTextAmount);
        nextButton = findViewById(R.id.nextButton);

        // Disable Next button initially
        nextButton.setEnabled(false);

        // Placeholder back navigation
        backArrow.setOnClickListener(v -> {
            // TODO: Replace with actual destination
            Intent intent = new Intent(SendMoneyActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        // Navigate to MyQRActivity
        arrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneyActivity.this, MyQRActivity.class);
            startActivity(intent);
        });

        // Watch input fields to enable "Next"
        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable "Next" if both fields are filled
                String number = editTextNumber.getText().toString().trim();
                String amount = editTextAmount.getText().toString().trim();
                nextButton.setEnabled(!number.isEmpty() && !amount.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no-op
            }
        };

        editTextNumber.addTextChangedListener(formWatcher);
        editTextAmount.addTextChangedListener(formWatcher);

        // Handle "Next" button click
        nextButton.setOnClickListener(v -> {
            String number = editTextNumber.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();

            if (number.isEmpty() || amount.isEmpty()) {
                Toast.makeText(SendMoneyActivity.this, "Please enter both number and amount", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SendMoneyActivity.this, SendMoneyConfirmActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("amount", amount);
                startActivity(intent);
            }
        });

    }

    // Placeholder validation function
    // private boolean validateNumberWithDatabase(String number) {
    //     // TODO: Query your Firestore or Realtime DB here
    //     return true;
    // }
}
