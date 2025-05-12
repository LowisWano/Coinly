package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout currencyDropdown, languageDropdown, helpButton, logoutButton;
    TextView selectedCurrency, selectedLanguage;

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
    }
}