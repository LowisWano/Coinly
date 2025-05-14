package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

public class ChangePinActivity extends AppCompatActivity {

    private TextView[] pinDigits;
    private Button[] numberButtons;
    private Button btnNext, btnCancel;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePinActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        initializeViews();
        setupNumberPad();
        setupBackspaceButton();
        setupNextButton();
        animateUI();
    }

    private void initializeViews() {
        pinDigits = new TextView[4];
        pinDigits[0] = findViewById(R.id.pinDigit1);
        pinDigits[1] = findViewById(R.id.pinDigit2);
        pinDigits[2] = findViewById(R.id.pinDigit3);
        pinDigits[3] = findViewById(R.id.pinDigit4);

        numberButtons = new Button[10];
        numberButtons[0] = findViewById(R.id.btn0);
        numberButtons[1] = findViewById(R.id.btn1);
        numberButtons[2] = findViewById(R.id.btn2);
        numberButtons[3] = findViewById(R.id.btn3);
        numberButtons[4] = findViewById(R.id.btn4);
        numberButtons[5] = findViewById(R.id.btn5);
        numberButtons[6] = findViewById(R.id.btn6);
        numberButtons[7] = findViewById(R.id.btn7);
        numberButtons[8] = findViewById(R.id.btn8);
        numberButtons[9] = findViewById(R.id.btn9);

        btnBackspace = findViewById(R.id.btnBackspace);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupNumberPad() {
        for (int i = 0; i < 10; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> {
                if (pinBuilder.length() < 4) {
                    pinBuilder.append(number);
                    updatePinDisplay();
                    btnNext.setEnabled(pinBuilder.length() == 4);
                }
            });
        }
    }

    private void setupBackspaceButton() {
        btnBackspace.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                updatePinDisplay();
                btnNext.setEnabled(false);
            }
        });
    }

    private void setupNextButton() {
        btnNext.setOnClickListener(v -> {
            char[] enteredPin = pinBuilder.toString().toCharArray();

            findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);

            User.Credentials credentials = new User.Credentials().withPin(enteredPin);
            Database.db().collection("users").document(userId).get()
                    .addOnSuccessListener(snapshot -> {
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        btnNext.setEnabled(true);

                        if (!snapshot.exists()) {
                            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String storedPin = snapshot.getString("credentials.pin");
                        if (storedPin == null || !storedPin.equals(pinBuilder.toString())) {
                            Toast.makeText(this, "Incorrect old PIN.", Toast.LENGTH_SHORT).show();
                            clearPinInput();
                        } else {
                            Intent intent = new Intent(ChangePinActivity.this, CreatePinActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        btnNext.setEnabled(true);
                        Toast.makeText(this, "Error verifying PIN: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void clearPinInput() {
        pinBuilder.setLength(0);
        updatePinDisplay();
    }

    private void updatePinDisplay() {
        for (int i = 0; i < 4; i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_empty);
        }
        for (int i = 0; i < pinBuilder.length(); i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_filled);
        }
    }

    private void animateUI() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        int delay = 100;
        animateViewWithDelay(findViewById(R.id.tvPinDescription), slideUp, delay * 2);
        animateViewWithDelay(findViewById(R.id.pinDisplay), slideUp, delay * 3);
        animateViewWithDelay(findViewById(R.id.keypad), slideUp, delay * 4);
        animateViewWithDelay(findViewById(R.id.btnNext), slideUp, delay * 5);
    }

    private void animateViewWithDelay(View view, Animation animation, int delay) {
        view.setVisibility(View.INVISIBLE);
        view.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }, delay);
    }
}