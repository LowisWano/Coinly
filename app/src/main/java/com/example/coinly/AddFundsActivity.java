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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddFundsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView availableBalance;
    private TextView balanceAfter;
    private TextInputEditText amountInput;
    private MaterialButton confirmAddFundsButton;
    
    private String pocketName;
    private double currentAmount;
    private double targetAmount;
    private int iconResId;
    private boolean isLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_funds);
        
        // Initialize views
        initViews();
        
        // Get data from intent
        getDataFromIntent();
        
        // Display pocket details
        displayPocketDetails();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up text change listener
        setupTextChangeListener();
    }
    
    private void initViews() {
        backButton = findViewById(R.id.backButton);
        availableBalance = findViewById(R.id.availableBalance);
        balanceAfter = findViewById(R.id.balanceAfter);
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
        }
    }
    
    private void displayPocketDetails() {
        // Format and set current amount
        String formattedBalance = "Php " + String.format("%,.2f", currentAmount);
        availableBalance.setText(formattedBalance);
        
        // Initially set balance after to the same as current amount
        balanceAfter.setText(formattedBalance);
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
                updateBalanceAfter();
            }
        });
    }
    
    private void updateBalanceAfter() {
        try {
            double amountToAdd = 0;
            if (amountInput.getText() != null && !amountInput.getText().toString().isEmpty()) {
                amountToAdd = Double.parseDouble(amountInput.getText().toString());
            }
            
            double newBalance = currentAmount + amountToAdd;
            String formattedNewBalance = "Php " + String.format("%,.2f", newBalance);
            balanceAfter.setText(formattedNewBalance);
        } catch (NumberFormatException e) {
            balanceAfter.setText("Php " + String.format("%,.2f", currentAmount));
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
                
                // Create result intent to pass back the updated amount
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ADDED_AMOUNT", amountToAdd);
                
                // Show success message
                Toast.makeText(this, "Added Php " + String.format("%,.2f", amountToAdd) + " to " + pocketName, Toast.LENGTH_SHORT).show();
                
                // Set the result and finish
                setResult(RESULT_OK, resultIntent);
                finish();
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
