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

public class ConfirmPinActivity extends AppCompatActivity {

    private TextView[] pinDigits;
    private TextView tvPinError;
    private Button[] numberButtons;
    private Button btnConfirm;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private String originalPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);

        // Get the PIN from the previous activity
        originalPin = getIntent().getStringExtra("pin");
        
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
                // PINs match
                Toast.makeText(this, R.string.pin_success, Toast.LENGTH_SHORT).show();
                
                // Short delay before moving to the main screen
                new Handler().postDelayed(() -> {
                    // Navigate to main activity
                    Intent intent = new Intent(ConfirmPinActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    
                    // Apply slide up animation
                    overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                    
                    finish();
                }, 1500);
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
        animateViewWithDelay(findViewById(R.id.tvConfirmPinTitle), slideUp, delay);
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