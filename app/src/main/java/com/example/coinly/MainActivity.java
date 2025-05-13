package com.example.coinly;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            return;
        }

        navController = navHostFragment.getNavController();

        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout walletButton = findViewById(R.id.walletButton);
        LinearLayout qrButton = findViewById(R.id.qrButton);
        LinearLayout transactionsButton = findViewById(R.id.transactionsButton);
        LinearLayout profileButton = findViewById(R.id.profileButton);

        homeButton.setOnClickListener(v -> {
            safeNavigate(R.id.home_fragment);
        });

        transactionsButton.setOnClickListener(v -> {
            safeNavigate(R.id.transactions_fragment);
        });

        walletButton.setOnClickListener(v -> {
            safeNavigate(R.id.pocket_fragment);
        });

        qrButton.setOnClickListener(v -> {
            safeNavigate(R.id.qrscanner_fragment);
        });

        profileButton.setOnClickListener(v -> {
            safeNavigate(R.id.profile_fragment);
        });
    }

    private void safeNavigate(int destinationId) {
        Log.i("safeNavigation", "Going to destination: " + destinationId);

        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }
}