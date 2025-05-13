package com.example.coinly;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TransactionDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_AMOUNT = "amount";
    public static final String EXTRA_REFERENCE = "reference";
    public static final String EXTRA_SENDER = "sender";
    public static final String EXTRA_RECEIVER = "receiver";

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
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        double amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 0);
        String reference = getIntent().getStringExtra(EXTRA_REFERENCE);
        String sender = getIntent().getStringExtra(EXTRA_SENDER);
        String receiver = getIntent().getStringExtra(EXTRA_RECEIVER);

        TextView amountText = findViewById(R.id.transactionAmount);
        TextView titleText = findViewById(R.id.transactionTitle);
        TextView dateText = findViewById(R.id.transactionDate);
        TextView senderText = findViewById(R.id.senderText);
        TextView receiverText = findViewById(R.id.receiverText);
        TextView referenceText = findViewById(R.id.referenceText);

        // Show actual amount including negative values
        String formattedAmount = (amount >= 0 ? "+ " : "- ") + "Php " + String.format("%.2f", Math.abs(amount));
        amountText.setText(formattedAmount);
        amountText.setTextColor(amount >= 0 ? 0xFF4CAF50 : 0xFFE91E63);

        titleText.setText(title);
        dateText.setText(date);
        senderText.setText(sender);
        receiverText.setText(receiver);
        referenceText.setText(reference);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}