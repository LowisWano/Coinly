package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ProgressBar;

import com.example.coinly.db.Database;
import com.example.coinly.db.Pocket;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class PocketDetailsActivity extends AppCompatActivity {

    private static final int ADD_FUNDS_REQUEST_CODE = 100;
    
    private ImageView pocketIcon;
    private ImageView pocketLockIcon;
    private ImageView backButton;
    private TextView pocketName;
    private TextView pocketStatus;
    private TextView currentAmount;
    private TextView targetAmount;
    private TextView progressPercent;
    private ProgressBar pocketProgress;
    private MaterialButton addFundsButton;
    private MaterialButton withdrawFundsButton;

    private FirebaseFirestore db;
    private String pocketId;
    private double targetAmt;
    private double currentAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pocket_details);
        
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        // Initialize views
        initViews();
        
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // Get pocket ID for Firestore updates
            pocketId = intent.getStringExtra("id");
            
            String name = intent.getStringExtra("POCKET_NAME");
            targetAmt = intent.getDoubleExtra("POCKET_TARGET", 0.0);
            currentAmt = intent.getDoubleExtra("POCKET_CURRENT", 0.0);
            int iconResId = intent.getIntExtra("POCKET_ICON", android.R.drawable.ic_menu_directions);
            boolean isLocked = intent.getBooleanExtra("POCKET_LOCKED", false);
            
            // Log the values for debugging
            Log.d("PocketDetails", "Received data from intent: ID=" + pocketId +
                  ", Name=" + name + ", Target=" + targetAmt + 
                  ", Balance=" + currentAmt + ", Locked=" + isLocked);
            
            // Calculate progress percentage
            int progressPercentage = (int) (currentAmt / targetAmt * 100);
            
            // Display pocket details
            displayPocketDetails(name, targetAmt, currentAmt, iconResId, isLocked, progressPercentage);
        }
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initViews() {
        String id = getIntent().getStringExtra("id");

        pocketIcon = findViewById(R.id.pocketIcon);
        pocketLockIcon = findViewById(R.id.pocketLockIcon);
        backButton = findViewById(R.id.backButton);
        pocketName = findViewById(R.id.pocketName);
        pocketStatus = findViewById(R.id.pocketStatus);
        currentAmount = findViewById(R.id.currentAmount);
        targetAmount = findViewById(R.id.targetAmount);
        progressPercent = findViewById(R.id.progressPercent);
        pocketProgress = findViewById(R.id.pocketProgress);
        addFundsButton = findViewById(R.id.addFundsButton);
        withdrawFundsButton = findViewById(R.id.withdrawFundsButton);
        
        // Ensure back button is properly styled
        if (backButton != null) {
            backButton.setClickable(true);
            backButton.setFocusable(true);
        } else {
            Toast.makeText(this, "Back button NOT found", Toast.LENGTH_LONG).show();
        }
        Pocket.getFrom(id, new Database.Data<Pocket>() {
            @Override
            public void onSuccess(Pocket data) {
                pocketName.setText(data.name);
                targetAmount.setText("Php " + Util.amountFormatter(data.target));
                currentAmount.setText("Php " + Util.amountFormatter(data.balance));
                pocketProgress.setProgress(data.percent());
                progressPercent.setText(data.percent() + " %");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PocketDetails", "Tried to get pocket", e);
            }
        });
    }
    
    private void displayPocketDetails(String name, double targetAmt, double currentAmt, int iconResId, boolean isLocked, int progressPercentage) {
        // Set pocket name and icon
        pocketName.setText(name);
        pocketIcon.setImageResource(iconResId);
        
        // Set lock icon visibility
        pocketLockIcon.setVisibility(isLocked ? View.VISIBLE : View.GONE);
        
        // Set pocket status
        pocketStatus.setText(isLocked ? "Locked" : "Active");
        pocketStatus.setTextColor(getResources().getColor(isLocked ? android.R.color.holo_red_light : android.R.color.holo_green_light));
        
        // Format and set amounts
        String formattedTarget = "Php " + String.format("%,.2f", targetAmt);
        String formattedCurrent = "Php " + String.format("%,.2f", currentAmt);
        
        targetAmount.setText(formattedTarget);
        currentAmount.setText(formattedCurrent);
        
        // Set progress
        pocketProgress.setProgress(progressPercentage);
        progressPercent.setText(progressPercentage + "%");
    }
    
    @Override
    public void onBackPressed() {
        // Navigate back to PocketActivity
        navigateToPocketsScreen();
    }
    
    private void navigateToPocketsScreen() {
        Intent intent = new Intent(PocketDetailsActivity.this, PocketActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
    
    private void setupClickListeners() {
        // Set back button click listener
        backButton.setOnClickListener(v -> {
            navigateToPocketsScreen();
        });
        
        // Set add funds button click listener
        addFundsButton.setOnClickListener(v -> {
            // Navigate to Add Funds activity
            Intent addFundsIntent = new Intent(PocketDetailsActivity.this, AddFundsActivity.class);
            
            // Pass the pocket details to the Add Funds activity
            addFundsIntent.putExtra("POCKET_NAME", pocketName.getText().toString());
            addFundsIntent.putExtra("POCKET_TARGET", getTargetAmountValue());
            addFundsIntent.putExtra("POCKET_CURRENT", getCurrentAmountValue());
            addFundsIntent.putExtra("POCKET_ICON", getIntent().getIntExtra("POCKET_ICON", android.R.drawable.ic_menu_directions));
            addFundsIntent.putExtra("POCKET_LOCKED", getIntent().getBooleanExtra("POCKET_LOCKED", false));
            
            // Start the activity expecting a result
            startActivityForResult(addFundsIntent, ADD_FUNDS_REQUEST_CODE);
        });
        
        // Set withdraw funds button click listener
        withdrawFundsButton.setOnClickListener(v -> {
            boolean isLocked = pocketLockIcon.getVisibility() == View.VISIBLE;
            if (isLocked) {
                Toast.makeText(PocketDetailsActivity.this, 
                    "Cannot withdraw from locked pocket", Toast.LENGTH_SHORT).show();
                return;
            }
            
            showWithdrawDialog();
        });
    }

    /**
     * Extract the current amount value from the currentAmount TextView
     */
    private double getCurrentAmountValue() {
        String currentAmountStr = currentAmount.getText().toString();
        // Remove the "Php " prefix and any commas
        currentAmountStr = currentAmountStr.replace("Php ", "").replace(",", "");
        try {
            return Double.parseDouble(currentAmountStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Extract the target amount value from the targetAmount TextView
     */
    private double getTargetAmountValue() {
        String targetAmountStr = targetAmount.getText().toString();
        // Remove the "Php " prefix and any commas
        targetAmountStr = targetAmountStr.replace("Php ", "").replace(",", "");
        try {
            return Double.parseDouble(targetAmountStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_FUNDS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the added amount from the result
            double addedAmount = data.getDoubleExtra("ADDED_AMOUNT", 0.0);

            // Update the current amount
            double newCurrentAmount = getCurrentAmountValue() + addedAmount;
            double targetAmt = getTargetAmountValue();

            // Calculate new progress percentage
            int progressPercentage = (int) (newCurrentAmount / targetAmt * 100);

            // Update the display with new values
            String formattedCurrent = "Php " + String.format("%,.2f", newCurrentAmount);
            currentAmount.setText(formattedCurrent);

            // Update progress bar and percentage
            pocketProgress.setProgress(Math.min(progressPercentage, 100)); // Cap at 100%
            progressPercent.setText(Math.min(progressPercentage, 100) + "%");
        }
    }
    
    private void processWithdrawal(double amount) {
        // Add debug logs
        Log.d("PocketDetails", "Processing withdrawal of " + amount + 
              " from current balance of " + currentAmt);
        
        if (amount <= 0) {
            Toast.makeText(this, "Please enter an amount greater than 0", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Set a minimum balance threshold to prevent insufficient fund errors
        if (currentAmt < 5) {
            // If balance is too low or zero, show a helpful message
            Toast.makeText(this, "Cannot withdraw: balance too low or not yet loaded", 
                Toast.LENGTH_LONG).show();
            return;
        }
        
        if (amount > currentAmt) {
            Toast.makeText(this, "Insufficient funds in this pocket", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get references to both documents
        DocumentReference pocketRef = db.collection("pockets").document(pocketId);
        String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");
        DocumentReference userRef = db.collection("users").document(userId);
        
        // Run a transaction to update both documents atomically
        db.runTransaction(transaction -> {
            // Get current pocket data
            DocumentSnapshot pocketSnap = transaction.get(pocketRef);
            if (!pocketSnap.exists()) {
                throw new FirebaseFirestoreException("Pocket not found", FirebaseFirestoreException.Code.NOT_FOUND);
            }
            
            // Get current user data
            DocumentSnapshot userSnap = transaction.get(userRef);
            if (!userSnap.exists()) {
                throw new FirebaseFirestoreException("User not found", FirebaseFirestoreException.Code.NOT_FOUND);
            }
            
            // Get current balances
            double pocketBalance = pocketSnap.getDouble("balance");
            double userBalance = userSnap.getDouble("wallet.balance");
            
            // Update pocket balance
            transaction.update(pocketRef, "balance", pocketBalance - amount);
            
            // Update user's wallet balance
            transaction.update(userRef, "wallet.balance", userBalance + amount);
            
            return null;
        })
        .addOnSuccessListener(aVoid -> {
            // Update UI
            currentAmt -= amount;
            int progressPercentage = (int) (currentAmt / targetAmt * 100);
            
            // Update display
            String formattedCurrent = "Php " + String.format("%,.2f", currentAmt);
            currentAmount.setText(formattedCurrent);
            pocketProgress.setProgress(progressPercentage);
            progressPercent.setText(progressPercentage + "%");
            
            Toast.makeText(PocketDetailsActivity.this, 
                "Successfully withdrew Php " + String.format("%,.2f", amount), 
                Toast.LENGTH_SHORT).show();
            
            Log.d("PocketDetails", "Withdrawal successful. New balance: " + currentAmt);
        })
        .addOnFailureListener(e -> {
            Log.e("PocketDetails", "Withdrawal failed: " + e.getMessage(), e);
            Toast.makeText(PocketDetailsActivity.this, 
                "Failed to withdraw: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        });
    }

    private void showWithdrawDialog() {
        // Create TextInputLayout for amount input
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_withdraw, null);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("Withdraw Funds")
            .setView(dialogView)
            .setPositiveButton("Withdraw", (dialog, which) -> {
                String amountStr = amountInput.getText().toString();
                if (!amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        processWithdrawal(amount);
                    } catch (NumberFormatException e) {
                        Toast.makeText(PocketDetailsActivity.this, 
                            "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PocketDetailsActivity.this, 
                        "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}