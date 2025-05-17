package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.Pocket;
import com.example.coinly.db.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

public class AddFundsFragment extends Fragment {
    private Context ctx;

    private ImageView backButton;
    private TextView availableBalance;
    private TextView balanceAfter;

    private TextView pocketBalanceAfter;
    private TextInputEditText amountInput;
    private MaterialButton confirmAddFundsButton;

    private String pocketId;
    private String userId;
    private double walletBalance;
    private double currentAmount;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_funds, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);
        pocketId = AddFundsFragmentArgs.fromBundle(getArguments()).getPocketId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(ctx, "User ID is missing", Toast.LENGTH_SHORT).show();
            back(view);
            return;
        }

        if (pocketId.isEmpty()) {
            Toast.makeText(ctx, "Pocket ID is missing", Toast.LENGTH_SHORT).show();
            back(view);
            return;
        }

        initViews(view);
        initButtons();
        loadPocketBalance(view);
        loadWalletBalance(view);
        setupTextChangeListener();
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        availableBalance = view.findViewById(R.id.availableBalance);
        balanceAfter = view.findViewById(R.id.balanceAfter);

        pocketBalanceAfter = view.findViewById(R.id.pocketBalanceAfter);
        amountInput = view.findViewById(R.id.amountInput);
        confirmAddFundsButton = view.findViewById(R.id.confirmAddFundsButton);
    }

    private void initButtons() {
        confirmAddFundsButton.setOnClickListener(this::addFunds);
        backButton.setOnClickListener(this::back);
    }

    private void loadPocketBalance(View view) {
        Pocket.getFrom(
                pocketId,
                new Database.Data<Pocket>() {
                    @Override
                    public void onSuccess(Pocket data) {
                        currentAmount = data.balance;

                        pocketBalanceAfter.setText("Php " + Util.amountFormatter(currentAmount));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ctx, "Failed to get pocket data", Toast.LENGTH_SHORT).show();
                        back(view);
                    }
                }
        );
    }

    private void loadWalletBalance(View view) {
        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>() {
                    @Override
                    public void onSuccess(User.Wallet data) {
                        walletBalance = data.balance;

                        availableBalance.setText("Php " + Util.amountFormatter(walletBalance));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ctx, "Failed to get user data", Toast.LENGTH_SHORT).show();
                        back(view);
                    }
                }
        );
    }

    private void setupTextChangeListener() {
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
    
    private void addFunds(View view) {
        try {
            if (amountInput.getText() != null && !amountInput.getText().toString().isEmpty()) {
                double amountToAdd = Double.parseDouble(amountInput.getText().toString());
                
                if (amountToAdd <= 0) {
                    Toast.makeText(ctx, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amountToAdd > walletBalance) {
                    Toast.makeText(ctx, "Insufficient wallet balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pocket.deposit(
                        userId,
                        pocketId,
                        amountToAdd,
                        new Database.Data<Pocket>() {
                            @Override
                            public void onSuccess(Pocket data) {
                                Toast.makeText(ctx, "Successfully added the funds", Toast.LENGTH_SHORT).show();
                                back(view);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(ctx, "Failed to add funds", Toast.LENGTH_SHORT).show();
                                Log.e("AddFundsFragment", "Failed to add funds", e);
                            }
                        }
                );
            } else {
                Toast.makeText(ctx, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(ctx, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}
