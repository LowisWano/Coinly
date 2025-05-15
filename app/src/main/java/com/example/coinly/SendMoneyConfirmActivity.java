package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class SendMoneyConfirmActivity extends AppCompatActivity {

    private ImageView backArrow;
    private boolean transactionDone = false;

    private String senderId;
    private String recipientPhone;
    private float amount;

    // UI elements
    private TextView userNameText, recipientNumberText, amountText, totalAmountText, referenceNumberText, transactionDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_confirm);

        // Get data from intent
        senderId = getIntent().getStringExtra("senderId");
        recipientPhone = getIntent().getStringExtra("number");
        amount = Float.parseFloat(getIntent().getStringExtra("amount"));

        // UI references
        ImageView swipeHandle = findViewById(R.id.swipe_handle);
        FrameLayout swipeContainer = findViewById(R.id.swipe_container);
        TextView swipeText = findViewById(R.id.swipe_text);
        backArrow = findViewById(R.id.backArrow);

        userNameText = findViewById(R.id.userName);
        recipientNumberText = findViewById(R.id.recipientNumber);
        amountText = findViewById(R.id.amount);
        totalAmountText = findViewById(R.id.totalAmount);
        referenceNumberText = findViewById(R.id.referenceNumber);
        transactionDateText = findViewById(R.id.transactionDate);

        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(SendMoneyConfirmActivity.this, SendMoneyActivity.class));
        });

        // Display transaction data
        recipientNumberText.setText(recipientPhone);
        amountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        totalAmountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        transactionDateText.setText(formatDate(Calendar.getInstance()));

        // Fetch reference number
        FirebaseFirestore.getInstance()
                .collection("counters")
                .document("transactions")
                .get()
                .addOnSuccessListener(doc -> {
                    long ref = doc.getLong("value") + 1;
                    referenceNumberText.setText("Ref number: " + ref);
                });

        // Get recipient name from phone number
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("details.phoneNumber", recipientPhone)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        var doc = query.getDocuments().get(0);
                        var fullName = (Map<String, Object>) ((Map<String, Object>) doc.get("details")).get("fullName");
                        String first = (String) fullName.get("first");
                        String last = (String) fullName.get("last");
                        String middle = (String) fullName.get("middleInitial");
                        userNameText.setText(String.format("%s %s %s", first, middle, last).trim());
                    }
                });

        swipeHandle.setOnTouchListener(new View.OnTouchListener() {
            float downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float containerWidth = swipeContainer.getWidth();
                float handleWidth = swipeHandle.getWidth();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float translationX = moveX - downX;
                        if (translationX >= 0 && translationX <= containerWidth - handleWidth) {
                            swipeHandle.setTranslationX(translationX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (swipeHandle.getTranslationX() >= (containerWidth - handleWidth - 20) && !transactionDone) {
                            swipeText.setText("Confirmed");
                            swipeHandle.setEnabled(false);
                            swipeHandle.setTranslationX(containerWidth - handleWidth);
                            performTransaction();
                        } else {
                            swipeHandle.animate().translationX(0).setDuration(200).start();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void performTransaction() {
        transactionDone = true;

        User.sendMoneyFromPhoneNumber(senderId, recipientPhone, amount, new Database.Data<String>() {
            @Override
            public void onSuccess(String transactionId) {
                Toast.makeText(SendMoneyConfirmActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SendMoneyConfirmActivity.this, SendMoneySummaryActivity.class);
                intent.putExtra("transactionId", transactionId);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                transactionDone = false;
                Toast.makeText(SendMoneyConfirmActivity.this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
