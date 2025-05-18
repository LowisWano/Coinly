package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.User;
import com.example.coinly.db.Database;
import java.util.GregorianCalendar;

public class RegisterFragment extends Fragment {
    private Context ctx;

    private EditText etFirstName, etMiddleInitial, etLastName;
    private EditText etDay, etMonth, etYear;
    private EditText etEmail, etPhoneNumber, etPassword;
    private Button btnRegister;
    private TextView tvRegisterTitle, tvRegisterHeader, tvFullName, tvBirthday;
    private TextView tvLogin;
    private LinearLayout llNameFields, llBirthdayFields;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupListeners(view);
        animateFormFields();
    }

    private void initViews(View view) {
        tvRegisterTitle = view.findViewById(R.id.tvRegisterTitle);
        tvRegisterHeader = view.findViewById(R.id.tvRegisterHeader);
        
        tvFullName = view.findViewById(R.id.tvFullName);
        llNameFields = view.findViewById(R.id.llNameFields);
        etFirstName = view.findViewById(R.id.etFirstName);
        etMiddleInitial = view.findViewById(R.id.etMiddleInitial);
        etLastName = view.findViewById(R.id.etLastName);
        
        tvBirthday = view.findViewById(R.id.tvBirthday);
        llBirthdayFields = view.findViewById(R.id.llBirthdayFields);
        etDay = view.findViewById(R.id.etDay);
        etMonth = view.findViewById(R.id.etMonth);
        etYear = view.findViewById(R.id.etYear);

        etEmail = view.findViewById(R.id.etEmail);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);

        btnRegister = view.findViewById(R.id.btnRegister);
        tvLogin = view.findViewById(R.id.tvLogin);
    }

    private void setupListeners(View view) {
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                char middleInitial = etMiddleInitial.getText().toString().isEmpty() ? ' ' : 
                                      etMiddleInitial.getText().toString().charAt(0);
                
                int day = Integer.parseInt(etDay.getText().toString().trim());
                int month = Integer.parseInt(etMonth.getText().toString().trim()) - 1;
                int year = Integer.parseInt(etYear.getText().toString().trim());
                
                GregorianCalendar birthdate = new GregorianCalendar(year, month, day);
                
                User.Credentials credentials = new User.Credentials()
                    .withEmail(email)
                    .withPassword(password);
                
                User.Details.FullName fullName = new User.Details.FullName()
                    .withFirst(firstName)
                    .withLast(lastName)
                    .withMiddleInitial(middleInitial);
                
                User.Details details = new User.Details()
                    .withPhoneNumber(phoneNumber)
                    .withFullName(fullName)
                    .withBirthdate(birthdate);
                
                loadingProgressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
                
                User.signUp(credentials, details, new Database.Data<String>() {
                    @Override
                    public void onSuccess(String userId) {
                        loadingProgressBar.setVisibility(View.GONE);
                        
                        ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE)
                            .edit()
                            .putString("userId", userId)
                            .apply();

                        Toast.makeText(ctx, R.string.registration_success, Toast.LENGTH_SHORT).show();


                        Navigation.findNavController(view)
                                .navigate(RegisterFragmentDirections.actionRegisterFragmentToCreatePinFragment());
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        loadingProgressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        
                        String errorMessage = "Registration failed";
                        if (e instanceof Database.KeyAlreadyExists) {
                            errorMessage = "Email already exists";
                        }
                        
                        Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        tvLogin.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());
        });
    }

    private void animateFormFields() {
        android.view.animation.Animation slideUp = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        android.view.animation.Animation shake = AnimationUtils.loadAnimation(ctx, R.anim.shake);
        
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
        
        tvRegisterTitle.postDelayed(() -> {
            tvRegisterTitle.setVisibility(View.VISIBLE);
            tvRegisterTitle.startAnimation(slideUp);
        }, 100);
        
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
        
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            etFirstName.setError("First name is required");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            etLastName.setError("Last name is required");
            isValid = false;
        }
        
        String day = etDay.getText().toString().trim();
        String month = etMonth.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        
        if (TextUtils.isEmpty(day) || TextUtils.isEmpty(month) || TextUtils.isEmpty(year)) {
            Toast.makeText(ctx, "Please enter a complete birth date", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
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
        
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            isValid = false;
        }
        
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            etPhoneNumber.setError("Please enter a valid phone number");
            isValid = false;
        }
        
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        return isValid;
    }
}