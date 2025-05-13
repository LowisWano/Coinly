package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    LinearLayout currencyDropdown, languageDropdown, helpButton, logoutButton;
    TextView selectedCurrency, selectedLanguage;

    String[] currencies = {"PHP", "USD", "EUR"};
    String[] languages = {"English", "Filipino", "Japanese"};

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currencyDropdown = view.findViewById(R.id.currency_dropdown_container);
        languageDropdown = view.findViewById(R.id.language_dropdown_container);
        helpButton = view.findViewById(R.id.help_button);
        logoutButton = view.findViewById(R.id.logout_button);

        selectedCurrency = view.findViewById(R.id.currency_selected_value);
        selectedLanguage = view.findViewById(R.id.language_selected_value);

        currencyDropdown.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Choose Currency")
                    .setItems(currencies, (dialog, which) -> {
                        selectedCurrency.setText(currencies[which]);
                    }).show();
        });

        languageDropdown.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Choose Language")
                    .setItems(languages, (dialog, which) -> {
                        selectedLanguage.setText(languages[which]);
                    }).show();
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });
    }
}