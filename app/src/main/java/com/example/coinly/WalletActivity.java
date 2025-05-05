package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    private static final String TAG = "WalletActivity";
    
    private RecyclerView pocketsRecyclerView;
    private RecyclerView transactionsRecyclerView;
    private PocketAdapter pocketAdapter;
    private TransactionAdapter transactionAdapter;
    private List<Pocket> pocketsList;
    private List<Transaction> transactionsList;
    private TextView balanceAmount;
    private ImageButton hideBalanceButton;
    private boolean isBalanceHidden = false;
    private String actualBalance = "Php 1,242.69";
    private String hiddenBalance = "Php ••••••";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        try {
            initViews();
            setupHideBalanceButton();
            loadPocketData();
            loadTransactionData();
            setupPocketsRecyclerView();
            setupTransactionsRecyclerView();
            setupBottomNavigation();
            setupSeeAllButtons();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing wallet activity", e);
            Toast.makeText(this, "Error opening wallet: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        pocketsRecyclerView = findViewById(R.id.pocketsRecyclerView);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        balanceAmount = findViewById(R.id.balanceAmount);
        hideBalanceButton = findViewById(R.id.hideBalanceButton);

        // Set user name dynamically (would come from user profile in a real app)
        TextView userNameTextView = findViewById(R.id.userName);
        userNameTextView.setText("John Doe");
        
        // Set initial balance
        balanceAmount.setText(actualBalance);
    }

    private void setupHideBalanceButton() {
        if (hideBalanceButton != null) {
            hideBalanceButton.setOnClickListener(v -> {
                isBalanceHidden = !isBalanceHidden;
                balanceAmount.setText(isBalanceHidden ? hiddenBalance : actualBalance);
                hideBalanceButton.setImageResource(isBalanceHidden ? 
                        R.drawable.ic_visibility_off : R.drawable.ic_visibility);
            });
        }
    }

    private void loadPocketData() {
        // In a real app, this data would come from a database or API
        pocketsList = new ArrayList<>();
        pocketsList.add(new Pocket("Home", 50000.00, 43000.00, R.drawable.ic_home, false));
        pocketsList.add(new Pocket("Motorcycle", 120000.00, 24000.00, R.drawable.ic_motorcycle, true));
    }

    private void loadTransactionData() {
        // In a real app, this data would come from a database or API
        transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction("Netflix Subscription", "April 27, 2025", -300.00));
        transactionsList.add(new Transaction("Youtube Premium", "April 26, 2025", -239.00));
        transactionsList.add(new Transaction("Deposit from 24 Chicken", "April 23, 2025", 45.00));
    }

    private void setupPocketsRecyclerView() {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            pocketsRecyclerView.setLayoutManager(layoutManager);
            
            pocketAdapter = new PocketAdapter(pocketsList, (pocket, position) -> {
                // Handle pocket item click
                Toast.makeText(this, pocket.getName() + " pocket clicked", Toast.LENGTH_SHORT).show();
                // Navigate to pocket details in a real app
            });
            
            pocketsRecyclerView.setAdapter(pocketAdapter);
        }
    }

    private void setupTransactionsRecyclerView() {
        if (transactionsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            transactionsRecyclerView.setLayoutManager(layoutManager);
            
            transactionAdapter = new TransactionAdapter(transactionsList);
            transactionsRecyclerView.setAdapter(transactionAdapter);
            
            // Limit height by showing only a few items
            transactionsRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void setupBottomNavigation() {
        try {
            CustomBottomNavigation bottomNav = findViewById(R.id.bottomNavigation);
            if (bottomNav != null) {
                bottomNav.setOnNavigationItemSelectedListener(itemId -> {
                    if (itemId == R.id.nav_home) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else if (itemId == R.id.nav_wallet) {
                        // Already on wallet page
                    } else if (itemId == R.id.nav_qr) {
                        Toast.makeText(this, "QR Scanner coming soon", Toast.LENGTH_SHORT).show();
                    } else if (itemId == R.id.nav_transactions) {
                        startActivity(new Intent(this, TransactionHistoryActivity.class));
                        finish();
                    } else if (itemId == R.id.nav_profile) {
                        Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
                
                // Set wallet as selected
                bottomNav.setSelectedItem(R.id.nav_wallet);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation", e);
        }
    }
    
    private void setupSeeAllButtons() {
        // Set up "See All" buttons
        TextView seeAllPockets = findViewById(R.id.seeAllPockets);
        if (seeAllPockets != null) {
            seeAllPockets.setOnClickListener(v -> {
                Toast.makeText(this, "All Pockets coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        TextView seeAllTransactions = findViewById(R.id.seeAllTransactions);
        if (seeAllTransactions != null) {
            seeAllTransactions.setOnClickListener(v -> {
                Intent intent = new Intent(this, TransactionHistoryActivity.class);
                startActivity(intent);
            });
        }
    }
}