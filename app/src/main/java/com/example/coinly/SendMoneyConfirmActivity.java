package com.example.coinly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SendMoneyConfirmActivity extends AppCompatActivity {

    private static final String TAG = "SendMoneyConfirm";

    private ImageView backArrow;
    private boolean transactionDone = false;

    private String senderId;
    private String recipientPhone;
    private float amount;

    // UI elements
    private TextView userNameText, recipientNumberText, amountText, totalAmountText, transactionDateText;
    private String formattedDate = "";
    private String referenceNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_confirm);

        // Get data from intent
        senderId = getIntent().getStringExtra("senderId");
        recipientPhone = getIntent().getStringExtra("number");
        amount = Float.parseFloat(getIntent().getStringExtra("amount"));

        formattedDate = formatDate(Calendar.getInstance());

        // Bind UI
        ImageView swipeHandle = findViewById(R.id.swipe_handle);
        FrameLayout swipeContainer = findViewById(R.id.swipe_container);
        TextView swipeText = findViewById(R.id.swipe_text);
        backArrow = findViewById(R.id.backArrow);

        userNameText = findViewById(R.id.userName);
        recipientNumberText = findViewById(R.id.recipientNumber);
        amountText = findViewById(R.id.amount);
        totalAmountText = findViewById(R.id.totalAmount);
        transactionDateText = findViewById(R.id.transactionDate);

        // Back navigation
        backArrow.setOnClickListener(v -> finish());

        // Static display
        recipientNumberText.setText(recipientPhone);
        amountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        totalAmountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        transactionDateText.setText(formattedDate);
        // Load recipient name
        User.getFromPhoneNumber(recipientPhone, User.Details.class, new Database.Data<User.Details>() {
            @Override
            public void onSuccess(User.Details data) {
                userNameText.setText(data.fullName.formatted());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SendMoneyConfirm", "Tried to get user's details", e);
            }
        });

        // Swipe listener
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

        Context ctx = this;

        User.sendMoneyFromPhoneNumber(senderId, recipientPhone, amount, new Database.Data<String>() {
            @Override
            public void onSuccess(String id) {
                Log.i(TAG, "Transaction success: ID = " + id);
                Toast.makeText(ctx, "Transaction successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ctx, SendMoneySummaryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {
                transactionDone = false;
                Log.e(TAG, "Transaction failed", e);
            }
        });
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
