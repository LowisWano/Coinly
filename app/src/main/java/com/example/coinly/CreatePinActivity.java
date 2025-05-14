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

import com.example.coinly.db.User;
import com.example.coinly.db.Database;

public class CreatePinActivity extends AppCompatActivity {

    private TextView[] pinDigits;
    private Button[] numberButtons;
    private Button btnNext;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        
        // Get user ID from intent or shared preferences
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", null);
        }
        
        if (userId == null) {
            // No userId found, redirect to login
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        initializeViews();
        
        // Set up listeners
        setupNumberPad();
        setupBackspaceButton();
        setupNextButton();
        
        // Apply entrance animations
        animateUI();
    }

    private void initializeViews() {
        // PIN display
        pinDigits = new TextView[4];
        pinDigits[0] = findViewById(R.id.pinDigit1);
        pinDigits[1] = findViewById(R.id.pinDigit2);
        pinDigits[2] = findViewById(R.id.pinDigit3);
        pinDigits[3] = findViewById(R.id.pinDigit4);

        // Number buttons
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

        // Other buttons
        btnBackspace = findViewById(R.id.btnBackspace);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupNumberPad() {
        for (int i = 0; i < 10; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> {
                if (pinBuilder.length() < 4) {
                    // Add digit to PIN
                    pinBuilder.append(number);
                    
                    // Update PIN display
                    updatePinDisplay();
                    
                    // Enable next button if PIN is complete
                    if (pinBuilder.length() == 4) {
                        btnNext.setEnabled(true);
                    }
                }
            });
        }
    }

    private void setupBackspaceButton() {
        btnBackspace.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                // Remove last digit
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                
                // Update PIN display
                updatePinDisplay();
                
                // Disable next button if PIN is incomplete
                if (pinBuilder.length() < 4) {
                    btnNext.setEnabled(false);
                }
            }
        });
    }

    private void setupNextButton() {
        btnNext.setOnClickListener(v -> {
            // Pass the created PIN and userId to the confirm PIN activity
            Intent intent = new Intent(CreatePinActivity.this, ConfirmPinActivity.class);
            intent.putExtra("pin", pinBuilder.toString());
            intent.putExtra("userId", userId);
            startActivity(intent);
            
            // Apply slide animation
            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
        });
    }

    private void updatePinDisplay() {
        // Reset all PIN displays to empty
        for (int i = 0; i < 4; i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_empty);
        }
        
        // Fill in circles for entered digits
        for (int i = 0; i < pinBuilder.length(); i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_filled);
        }
    }
    
    private void animateUI() {
        // Animate UI elements from bottom to top
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        // Stagger animations with delays
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