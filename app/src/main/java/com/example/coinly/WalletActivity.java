package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coinly.db.User;
import com.example.coinly.db.Database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");

        try {
            initViews();
            setupHideBalanceButton();
            fetchUserBalance();
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

        // Set initial balance
        balanceAmount.setText(actualBalance);

        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        ((TextView) findViewById(R.id.userName)).setText(String.format(
                                "%s %s%s",
                                data.fullName.first,
                                (data.fullName.middleInitial != '\0') ? data.fullName.middleInitial + ". " : "",
                                data.fullName.last
                                )
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Wallet", "Tried to get user's details", e);
                    }
                }
        );
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
        transactionsList = new ArrayList<>();
        
        String email = "destin@gmail.com";
        String password = "password";
        
        // Create credentials object
        User.Credentials credentials = new User.Credentials()
                .withEmail(email)
                .withPassword(password);
        
        // Call getTransactions method
        User.getTransactions(credentials, new Database.Data<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> transactions) {
                // Process transactions data
                for (Map<String, Object> transaction : transactions) {
                    String name = (String) transaction.get("name");
                    
                    // Get date as timestamp and format it
                    Date date = transaction.get("date") instanceof com.google.firebase.Timestamp ? 
                            ((com.google.firebase.Timestamp) transaction.get("date")).toDate() : new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);
                    
                    // Get amount and refNum
                    double amount = transaction.get("amount") instanceof Number ? 
                            ((Number) transaction.get("amount")).doubleValue() : 0.0;
                    String refNum = (String) transaction.get("refNum");
                    
                    // Add transaction to list
                    transactionsList.add(new Transaction(name, formattedDate, amount, refNum, "Your Wallet", name));
                }

                // Update the RecyclerView on UI thread
                runOnUiThread(() -> {
                    if (transactionAdapter != null) {
                        transactionAdapter.notifyDataSetChanged();
                    } else {
                        setupTransactionsRecyclerView();
                    }
                });
            }
            
            @Override
            public void onFailure(Exception e) {
                // Handle error
                Log.e(TAG, "Failed to fetch transactions", e);
                runOnUiThread(() -> {
                    Toast.makeText(WalletActivity.this, 
                            "Could not retrieve transactions: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    
                    // Use fallback data
                    transactionsList.add(new Transaction("Netflix Subscription", "April 27, 2025", -300.00));
                    transactionsList.add(new Transaction("Youtube Premium", "April 26, 2025", -239.00));
                    transactionsList.add(new Transaction("Deposit from 24 Chicken", "April 23, 2025", 45.00));
                    
                    if (transactionAdapter != null) {
                        transactionAdapter.notifyDataSetChanged();
                    } else {
                        setupTransactionsRecyclerView();
                    }
                });
            }
        });
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
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout walletButton = findViewById(R.id.walletButton);
        LinearLayout qrButton = findViewById(R.id.qrButton);
        LinearLayout transactionsButton = findViewById(R.id.transactionsButton);
        LinearLayout profileButton = findViewById(R.id.profileButton);

        transactionsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
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
        homeButton.setSelected(true);

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
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

    private void fetchUserBalance() {
        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>() {
                    @Override
                    public void onSuccess(User.Wallet data) {
                        actualBalance = String.format("Php %.2f", data.balance);

                        if (!isBalanceHidden) {
                            balanceAmount.setText(actualBalance);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Wallet", "Tried to get user's balance", e);
                    }
                }
        );
    }
}