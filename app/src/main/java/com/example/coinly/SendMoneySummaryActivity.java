package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class SendMoneySummaryActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView userNameText, transactionNumberText, transactionAmountText, totalAmountText, referenceNumberText, transactionDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_summary);

        // Intent
        String transactionId = getIntent().getStringExtra("transactionId");

        // Bind views
        backArrow = findViewById(R.id.backArrow);
        userNameText = findViewById(R.id.userName);
        transactionNumberText = findViewById(R.id.transactionNumber);
        transactionAmountText = findViewById(R.id.transactionAmount);
        totalAmountText = findViewById(R.id.totalAmount);
        referenceNumberText = findViewById(R.id.referenceNumber);
        transactionDateText = findViewById(R.id.transactionDate);

        // Back to main
        backArrow.setOnClickListener(v -> finish());

        // Fetch transaction data
        FirebaseFirestore.getInstance()
                .collection("transactions")
                .document(transactionId)
                .get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load transaction: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void populateFields(DocumentSnapshot txnSnapshot) {
        if (!txnSnapshot.exists()) {
            Toast.makeText(this, "Transaction not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipientId = txnSnapshot.getString("receiveId");
        float amount = Float.parseFloat(String.valueOf(txnSnapshot.get("amount")));
        long refNum = Long.parseLong(txnSnapshot.getId());
        Calendar date = Calendar.getInstance();
        date.setTime(txnSnapshot.getDate("date"));

        // Format values
        String formattedAmount = String.format(Locale.getDefault(), "Php %.2f", amount);
        String formattedDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date.getTime());

        // Set transaction values
        transactionAmountText.setText(formattedAmount);
        totalAmountText.setText(formattedAmount);
        referenceNumberText.setText("Ref number: " + refNum);
        transactionDateText.setText(formattedDate);

        // Get recipient info
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(recipientId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (!userSnapshot.exists()) return;

                    Map<String, Object> details = (Map<String, Object>) userSnapshot.get("details");
                    Map<String, Object> fullName = (Map<String, Object>) details.get("fullName");

                    String first = (String) fullName.get("first");
                    String middle = (String) fullName.get("middleInitial");
                    String last = (String) fullName.get("last");

                    String phoneNumber = (String) details.get("phoneNumber");

                    userNameText.setText(String.format("%s %s %s", first, middle, last).trim());
                    transactionNumberText.setText(phoneNumber);
                });
    }
}
