package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout currencyDropdown, languageDropdown, helpButton, logoutButton;
    TextView selectedCurrency, selectedLanguage;
    Button updateButton;

    String[] currencies = {"PHP", "USD", "EUR"};
    String[] languages = {"English", "Filipino", "Japanese"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        currencyDropdown = findViewById(R.id.currency_dropdown_container);
        languageDropdown = findViewById(R.id.language_dropdown_container);
        helpButton = findViewById(R.id.help_button);
        logoutButton = findViewById(R.id.logout_button);
        updateButton = findViewById(R.id.update_button);

        selectedCurrency = findViewById(R.id.currency_selected_value);
        selectedLanguage = findViewById(R.id.language_selected_value);

        currencyDropdown.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Choose Currency")
                    .setItems(currencies, (dialog, which) -> {
                        selectedCurrency.setText(currencies[which]);
                    }).show();
        });

        languageDropdown.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Choose Language")
                    .setItems(languages, (dialog, which) -> {
                        selectedLanguage.setText(languages[which]);
                    }).show();
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        updateButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setCancelable(true);

            // Inflate the dialog view
            View view = getLayoutInflater().inflate(R.layout.dialog_update_profile, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();

            // Hook elements inside the dialog
            EditText birthdateInput = view.findViewById(R.id.edit_birthdate);
            Button updateBtn = view.findViewById(R.id.update_btn);
            Button editPinBtn = view.findViewById(R.id.edit_pin_btn);

            // Optional: Date picker
            birthdateInput.setOnClickListener(v1 -> {
                // Add date picker logic here if needed
            });

            // Example: Handle update click
            updateBtn.setOnClickListener(v2 -> {
                // Collect and validate data...
                dialog.dismiss(); // Close dialog after update
            });

            editPinBtn.setOnClickListener(v3 -> {
                // TODO: Add pin change logic or navigation
            });

            dialog.show();

            // After show() so the window is initialized
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                // Remove unwanted padding by modifying the decor view
                View decorView = window.getDecorView();
                decorView.setPadding(50, 0, 50, 0);
            }
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout walletButton = findViewById(R.id.walletButton);
        LinearLayout qrButton = findViewById(R.id.qrButton);
        LinearLayout transactionsButton = findViewById(R.id.transactionsButton);
        LinearLayout profileButton = findViewById(R.id.profileButton);

        transactionsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
            finish();
        });

        walletButton.setOnClickListener(v -> {
            // TODO: Navigate to Wallet screen
        });

        qrButton.setOnClickListener(v -> {
            // TODO: Open QR scanner
        });

        // Mark transactions button as selected
        homeButton.setSelected(true);

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}