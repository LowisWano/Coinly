package com.example.coinly;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView balanceAmountText;
    private Button openWalletButton;
    private String walletBalance = "1,000,000"; // Static data for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize UI components
        initializeUI();
        
        // Set up click listeners
        setupListeners();
    }
    
    private void initializeUI() {
        balanceAmountText = findViewById(R.id.balanceAmount);
        openWalletButton = findViewById(R.id.openWalletButton);
        
        // Set static data for wallet balance
        balanceAmountText.setText(walletBalance);
    }
    
    private void setupListeners() {
        // Handle Open Wallet button click
        openWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening wallet details", Toast.LENGTH_SHORT).show();
                // Future implementation: Navigate to wallet details screen
            }
        });
        
        // Set up click listeners for feature buttons
        setupFeatureButtonListeners();
        
        // Set up click listeners for bottom navigation
        setupBottomNavListeners();
    }
    
    private void setupFeatureButtonListeners() {
        // Find all feature button container views
        View streakButton = findViewById(R.id.streakButton);
        View redeemButton = findViewById(R.id.redeemButton);
        View leaderboardButton = findViewById(R.id.leaderboardButton);
        
        // Add click listeners to each feature button
        if (streakButton != null) {
            streakButton.setOnClickListener(v -> {
                Toast.makeText(this, "Streak feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (redeemButton != null) {
            redeemButton.setOnClickListener(v -> {
                Toast.makeText(this, "Redeem feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (leaderboardButton != null) {
            leaderboardButton.setOnClickListener(v -> {
                Toast.makeText(this, "Leaderboard feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void setupBottomNavListeners() {
        // Find all bottom navigation items
        View homeNav = findViewById(R.id.homeNav);
        View walletNav = findViewById(R.id.walletNav);
        View qrCodeNav = findViewById(R.id.qrCodeNav);
        View transactionsNav = findViewById(R.id.transactionsNav);
        View profileNav = findViewById(R.id.profileNav);
        
        // Add click listeners to each bottom navigation item
        if (homeNav != null) {
            homeNav.setOnClickListener(v -> {
                Toast.makeText(this, "You're already on the Home screen", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (walletNav != null) {
            walletNav.setOnClickListener(v -> {
                Toast.makeText(this, "Wallet screen coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (qrCodeNav != null) {
            qrCodeNav.setOnClickListener(v -> {
                Toast.makeText(this, "QR Code scanner coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (transactionsNav != null) {
            transactionsNav.setOnClickListener(v -> {
                Toast.makeText(this, "Transactions screen coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (profileNav != null) {
            profileNav.setOnClickListener(v -> {
                Toast.makeText(this, "Profile screen coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }
}