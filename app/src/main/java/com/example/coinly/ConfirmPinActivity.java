package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class ConfirmPinActivity extends AppCompatActivity {

    private TextView[] pinDigits;
    private TextView tvPinError;
    private Button[] numberButtons;
    private Button btnConfirm;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private String originalPin;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);

        // Get the PIN and userId from the previous activity
        originalPin = getIntent().getStringExtra("pin");
        userId = getIntent().getStringExtra("userId");
        
        // If userId is not passed, try to get from shared preferences
        if (userId == null) {
            userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", null);
        }
        
        if (userId == null || originalPin == null) {
            // Required data missing, redirect to login
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
        setupConfirmButton();
        
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
        
        // Error message
        tvPinError = findViewById(R.id.tvPinError);

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
        btnConfirm = findViewById(R.id.btnConfirm);
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
                    
                    // Hide any error
                    tvPinError.setVisibility(View.GONE);
                    
                    // Enable confirm button if PIN is complete
                    if (pinBuilder.length() == 4) {
                        btnConfirm.setEnabled(true);
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
                
                // Disable confirm button if PIN is incomplete
                if (pinBuilder.length() < 4) {
                    btnConfirm.setEnabled(false);
                }
            }
        });
    }

    private void setupConfirmButton() {
        btnConfirm.setOnClickListener(v -> {
            // Add null check to prevent NullPointerException
            if (originalPin != null && originalPin.equals(pinBuilder.toString())) {
                // PINs match - save to database
                btnConfirm.setEnabled(false);
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
                
                // Create credentials with PIN
                User.Credentials credentials = new User.Credentials()
                    .withPin(originalPin.toCharArray());
                
                // Store the PIN in the database
                User.setPin(userId, credentials, new Database.Data<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        // Hide loading indicator
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        
                        // PIN saved successfully
                        Toast.makeText(ConfirmPinActivity.this, R.string.pin_success, Toast.LENGTH_SHORT).show();
                        
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(ConfirmPinActivity.this, WalletActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            
                            // Apply slide up animation
                            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                            
                            finish();
                        }, 1500);
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        // Hide loading indicator
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        
                        // Failed to save PIN
                        btnConfirm.setEnabled(true);
                        Toast.makeText(ConfirmPinActivity.this, 
                            "Failed to save PIN: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // PINs don't match
                tvPinError.setVisibility(View.VISIBLE);
                
                // Clear the PIN input
                pinBuilder.setLength(0);
                updatePinDisplay();
                btnConfirm.setEnabled(false);
                
                // Shake animation for error
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                findViewById(R.id.pinDisplay).startAnimation(shake);
            }
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
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }
    
    private void animateUI() {
        // Animate UI elements from bottom to top
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        // Stagger animations with delays
        int delay = 100;
        animateViewWithDelay(findViewById(R.id.tvPinConfirmDescription), slideUp, delay * 2);
        animateViewWithDelay(findViewById(R.id.pinDisplay), slideUp, delay * 3);
        animateViewWithDelay(findViewById(R.id.keypad), slideUp, delay * 4);
        animateViewWithDelay(findViewById(R.id.btnConfirm), slideUp, delay * 5);
    }
    
    private void animateViewWithDelay(View view, Animation animation, int delay) {
        view.setVisibility(View.INVISIBLE);
        view.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }, delay);
    }
}