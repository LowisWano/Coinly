package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneyActivity.this, WalletActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigate to MyQRActivity
        arrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneyActivity.this, MyQRActivity.class);
            startActivity(intent);
            finish();
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

        String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");

        // Handle "Next" button click
        nextButton.setOnClickListener(v -> {
            String number = editTextNumber.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();

            if (number.isEmpty() || amount.isEmpty()) {
                Toast.makeText(SendMoneyActivity.this, "Please enter both number and amount", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(SendMoneyActivity.this, SendMoneyConfirmActivity.class);
            intent.putExtra("number", number);
            intent.putExtra("amount", amount);
            intent.putExtra("senderId", userId);
            startActivity(intent);
            finish();
        });

        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>(){

                    @Override
                    public void onSuccess(User.Details data) {
                        TextView userName, phoneNumber;
                        userName = findViewById(R.id.userName);
                        phoneNumber = findViewById(R.id.phoneNumber);

                        userName.setText(data.fullName.formatted());
                        phoneNumber.setText(data.phoneNumber);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("SendMoney", "Tried to send money", e);
                    }
                }
        );

        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>(){

                    @Override
                    public void onSuccess(User.Wallet data) {
                        TextView enterAmount = findViewById(R.id.enterAmount);

                        enterAmount.setText(String.format("Enter Amount (Php %s)", Util.amountFormatter(data.balance)));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Error", "Error", e);
                    }
                }
        );

    }


}
