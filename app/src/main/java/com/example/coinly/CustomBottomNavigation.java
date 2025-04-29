package com.example.coinly;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomBottomNavigation extends RelativeLayout {
    private LinearLayout homeButton;
    private LinearLayout walletButton;
    private LinearLayout qrButton;
    private LinearLayout transactionsButton;
    private LinearLayout profileButton;
    private OnNavigationItemSelectedListener listener;

    public interface OnNavigationItemSelectedListener {
        void onItemSelected(int itemId);
    }

    public CustomBottomNavigation(Context context) {
        super(context);
        init();
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_navigation, this, true);
        
        homeButton = findViewById(R.id.homeButton);
        walletButton = findViewById(R.id.walletButton);
        qrButton = findViewById(R.id.qrButton);
        transactionsButton = findViewById(R.id.transactionsButton);
        profileButton = findViewById(R.id.profileButton);

        setupClickListeners();
    }

    private void setupClickListeners() {
        homeButton.setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected(R.id.nav_home);
        });

        walletButton.setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected(R.id.nav_wallet);
        });

        qrButton.setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected(R.id.nav_qr);
        });

        transactionsButton.setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected(R.id.nav_transactions);
        });

        profileButton.setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected(R.id.nav_profile);
        });
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedItem(int itemId) {
        // Reset all to default state
        homeButton.setSelected(false);
        walletButton.setSelected(false);
        transactionsButton.setSelected(false);
        profileButton.setSelected(false);

        // Set selected state
        if (itemId == R.id.nav_home) {
            homeButton.setSelected(true);
        } else if (itemId == R.id.nav_wallet) {
            walletButton.setSelected(true);
        } else if (itemId == R.id.nav_transactions) {
            transactionsButton.setSelected(true);
        } else if (itemId == R.id.nav_profile) {
            profileButton.setSelected(true);
        }
    }
}