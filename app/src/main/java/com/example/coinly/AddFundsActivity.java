package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFundsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView availableBalance;
    private TextView balanceAfter;
    private TextView pocketBalanceAfter;
    private TextInputEditText amountInput;
    private MaterialButton confirmAddFundsButton;
    
    private String pocketName;
    private double currentAmount;
    private double targetAmount;
    private int iconResId;
    private boolean isLocked;
    private String pocketId;
    private String userId;
    private double walletBalance;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_funds);
        
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        // Initialize views
        initViews();
        
        // Get data from intent
        getDataFromIntent();
        
        // Get user's wallet balance
        getWalletBalance();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up text change listener
        setupTextChangeListener();
    }
    
    private void initViews() {
        backButton = findViewById(R.id.backButton);
        availableBalance = findViewById(R.id.availableBalance);
        balanceAfter = findViewById(R.id.balanceAfter);
        pocketBalanceAfter = findViewById(R.id.pocketBalanceAfter);
        amountInput = findViewById(R.id.amountInput);
        confirmAddFundsButton = findViewById(R.id.confirmAddFundsButton);
    }
    
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            pocketName = intent.getStringExtra("POCKET_NAME");
            currentAmount = intent.getDoubleExtra("POCKET_CURRENT", 0.0);
            targetAmount = intent.getDoubleExtra("POCKET_TARGET", 0.0);
            iconResId = intent.getIntExtra("POCKET_ICON", android.R.drawable.ic_menu_directions);
            isLocked = intent.getBooleanExtra("POCKET_LOCKED", false);
            pocketId = intent.getStringExtra("id");
            userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");
        }
    }

    private void getWalletBalance() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                walletBalance = documentSnapshot.getDouble("wallet.balance");
                // Display wallet balance
                String formattedBalance = "Php " + String.format("%,.2f", walletBalance);
                availableBalance.setText(formattedBalance);
                balanceAfter.setText(formattedBalance);
                
                // Display initial pocket balance
                String formattedPocketBalance = "Php " + String.format("%,.2f", currentAmount);
                pocketBalanceAfter.setText(formattedPocketBalance);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get wallet balance", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    
    private void setupClickListeners() {
        // Set back button click listener
        backButton.setOnClickListener(v -> {
            navigateBack();
        });
        
        // Set add funds button click listener
        confirmAddFundsButton.setOnClickListener(v -> {
            addFunds();
        });
    }
    
    private void setupTextChangeListener() {
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBalancesAfter();
            }
        });
    }
    
    private void updateBalancesAfter() {
        try {
            double amountToAdd = 0;
            if (amountInput.getText() != null && !amountInput.getText().toString().isEmpty()) {
                amountToAdd = Double.parseDouble(amountInput.getText().toString());
            }
            
            // Update wallet balance after
            double newWalletBalance = walletBalance - amountToAdd;
            String formattedWalletBalance = "Php " + String.format("%,.2f", newWalletBalance);
            balanceAfter.setText(formattedWalletBalance);
            
            // Update pocket balance after
            double newPocketBalance = currentAmount + amountToAdd;
            String formattedPocketBalance = "Php " + String.format("%,.2f", newPocketBalance);
            pocketBalanceAfter.setText(formattedPocketBalance);
        } catch (NumberFormatException e) {
            balanceAfter.setText("Php " + String.format("%,.2f", walletBalance));
            pocketBalanceAfter.setText("Php " + String.format("%,.2f", currentAmount));
        }
    }
    
    private void addFunds() {
        try {
            if (amountInput.getText() != null && !amountInput.getText().toString().isEmpty()) {
                double amountToAdd = Double.parseDouble(amountInput.getText().toString());
                
                if (amountToAdd <= 0) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amountToAdd > walletBalance) {
                    Toast.makeText(this, "Insufficient wallet balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Get references to both documents
                DocumentReference pocketRef = db.collection("pockets").document(pocketId);
                DocumentReference userRef = db.collection("users").document(userId);
                
                // Run a transaction to update both documents atomically
                db.runTransaction(transaction -> {
                    // Get current pocket data
                    double pocketBalance = transaction.get(pocketRef).getDouble("balance");
                    double userBalance = transaction.get(userRef).getDouble("wallet.balance");
                    
                    // Update pocket balance
                    transaction.update(pocketRef, "balance", pocketBalance + amountToAdd);
                    
                    // Update user's wallet balance
                    transaction.update(userRef, "wallet.balance", userBalance - amountToAdd);
                    
                    return null;
                })
                .addOnSuccessListener(aVoid -> {
                    // Show success message
                    Toast.makeText(this, "Added Php " + String.format("%,.2f", amountToAdd) + " to " + pocketName, Toast.LENGTH_SHORT).show();
                    
                    // Create result intent to pass back the updated amount
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("ADDED_AMOUNT", amountToAdd);
                    
                    // Set the result and finish
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add funds: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateBack() {
        // Just finish this activity to return to PocketDetailsActivity
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // Navigate back to PocketDetailsActivity
        navigateBack();
    }
}
