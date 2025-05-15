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

import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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