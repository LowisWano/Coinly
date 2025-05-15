package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

public class SendMoneyActivity extends AppCompatActivity {

    private ImageView backArrow, arrowButton;
    private EditText editTextNumber, editTextAmount;
    private Button nextButton;

    private float currentBalance = 0f;
    private String currentUserPhoneNumber = "";

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

        // Get current user ID
        String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");

        // Set header values
        User.get(userId, User.Details.class, new Database.Data<User.Details>() {
            @Override
            public void onSuccess(User.Details data) {
                TextView userName = findViewById(R.id.userName);
                TextView phoneNumber = findViewById(R.id.phoneNumber);

                userName.setText(data.fullName.first + " " + data.fullName.middleInitial + " " + data.fullName.last);
                currentUserPhoneNumber = data.phoneNumber;
                phoneNumber.setText(data.phoneNumber);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SendMoneyActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });

        // Get balance
        User.get(userId, User.Wallet.class, new Database.Data<User.Wallet>() {
            @Override
            public void onSuccess(User.Wallet data) {
                currentBalance = (float) data.balance;
                TextView enterAmount = findViewById(R.id.enterAmount);
                enterAmount.setText(String.format("Enter Amount (%.2f)", currentBalance));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SendMoneyActivity.this, "Failed to load wallet info", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneyActivity.this, WalletActivity.class);
            startActivity(intent);
            finish();
        });

        arrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneyActivity.this, MyQRActivity.class);
            startActivity(intent);
        });

        // Enable "Next" only when both fields are not empty
        TextWatcher formWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = !editTextNumber.getText().toString().trim().isEmpty()
                        && !editTextAmount.getText().toString().trim().isEmpty();
                nextButton.setEnabled(enable);
            }
        };

        editTextNumber.addTextChangedListener(formWatcher);
        editTextAmount.addTextChangedListener(formWatcher);

        // "Next" Button Logic
        nextButton.setOnClickListener(v -> {
            String number = editTextNumber.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();

            if (number.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount;
            try {
                amount = Float.parseFloat(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > currentBalance) {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                return;
            }

            if (number.equals(currentUserPhoneNumber)) {
                Toast.makeText(this, "You cannot send money to yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass to confirmation page (database logic will be done there)
            Intent intent = new Intent(SendMoneyActivity.this, SendMoneyConfirmActivity.class);
            intent.putExtra("number", number);
            intent.putExtra("amount", amountStr);
            intent.putExtra("senderId", userId);
            startActivity(intent);
        });
    }
}
