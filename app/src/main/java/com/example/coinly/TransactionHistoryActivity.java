package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private EditText searchEditText;
    private ImageButton filterButton;
    private TextView balanceText;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_transaction);

        setupToolbar();
        setupViews();
        loadTransactions();
        setupSearch();
        setupFilter();
        setupBottomNavigation();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.transactionsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);
        balanceText = findViewById(R.id.balanceText);
        TextView transactionCount = findViewById(R.id.transactionCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.viewAllTransactions).setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestTransactionHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadTransactions() {
        // TODO: Replace with actual data loading from database/API
        allTransactions.add(new Transaction(
            "Netflix Subscription", 
            "January 27, 2025", 
            -300.00,
            "NF27012025",
            "Your Wallet",
            "Netflix, Inc."
        ));
        allTransactions.add(new Transaction(
            "Youtube Premium", 
            "January 26, 2025", 
            -239.00,
            "YT26012025",
            "Your Wallet",
            "Google LLC"
        ));
        allTransactions.add(new Transaction(
            "24 Chicken", 
            "January 23, 2025", 
            45.00,
            "24C23012025",
            "24 Chicken",
            "Your Wallet"
        ));
        allTransactions.add(new Transaction(
            "Burp", 
            "January 15, 2025", 
            19.00,
            "BP15012025",
            "Burp App",
            "Your Wallet"
        ));

        // Calculate current balance
        currentBalance = 0.0;
        for (Transaction transaction : allTransactions) {
            currentBalance += transaction.getAmount();
        }

        // Update balance display - show 0 if negative
        balanceText.setText(String.format("₱ %.2f", Math.max(0, currentBalance)));

        filteredTransactions.addAll(allTransactions);
        adapter.notifyDataSetChanged();
        
        // Update the transaction count text
        TextView transactionCount = findViewById(R.id.transactionCount);
        transactionCount.setText(String.format("Last 7 days (%d)", allTransactions.size()));
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilter() {
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_filter_transactions);

        dialog.findViewById(R.id.filterAll).setOnClickListener(v -> {
            filteredTransactions.clear();
            filteredTransactions.addAll(allTransactions);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterDeposit).setOnClickListener(v -> {
            filterByType(true); // true for coins added
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterExpenses).setOnClickListener(v -> {
            filterByType(false); // false for coins spent
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterTransactions(String query) {
        filteredTransactions.clear();
        if (query.isEmpty()) {
            filteredTransactions.addAll(allTransactions);
        } else {
            for (Transaction transaction : allTransactions) {
                if (transaction.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        transaction.getDate().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add(transaction);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByType(boolean isCoinsAdded) {
        filteredTransactions.clear();
        for (Transaction transaction : allTransactions) {
            if ((isCoinsAdded && transaction.getAmount() > 0) ||
                    (!isCoinsAdded && transaction.getAmount() < 0)) {
                filteredTransactions.add(transaction);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout walletButton = findViewById(R.id.walletButton);
        LinearLayout qrButton = findViewById(R.id.qrButton);
        LinearLayout transactionsButton = findViewById(R.id.transactionsButton);
        LinearLayout profileButton = findViewById(R.id.profileButton);

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        walletButton.setOnClickListener(v -> {
            // TODO: Navigate to Wallet screen
        });

        qrButton.setOnClickListener(v -> {
            // TODO: Open QR scanner
        });

        // Mark transactions button as selected
        transactionsButton.setSelected(true);

        profileButton.setOnClickListener(v -> {
            // TODO: Navigate to Profile screen
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}