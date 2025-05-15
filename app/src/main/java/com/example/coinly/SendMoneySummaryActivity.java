package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;

public class SendMoneySummaryActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView userNameText, transactionNumberText, transactionAmountText, totalAmountText, referenceNumberText, transactionDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_summary);

        // Intent
        String transactionId = getIntent().getStringExtra("id");

        // Bind views
        backArrow = findViewById(R.id.backArrow);
        userNameText = findViewById(R.id.userName);
        transactionNumberText = findViewById(R.id.transactionNumber);
        transactionAmountText = findViewById(R.id.transactionAmount);
        totalAmountText = findViewById(R.id.totalAmount);
        referenceNumberText = findViewById(R.id.referenceNumber);
        transactionDateText = findViewById(R.id.transactionDate);

        // Back to main
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneySummaryActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Transaction.getFrom(transactionId, new Database.Data<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                transactionAmountText.setText(Util.amountFormatter(data.amount));
                totalAmountText.setText(Util.amountFormatter(data.amount));
                referenceNumberText.setText("Ref number: " + transactionId);
                transactionDateText.setText(Util.dateFormatter(data.date));

                User.get(data.receiveId, User.Details.class, new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        userNameText.setText(data.fullName.formatted());
                        transactionNumberText.setText(data.phoneNumber);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("SendMoneySummary", "Tried to get user's details", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SendMoneySummary", "Tried to get transaction", e);
            }
        });
    }
}
