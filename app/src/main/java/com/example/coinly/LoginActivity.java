package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilPhoneNumber, tilMpin;
    private TextInputEditText etPhoneNumber, etMpin;
    private Button btnLogin;
    private TextView tvForgotMpin, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();
        
        // Set up listeners
        setupListeners();
    }

    private void initViews() {
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilMpin = findViewById(R.id.tilMpin);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etMpin = findViewById(R.id.etMpin);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotMpin = findViewById(R.id.tvForgotMpin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        // Login button click
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                // In a real app, perform network call or check local database
                // For now, we'll just simulate a successful login
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                
                // Navigate to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close login activity
            }
        });

        // Forgot MPIN click
        tvForgotMpin.setOnClickListener(v -> {
            // TODO: Implement forgot MPIN functionality
            Toast.makeText(this, "Forgot MPIN functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Register click
        tvRegister.setOnClickListener(v -> {
            // TODO: Implement registration functionality
            Toast.makeText(this, "Registration functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate phone number
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            tilPhoneNumber.setError(getString(R.string.invalid_phone));
            isValid = false;
        } else {
            tilPhoneNumber.setError(null);
        }
        
        // Validate MPIN
        String mpin = etMpin.getText().toString().trim();
        if (TextUtils.isEmpty(mpin) || mpin.length() != 4) {
            tilMpin.setError(getString(R.string.invalid_mpin));
            isValid = false;
        } else {
            tilMpin.setError(null);
        }
        
        return isValid;
    }
}