package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coinly.db.Pocket;
import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;
import com.example.coinly.db.Database;

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
                        ((TextView) findViewById(R.id.userName)).setText(data.fullName.formatted());
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
        Pocket.get(userId, new Database.Data<List<Pocket>>() {
            @Override
            public void onSuccess(List<Pocket> data) {
                pocketsList = data;
                if (pocketsList == null) {
                    pocketsList = new ArrayList<>();
                }
                setupPocketsRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load pockets", e);
                Toast.makeText(WalletActivity.this, "Failed to load pockets", Toast.LENGTH_SHORT).show();
                pocketsList = new ArrayList<>();
                setupPocketsRecyclerView();
            }
        });
    }

    private void loadTransactionData() {
        Transaction.get(userId, new Database.Data<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> data) {
                transactionsList = data;
                setupTransactionsRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Wallet", "Tried to get user's transactions", e);
            }
        });
    }

    private void setupPocketsRecyclerView() {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            pocketsRecyclerView.setLayoutManager(layoutManager);
            
            pocketAdapter = new PocketAdapter(pocketsList, (pocket, position) -> {
                // Navigate to pocket details
                Intent intent = new Intent(this, PocketDetailsActivity.class);
                intent.putExtra("id", pocket.id);
                intent.putExtra("POCKET_NAME", pocket.name);
                intent.putExtra("POCKET_TARGET", pocket.target);
                intent.putExtra("POCKET_CURRENT", pocket.balance);
                intent.putExtra("POCKET_LOCKED", pocket.locked);
                startActivity(intent);
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
                Intent intent = new Intent(this, PocketActivity.class);
                startActivity(intent);
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
                        actualBalance = String.format("Php %s", Util.amountFormatter(data.balance));

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