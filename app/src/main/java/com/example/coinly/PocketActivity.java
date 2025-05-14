package com.example.coinly;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

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
        // In a real app, this data would come from a database or API
        pocketsList = new ArrayList<>();
        
        // Using our utility class to get appropriate icons for each category
        pocketsList.add(new Pocket("Home", 50000.00, 43000.00, android.R.drawable.ic_menu_directions, false));
        pocketsList.add(new Pocket("Vehicle", 120000.00, 24000.00, android.R.drawable.ic_menu_directions, true));
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