package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

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
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);
    }

    private void loadTransactions() {
        // TODO: Replace with actual data loading from database/API
        allTransactions.add(new Transaction("Netflix Subscription", "January 27, 2025", -300.00));
        allTransactions.add(new Transaction("Youtube Premium", "January 26, 2025", -239.00));
        allTransactions.add(new Transaction("24 Chicken", "January 23, 2025", 45.00));
        allTransactions.add(new Transaction("Burp", "January 15, 2025", 19.00));
        
        filteredTransactions.addAll(allTransactions);
        adapter.notifyDataSetChanged();
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
        
        // Setup filter options (All, Income, Expenses)
        dialog.findViewById(R.id.filterAll).setOnClickListener(v -> {
            filteredTransactions.clear();
            filteredTransactions.addAll(allTransactions);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterIncome).setOnClickListener(v -> {
            filterByType(true);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterExpenses).setOnClickListener(v -> {
            filterByType(false);
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

    private void filterByType(boolean isIncome) {
        filteredTransactions.clear();
        for (Transaction transaction : allTransactions) {
            if ((isIncome && transaction.getAmount() > 0) ||
                (!isIncome && transaction.getAmount() < 0)) {
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