package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    private EditText etFirstName, etMiddleInitial, etLastName;
    private EditText etDay, etMonth, etYear;
    private EditText etEmail, etPhoneNumber, etPassword;
    private Button btnRegister;
    private TextView tvRegisterTitle, tvRegisterHeader, tvFullName, tvBirthday;
    private TextView tvLogin; // Added reference for login TextView
    private LinearLayout llNameFields, llBirthdayFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        initViews();
        
        // Set up button click listeners
        setupListeners();
        
        // Apply animations to form fields
        animateFormFields();
    }

    private void initViews() {
        // Title
        tvRegisterTitle = findViewById(R.id.tvRegisterTitle);
        
        // Register header inside card
        tvRegisterHeader = findViewById(R.id.tvRegisterHeader);
        
        // Name section
        tvFullName = findViewById(R.id.tvFullName);
        llNameFields = findViewById(R.id.llNameFields);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleInitial = findViewById(R.id.etMiddleInitial);
        etLastName = findViewById(R.id.etLastName);
        
        // Birthday section
        tvBirthday = findViewById(R.id.tvBirthday);
        llBirthdayFields = findViewById(R.id.llBirthdayFields);
        etDay = findViewById(R.id.etDay);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        
        // Other fields
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        
        // Button
        btnRegister = findViewById(R.id.btnRegister);

        // Login text
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                // In a real app, this would connect to a backend service to register the user
                // For now, we'll just display a success toast and navigate to the PIN creation page
                
                Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                
                // Navigate to PIN creation activity
                Intent intent = new Intent(RegisterActivity.this, CreatePinActivity.class);
                startActivity(intent);
                
                // Apply slide up animation for the transition
                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                
                finish(); // Close register activity
            }
        });

        // Set click listener for "Already have an account? Login" text
        tvLogin.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            
            // Apply slide animation for transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            
            finish(); // Optional: close RegisterActivity
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply slide down animation when back button is pressed
        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

    private void animateFormFields() {
        // Define the animations
        android.view.animation.Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        android.view.animation.Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        
        // Set initial visibility to invisible
        tvRegisterTitle.setVisibility(View.INVISIBLE);
        tvRegisterHeader.setVisibility(View.INVISIBLE);
        tvFullName.setVisibility(View.INVISIBLE);
        llNameFields.setVisibility(View.INVISIBLE);
        tvBirthday.setVisibility(View.INVISIBLE);
        llBirthdayFields.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        etPhoneNumber.setVisibility(View.INVISIBLE);
        etPassword.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        
        // Stagger animations with increasing delays
        tvRegisterTitle.postDelayed(() -> {
            tvRegisterTitle.setVisibility(View.VISIBLE);
            tvRegisterTitle.startAnimation(slideUp);
        }, 100);
        
        // Apply shake animation to register header to make it stand out
        tvRegisterHeader.postDelayed(() -> {
            tvRegisterHeader.setVisibility(View.VISIBLE);
            tvRegisterHeader.startAnimation(shake);
        }, 150);
        
        tvFullName.postDelayed(() -> {
            tvFullName.setVisibility(View.VISIBLE);
            tvFullName.startAnimation(slideUp);
        }, 200);
        
        llNameFields.postDelayed(() -> {
            llNameFields.setVisibility(View.VISIBLE);
            llNameFields.startAnimation(slideUp);
        }, 300);
        
        tvBirthday.postDelayed(() -> {
            tvBirthday.setVisibility(View.VISIBLE);
            tvBirthday.startAnimation(slideUp);
        }, 400);
        
        llBirthdayFields.postDelayed(() -> {
            llBirthdayFields.setVisibility(View.VISIBLE);
            llBirthdayFields.startAnimation(slideUp);
        }, 500);
        
        etEmail.postDelayed(() -> {
            etEmail.setVisibility(View.VISIBLE);
            etEmail.startAnimation(slideUp);
        }, 600);
        
        etPhoneNumber.postDelayed(() -> {
            etPhoneNumber.setVisibility(View.VISIBLE);
            etPhoneNumber.startAnimation(slideUp);
        }, 700);
        
        etPassword.postDelayed(() -> {
            etPassword.setVisibility(View.VISIBLE);
            etPassword.startAnimation(slideUp);
        }, 800);
        
        btnRegister.postDelayed(() -> {
            btnRegister.setVisibility(View.VISIBLE);
            btnRegister.startAnimation(slideUp);
        }, 900);
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate first name
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            etFirstName.setError("First name is required");
            isValid = false;
        }
        
        // Validate last name
        if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            etLastName.setError("Last name is required");
            isValid = false;
        }
        
        // Validate birthday
        String day = etDay.getText().toString().trim();
        String month = etMonth.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        
        if (TextUtils.isEmpty(day) || TextUtils.isEmpty(month) || TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Please enter a complete birth date", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            // Basic validation for date values
            int dayVal = Integer.parseInt(day);
            int monthVal = Integer.parseInt(month);
            int yearVal = Integer.parseInt(year);
            
            if (dayVal < 1 || dayVal > 31) {
                etDay.setError("Invalid day");
                isValid = false;
            }
            
            if (monthVal < 1 || monthVal > 12) {
                etMonth.setError("Invalid month");
                isValid = false;
            }
            
            if (yearVal < 1900 || yearVal > 2025) {
                etYear.setError("Invalid year");
                isValid = false;
            }
        }
        
        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            isValid = false;
        }
        
        // Validate phone number
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            etPhoneNumber.setError("Please enter a valid phone number");
            isValid = false;
        }
        
        // Validate password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        return isValid;
    }
}