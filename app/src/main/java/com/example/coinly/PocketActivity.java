package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinly.db.Database;
import com.example.coinly.db.Pocket;
import com.example.coinly.db.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PocketActivity extends AppCompatActivity {

    private RecyclerView pocketsRecyclerView;
    private PocketCardAdapter pocketAdapter;
    private List<Pocket> pocketsList;
    private ImageButton filterButton;
    private FloatingActionButton addPocketButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pocket);
        
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        initViews();
        loadPocketData();
        setupBottomNavigation();
    }
    
    private void initViews() {
        pocketsRecyclerView = findViewById(R.id.pocketsRecyclerView);
        filterButton = findViewById(R.id.filterButton);
        addPocketButton = findViewById(R.id.addPocketButton);
        
        // Set up filter button
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                // TODO: Implement filtering functionality
            });
        }
        
        // Set up add pocket button
        if (addPocketButton != null) {
            addPocketButton.setOnClickListener(v -> showCreatePocketDialog());
        }
    }

    private void showCreatePocketDialog() {
        // Inflate the dialog layout
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_pocket, null);
        
        // Get references to the input fields
        TextInputEditText nameInput = dialogView.findViewById(R.id.pocketNameInput);
        TextInputEditText balanceInput = dialogView.findViewById(R.id.pocketBalanceInput);
        TextInputEditText targetInput = dialogView.findViewById(R.id.pocketTargetInput);
        
        // Create and show the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true);
            
        AlertDialog dialog = builder.create();
        
        // Set up the cancel button
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
        
        // Set up the create button
        dialogView.findViewById(R.id.createButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String balanceStr = balanceInput.getText().toString().trim();
            String targetStr = targetInput.getText().toString().trim();
            
            // Validate inputs
            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }
            
            double balance = 0.0;
            double target = 0.0;
            
            try {
                if (!balanceStr.isEmpty()) {
                    balance = Double.parseDouble(balanceStr);
                }
                if (!targetStr.isEmpty()) {
                    target = Double.parseDouble(targetStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get current user ID
            String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");
            
            // Create pocket data
            Map<String, Object> pocketData = new HashMap<>();
            pocketData.put("userId", userId);
            pocketData.put("name", name);
            pocketData.put("balance", balance);
            pocketData.put("target", target);
            pocketData.put("locked", false);
            
            // Save to Firestore
            db.collection("pockets")
                .add(pocketData)
                .addOnSuccessListener(documentReference -> {
                    runOnUiThread(() -> {
                        Toast.makeText(PocketActivity.this, "Pocket created successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadPocketData(); // Reload the pockets list
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        Toast.makeText(PocketActivity.this, "Failed to create pocket", Toast.LENGTH_SHORT).show();
                        Log.e("PocketActivity", "Failed to create pocket", e);
                    });
                });
        });
        
        dialog.show();
    }
    
    private void loadPocketData() {
        String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", "");

        Pocket.get(userId, new Database.Data<List<Pocket>>() {
            @Override
            public void onSuccess(List<Pocket> data) {
                Log.i("Pocket", "Successfully retrieved pocket list");
                pocketsList = data;
                setupPocketsRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Pocket", "Tried to get user's pockets", e);
            }
        });
    }
    
    private void setupPocketsRecyclerView() {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            pocketsRecyclerView.setLayoutManager(layoutManager);
            
            pocketAdapter = new PocketCardAdapter(pocketsList, new PocketCardAdapter.OnPocketCardListener() {
                @Override
                public void onPocketClick(Pocket pocket, int position) {
                    // Handle pocket item click
                    // TODO: Navigate to pocket details activity
                    Toast.makeText(PocketActivity.this, "Selected pocket: " + pocket.name, Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onViewDetailsClick(Pocket pocket, int position) {
                    // Navigate to pocket details activity
                    Intent intent = new Intent(PocketActivity.this, PocketDetailsActivity.class);
                    
                    // First pass the ID for Firestore fetching
                    intent.putExtra("id", pocket.id);
                    
                    // Also pass all pocket data directly as fallback
                    intent.putExtra("POCKET_NAME", pocket.name);
                    intent.putExtra("POCKET_TARGET", pocket.target);
                    intent.putExtra("POCKET_CURRENT", pocket.balance);
                    intent.putExtra("POCKET_LOCKED", pocket.locked);
                    
                    // For debugging
                    Log.d("PocketActivity", "Sending pocket to details: ID=" + pocket.id + 
                          ", Name=" + pocket.name + ", Target=" + pocket.target + 
                          ", Balance=" + pocket.balance + ", Locked=" + pocket.locked);
                    
                    startActivity(intent);
                }
                
                @Override
                public void onAddFundsClick(Pocket pocket, int position) {
                    // Handle add funds button click
                    Toast.makeText(PocketActivity.this, "Add funds to: " + pocket.name, Toast.LENGTH_SHORT).show();
                    // TODO: Navigate to add funds activity/dialog
                }
            });
            
            pocketsRecyclerView.setAdapter(pocketAdapter);

            pocketsRecyclerView.setPadding(0, 0, 0, 160);
            pocketsRecyclerView.setClipToPadding(false);
        }
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
        
        // Mark wallet button as selected as this is the wallet/pockets screen
        walletButton.setSelected(true);
        
        qrButton.setOnClickListener(v -> {
            // TODO: Open QR scanner
        });
        
        transactionsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
            finish();
        });
        
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}