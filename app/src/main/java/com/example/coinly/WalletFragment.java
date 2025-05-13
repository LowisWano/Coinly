package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {

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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupHideBalanceButton();
            loadPocketData();
            loadTransactionData();
            setupPocketsRecyclerView();
            setupTransactionsRecyclerView();
            setupSeeAllButtons(view);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing wallet activity", e);
            Toast.makeText(requireContext(), "Error opening wallet: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews(@NonNull View view) {
        pocketsRecyclerView = view.findViewById(R.id.pocketsRecyclerView);
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        balanceAmount = view.findViewById(R.id.balanceAmount);
        hideBalanceButton = view.findViewById(R.id.hideBalanceButton);

        // Set user name dynamically (would come from user profile in a real app)
        TextView userNameTextView = view.findViewById(R.id.userName);
        userNameTextView.setText("John Doe");
        
        // Set initial balance
        balanceAmount.setText(actualBalance);
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
        // In a real app, this data would come from a database or API
        transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction("Netflix Subscription", "April 27, 2025", -300.00));
        transactionsList.add(new Transaction("Youtube Premium", "April 26, 2025", -239.00));
        transactionsList.add(new Transaction("Deposit from 24 Chicken", "April 23, 2025", 45.00));
    }

    private void setupPocketsRecyclerView() {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            pocketsRecyclerView.setLayoutManager(layoutManager);
            
            pocketAdapter = new PocketAdapter(pocketsList, (pocket, position) -> {
                // Handle pocket item click
                Toast.makeText(requireContext(), pocket.getName() + " pocket clicked", Toast.LENGTH_SHORT).show();
                // Navigate to pocket details in a real app
            });
            
            pocketsRecyclerView.setAdapter(pocketAdapter);
        }
    }

    private void setupTransactionsRecyclerView() {
        if (transactionsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            transactionsRecyclerView.setLayoutManager(layoutManager);
            
            transactionAdapter = new TransactionAdapter(transactionsList);
            transactionsRecyclerView.setAdapter(transactionAdapter);
            
            // Limit height by showing only a few items
            transactionsRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void setupSeeAllButtons(@NonNull View view) {
        // Set up "See All" buttons
        TextView seeAllPockets = view.findViewById(R.id.seeAllPockets);
        if (seeAllPockets != null) {
            seeAllPockets.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "All Pockets coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        TextView seeAllTransactions = view.findViewById(R.id.seeAllTransactions);
        if (seeAllTransactions != null) {
            seeAllTransactions.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), TransactionHistoryFragment.class);
                startActivity(intent);
            });
        }
    }
}