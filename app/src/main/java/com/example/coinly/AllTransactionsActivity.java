package com.example.coinly;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class AllTransactionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private EditText searchEditText;
    private ImageButton filterButton;
    private TextView balanceAmount;
    private TextView totalIncome;
    private TextView totalExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transactions);

        setupViews();
        setupToolbar();
        loadTransactions();
        setupSearch();
        setupFilter();
        updateTransactionTotals();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.transactionsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);
        balanceAmount = findViewById(R.id.balanceAmount);
        totalIncome = findViewById(R.id.totalIncome);
        totalExpenses = findViewById(R.id.totalExpenses);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);
    }

    private void loadTransactions() {
        // TODO: Replace with actual data loading from database
        allTransactions.add(new Transaction("Netflix Subscription", "January 27, 2025", -300.00));
        allTransactions.add(new Transaction("Youtube Premium", "January 26, 2025", -239.00));
        allTransactions.add(new Transaction("Salary", "January 25, 2025", 25000.00));
        allTransactions.add(new Transaction("24 Chicken", "January 23, 2025", -450.00));
        allTransactions.add(new Transaction("Freelance Work", "January 20, 2025", 15000.00));
        allTransactions.add(new Transaction("Grocery Shopping", "January 18, 2025", -2500.00));
        
        filteredTransactions.addAll(allTransactions);
        adapter.notifyDataSetChanged();
        updateTransactionTotals();
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

    private void updateTransactionTotals() {
        double income = 0;
        double expenses = 0;

        for (Transaction transaction : allTransactions) {
            if (transaction.getAmount() > 0) {
                income += transaction.getAmount();
            } else {
                expenses += Math.abs(transaction.getAmount());
            }
        }

        double balance = income - expenses;
        
        balanceAmount.setText(String.format("Php %.2f", balance));
        totalIncome.setText(String.format("+ Php %.2f", income));
        totalExpenses.setText(String.format("- Php %.2f", expenses));
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