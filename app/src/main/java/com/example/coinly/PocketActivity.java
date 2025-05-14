package com.example.coinly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PocketActivity extends AppCompatActivity {

    private RecyclerView pocketsRecyclerView;
    private PocketCardAdapter pocketAdapter;
    private List<Pocket> pocketsList;
    private ImageButton filterButton;
    private FloatingActionButton addPocketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pocket);
        
        initViews();
        loadPocketData();
        setupPocketsRecyclerView();
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
            addPocketButton.setOnClickListener(v -> {
                // TODO: Implement add pocket functionality
            });
        }
    }
    
    private void loadPocketData() {
        pocketsList = new ArrayList<>();
        
        // Get userId from SharedPreferences
        String userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", null);
        
        // If no userId found, use hardcoded credentials for testing
        if (userId == null) {
            // Fallback to hardcoded data for testing
            pocketsList.add(new Pocket("Homezzzzz", 50000.00, 43000.00, android.R.drawable.ic_menu_directions, false));
            pocketsList.add(new Pocket("Vehicle", 120000.00, 24000.00, android.R.drawable.ic_menu_directions, true));
            return;
        }
        
        // Get user credentials from SharedPreferences or hardcode for testing
        String email = "l@gmail.com"; // In real app, get from SharedPreferences
        String password = "password";       // In real app, get from SharedPreferences or keystore
        
        // Create credentials object
        User.Credentials credentials = new User.Credentials()
                .withEmail(email)
                .withPassword(password);
        
        // Call getPockets method
        User.getPockets(credentials, new Database.Data<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> pockets) {
                // Convert Firebase data to Pocket objects
                pocketsList.clear();
                for (Map<String, Object> pocketData : pockets) {
                    String name = (String) pocketData.get("name");
                    boolean isActive = (boolean) pocketData.getOrDefault("isActive", true);
                    
                    // Handle numeric types which could be Double, Long, or Integer
                    Number targetAmount = (Number) pocketData.getOrDefault("targetAmount", 0);
                    Number currentAmount = (Number) pocketData.getOrDefault("currentAmount", 0);
                    
                    // Add the pocket to the list with an appropriate icon
                    // In a real app, you might want to map pocket types to specific icons
                    int iconResId = android.R.drawable.ic_menu_directions;
                    
                    pocketsList.add(new Pocket(
                            name, 
                            targetAmount.doubleValue(), 
                            currentAmount.doubleValue(), 
                            iconResId, 
                            !isActive));
                }
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    if (pocketAdapter != null) {
                        pocketAdapter.notifyDataSetChanged();
                    } else {
                        setupPocketsRecyclerView();
                    }
                });
            }
            
            @Override
            public void onFailure(Exception e) {
                // If failed to get pockets, use hardcoded data
                runOnUiThread(() -> {
                    Toast.makeText(PocketActivity.this, 
                            "Failed to load pockets: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    
                    // Use fallback data
                    pocketsList.clear();
                    pocketsList.add(new Pocket("Home", 50000.00, 43000.00, android.R.drawable.ic_menu_directions, false));
                    pocketsList.add(new Pocket("Vehicle", 120000.00, 24000.00, android.R.drawable.ic_menu_directions, true));
                    
                    if (pocketAdapter != null) {
                        pocketAdapter.notifyDataSetChanged();
                    } else {
                        setupPocketsRecyclerView();
                    }
                });
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
                    Toast.makeText(PocketActivity.this, "Selected pocket: " + pocket.getName(), Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onViewDetailsClick(Pocket pocket, int position) {
                    // Navigate to pocket details activity
                    Intent intent = new Intent(PocketActivity.this, PocketDetailsActivity.class);
                    intent.putExtra("POCKET_NAME", pocket.getName());
                    intent.putExtra("POCKET_TARGET", pocket.getTargetAmount());
                    intent.putExtra("POCKET_CURRENT", pocket.getCurrentAmount());
                    intent.putExtra("POCKET_ICON", pocket.getIconResourceId());
                    intent.putExtra("POCKET_LOCKED", pocket.isLocked());
                    startActivity(intent);
                }
                
                @Override
                public void onAddFundsClick(Pocket pocket, int position) {
                    // Handle add funds button click
                    Toast.makeText(PocketActivity.this, "Add funds to: " + pocket.getName(), Toast.LENGTH_SHORT).show();
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