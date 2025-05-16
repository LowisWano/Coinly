package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.coinly.db.User;
import com.example.coinly.db.Database;

public class LoginFragment extends Fragment {
    private Context ctx;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupListeners(view);
        checkIfLoggedIn(view);
    }

    private void checkIfLoggedIn(View view) {
        String userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", "");

        if (userId.isEmpty()) {
            return;
        }

        User.get(
                userId,
                User.Credentials.class,
                new Database.Data<User.Credentials>() {
                    @Override
                    public void onSuccess(User.Credentials data) {
                        Navigation.findNavController(view)
                                .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (!(e instanceof Database.DataNotFound)) {
                            Log.e("Login", "Tried to get user's credentials", e);
                        }
                    }
                }
        );
    }

    private void initViews(View view) {
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegister = view.findViewById(R.id.tvRegister);
        // TODO: Implement forgot password
    }

    private void setupListeners(View view) {
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                
                User.Credentials credentials = new User.Credentials()
                    .withEmail(email)
                    .withPassword(password);
                
                loadingProgressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                
                User.login(credentials, new Database.Data<String>() {
                    @Override
                    public void onSuccess(String userId) {
                        loadingProgressBar.setVisibility(View.GONE);
                        
                        ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE)
                            .edit()
                            .putString("userId", userId)
                            .apply();
                        
                        Toast.makeText(ctx, R.string.login_success, Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(view)
                                .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment());
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        view.findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        
                        String errorMessage = "Login failed";

                        if (e instanceof Database.DataNotFound) {
                            errorMessage = "Invalid email or password";

                            tilEmail.setError(" ");
                            tilPassword.setError(" ");
                            
                            Animation shake = AnimationUtils.loadAnimation(ctx, R.anim.shake);
                            view.findViewById(R.id.loginCardView).startAnimation(shake);
                        } else {
                            errorMessage = "Login failed: " + e.getMessage();
                        }
                        
                        Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        tvRegister.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        return isValid;
    }
}