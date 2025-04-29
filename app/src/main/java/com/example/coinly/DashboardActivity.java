package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter adapter;
    private CustomBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupBottomNavigation();
        setupTransactionsList();
    }

    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(itemId -> {
            if (itemId == R.id.nav_home) {
                // Already on home
                return;
            } else if (itemId == R.id.nav_wallet) {
                // Navigate to wallet
                Toast.makeText(this, "Wallet coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_qr) {
                // Open QR scanner
                Toast.makeText(this, "QR Scanner coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionHistoryActivity.class));
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Set home as selected by default
        bottomNavigation.setSelectedItem(R.id.nav_home);
    }

    private void setupTransactionsList() {
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Sample data
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Netflix Subscription", "January 27, 2025", -300.00));
        transactions.add(new Transaction("Youtube Premium", "January 26, 2025", -239.00));
        transactions.add(new Transaction("24 Chicken", "January 23, 2025", 45.00));
        
        adapter = new TransactionAdapter(transactions);
        transactionsRecyclerView.setAdapter(adapter);

        // Add click listener to view all transactions
        findViewById(R.id.viewAllTransactions).setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionHistoryActivity.class);
            startActivity(intent);
        });
    }
}