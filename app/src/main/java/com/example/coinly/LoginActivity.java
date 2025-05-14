package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.coinly.db.User;
import com.example.coinly.db.Database;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;

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
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
//        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        // Login button click
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                // Create credentials object
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                
                User.Credentials credentials = new User.Credentials()
                    .withEmail(email)
                    .withPassword(password);
                
                // Show loading indicator
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                
                // Authenticate user
                User.login(credentials, new Database.Data<String>() {
                    @Override
                    public void onSuccess(String userId) {
                        // Hide loading indicator
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        
                        // Store user ID in shared preferences for future use
                        getSharedPreferences("coinly", MODE_PRIVATE)
                            .edit()
                            .putString("userId", userId)
                            .apply();
                        
                        Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        
                        // Navigate to main activity
                        Intent intent = new Intent(LoginActivity.this, WalletActivity.class);
                        startActivity(intent);
                        finish(); // Close login activity
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        // Hide loading indicator
                        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        
                        String errorMessage = "Login failed";
                        if (e instanceof Database.DataNotFound) {
                            errorMessage = "Invalid email or password";
                            // Highlight the fields with error
                            tilEmail.setError(" ");
                            tilPassword.setError(" ");
                            
                            // Shake animation for error feedback
                            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                            findViewById(R.id.loginCardView).startAnimation(shake);
                        } else {
                            // Other error - probably network or server issue
                            errorMessage = "Login failed: " + e.getMessage();
                        }
                        
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Forgot password click
//        tvForgotPassword.setOnClickListener(v -> {
//            // TODO: Implement forgot password functionality
//            Toast.makeText(this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
//        });

        // Register click - Navigate to RegisterActivity with animation
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            
            // Apply the slide up animation
            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // Validate password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        } else {
            tilPassword.setError(null);
        }
        
        return isValid;
    }
}