package com.example.coinly;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.coinly.db.Database;
import com.example.coinly.db.Transaction;

public class TransactionDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        setupToolbar();
        displayTransactionDetails();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void displayTransactionDetails() {
        String id = getIntent().getStringExtra("id");

        TextView amountText = findViewById(R.id.transactionAmount);
        TextView titleText = findViewById(R.id.transactionTitle);
        TextView dateText = findViewById(R.id.transactionDate);
        TextView senderText = findViewById(R.id.senderText);
        TextView receiverText = findViewById(R.id.receiverText);
        TextView referenceText = findViewById(R.id.referenceText);

        Transaction.getFrom(id, new Database.Data<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                amountText.setText(data.formattedAmount());
                amountText.setTextColor(data.amountColor());
                titleText.setText(data.name);
                dateText.setText(Util.dateFormatter(data.date));
                senderText.setText(data.senderId);
                receiverText.setText(data.receiveId);
                referenceText.setText(data.id);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TransactionDetails", "Tried to get transaction", e);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}