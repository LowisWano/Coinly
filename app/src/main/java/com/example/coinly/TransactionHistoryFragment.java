package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private EditText searchEditText;
    private ImageButton filterButton;
    private TextView balanceText, transactionCount;
    private double currentBalance = 0.0;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadTransactions();
        setupSearch();
        setupFilter();
    }

    private void initViews(@NonNull View view) {
        recyclerView = view.findViewById(R.id.transactionsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.filterButton);
        balanceText = view.findViewById(R.id.balanceText);
        transactionCount = view.findViewById(R.id.transactionCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.viewAllButton).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RequestTransactionHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadTransactions() {
        // TODO: Replace with actual data loading from database/API
        allTransactions = new ArrayList<>();
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

        // Update balance display
        balanceText.setText(String.format("₱ %,.2f", Math.abs(currentBalance)));

        // Update filtered transactions
        filteredTransactions.clear();
        filteredTransactions.addAll(allTransactions);
        adapter.notifyDataSetChanged();
        
        // Update the transaction count text
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
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
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
}