package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;
import com.example.coinly.db.Database;

import androidx.appcompat.app.AppCompatActivity;
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
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");

        setupViews();
        loadTransactions();
        setupSearch();
        setupFilter();
        setupBottomNavigation();
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

        findViewById(R.id.viewAllButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestTransactionHistoryActivity.class);
            startActivity(intent);
        });

        ((TextView) findViewById(R.id.referralCodeText)).setText(userId);

        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        ((TextView) findViewById(R.id.userNameText)).setText(data.fullName.formatted());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TransactionHistory", "Tried to get user's details", e);
                    }
                }
        );

        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>() {
                    @Override
                    public void onSuccess(User.Wallet data) {
                        ((TextView) findViewById(R.id.balanceText)).setText(Util.amountFormatter(data.balance));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TransactionHistory", "Tried to get user's wallet", e);
                    }
                }
        );
    }

    private static final String TAG = "TransactionHistory";

    private void loadTransactions() {
        // Show loading state
        Toast.makeText(this, "Loading transactions...", Toast.LENGTH_SHORT).show();

        Transaction.get(userId, new Database.Data<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> data) {
                allTransactions = data;

                filteredTransactions.clear();
                filteredTransactions.addAll(allTransactions);

                ((TextView) findViewById(R.id.transactionCount)).setText(
                        String.format("Last 7 days (%d)", allTransactions.size())
                );

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TransactionHistory", "Tried to get user's transactions", e);
            }
        });
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

//        dialog.findViewById(R.id.filterDeposit).setOnClickListener(v -> {
//            filterByType(true); // true for coins added
//            dialog.dismiss();
//        });

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
                if (transaction.name.toLowerCase().contains(query.toLowerCase()) ||
                        transaction.date.toString().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add(transaction);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByType(boolean isCoinsAdded) {
        filteredTransactions.clear();
        for (Transaction transaction : allTransactions) {
            if ((isCoinsAdded && transaction.type == Transaction.Type.Receive) ||
                    (!isCoinsAdded && transaction.type == Transaction.Type.Deposit)) {
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
            startActivity(new Intent(this, WalletActivity.class));
            finish();
        });

        walletButton.setOnClickListener(v -> {
            startActivity(new Intent(this, PocketActivity.class));
            finish();
        });

        qrButton.setOnClickListener(v -> {
            // TODO: Open QR scanner
        });

        // Mark transactions button as selected
        transactionsButton.setSelected(true);

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
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