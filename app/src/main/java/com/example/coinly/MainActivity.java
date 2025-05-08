package com.example.coinly;

import android.content.Intent;
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
                // Navigate to wallet activity
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });
        
        // Set up click listeners for bottom navigation
        setupBottomNavListeners();
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
                // Navigate to wallet activity
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            });
        }
        
        if (qrCodeNav != null) {
            qrCodeNav.setOnClickListener(v -> {
                Toast.makeText(this, "QR Code scanner coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (transactionsNav != null) {
            transactionsNav.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            });
        }
        
        if (profileNav != null) {
            profileNav.setOnClickListener(v -> {
                Toast.makeText(this, "Profile screen coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }
}