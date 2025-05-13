package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView balanceAmountText;
    private Button openWalletButton;
    private TextView seeAllTransactionsText;
    private RecyclerView recentTransactionsRecyclerView;
    private TextView noTransactionsText;
    private LinearLayout homeButton, walletButton, qrButton, transactionsButton;
    
    private String walletBalance = "₱ 1,000,000"; // Static data for now
    private List<Transaction> transactionsList;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        initializeUI();
        
        // Load sample transaction data
        loadSampleTransactions();
        
        // Setup recyclerview
        setupTransactionRecyclerView();
        
        // Set up click listeners
        setupListeners();
    }
    
    private void initializeUI() {
        balanceAmountText = findViewById(R.id.balanceAmount);
        openWalletButton = findViewById(R.id.openWalletButton);
        seeAllTransactionsText = findViewById(R.id.seeAllTransactions);
        recentTransactionsRecyclerView = findViewById(R.id.recentTransactionsRecyclerView);
        noTransactionsText = findViewById(R.id.noTransactionsText);
        
        // Bottom navigation
        homeButton = findViewById(R.id.homeButton);
        walletButton = findViewById(R.id.walletButton);
        qrButton = findViewById(R.id.qrButton);
        transactionsButton = findViewById(R.id.transactionsButton);
        
        // Set static data for wallet balance
        balanceAmountText.setText(walletBalance);
    }
    
    private void loadSampleTransactions() {
        transactionsList = new ArrayList<>();
        
        // Add some sample transactions
        transactionsList.add(new Transaction("Received from John Doe", "Today, 2:30 PM", 500.00));
        transactionsList.add(new Transaction("Paid to Coffee Shop", "Today, 11:25 AM", -120.50));
        transactionsList.add(new Transaction("Received from Maria Garcia", "Yesterday, 5:15 PM", 1000.00));
        
        // Show/hide the "no transactions" message
        if (transactionsList.isEmpty()) {
            recentTransactionsRecyclerView.setVisibility(View.GONE);
            noTransactionsText.setVisibility(View.VISIBLE);
        } else {
            recentTransactionsRecyclerView.setVisibility(View.VISIBLE);
            noTransactionsText.setVisibility(View.GONE);
        }
    }
    
    private void setupTransactionRecyclerView() {
        transactionAdapter = new TransactionAdapter(transactionsList);
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentTransactionsRecyclerView.setAdapter(transactionAdapter);
        recentTransactionsRecyclerView.setNestedScrollingEnabled(false);
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
        
        // Handle See All Transactions click
        seeAllTransactionsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
        
        // Set up click listeners for bottom navigation
        homeButton.setSelected(true); // Highlight home button
        
        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });
        
        transactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}