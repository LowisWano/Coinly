package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
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

import android.widget.ProgressBar;

import com.example.coinly.db.Database;
import com.example.coinly.db.Pocket;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class PocketDetailsFragment extends Fragment {
    private Context ctx;
    
    private ImageView pocketLockIcon;
    private ImageView backButton;
    private TextView pocketName;
    private TextView currentAmount;
    private TextView targetAmount;
    private TextView progressPercent;
    private ProgressBar pocketProgress;
    private MaterialButton addFundsButton;
    private MaterialButton withdrawFundsButton;

    private String userId;
    private String pocketId;
    private double currAmount;
    private boolean isLocked;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pocket_details, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);
        pocketId = PocketDetailsFragmentArgs.fromBundle(getArguments()).getPocketId();

        if (userId == null || userId.isEmpty()) {
            Log.e("PocketDetails", "User ID is null");
            Toast.makeText(ctx, "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pocketId.isEmpty()) {
            Log.e("PocketDetails", "Pocket ID is null");
            Toast.makeText(ctx, "Pocket ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews(view);
        initButtons();
    }

    private void initViews(View view) {
        pocketLockIcon = view.findViewById(R.id.pocketLockIcon);
        backButton = view.findViewById(R.id.backButton);
        pocketName = view.findViewById(R.id.pocketName);
        currentAmount = view.findViewById(R.id.currentAmount);
        targetAmount = view.findViewById(R.id.targetAmount);
        progressPercent = view.findViewById(R.id.progressPercent);
        pocketProgress = view.findViewById(R.id.pocketProgress);
        addFundsButton = view.findViewById(R.id.addFundsButton);
        withdrawFundsButton = view.findViewById(R.id.withdrawFundsButton);
        
        backButton.setClickable(true);
        backButton.setFocusable(true);

        Pocket.getFrom(pocketId, new Database.Data<Pocket>() {
            @Override
            public void onSuccess(Pocket data) {
                isLocked = data.locked;

                setPocketDetails(data);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PocketDetails", "Tried to get pocket", e);
                back(view);
            }
        });
    }

    private void initButtons() {
        backButton.setOnClickListener(this::back);
        
        addFundsButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(PocketDetailsFragmentDirections.actionPocketDetailsFragmentToAddFundsFragment(pocketId));
        });
        
        withdrawFundsButton.setOnClickListener(v -> {
            if (isLocked) {
                Toast.makeText(ctx,
                    "Cannot withdraw from locked pocket", Toast.LENGTH_SHORT).show();
                return;
            }
            
            showWithdrawDialog();
        });
    }

    private void setPocketDetails(Pocket pocket) {
        currAmount = pocket.balance;

        pocketName.setText(pocket.name);
        targetAmount.setText("Php " + Util.amountFormatter(pocket.target));
        currentAmount.setText("Php " + Util.amountFormatter(pocket.balance));
        pocketProgress.setProgress(pocket.percent());
        progressPercent.setText(pocket.percent() + " %");
    }

    private void processWithdrawal(double amount) {
        if (amount <= 0) {
            Toast.makeText(ctx, "Please enter an amount greater than 0",
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (currAmount < 5) {
            Toast.makeText(ctx, "Cannot withdraw: balance too low or not yet loaded",
                Toast.LENGTH_LONG).show();
            return;
        }
        
        if (amount > currAmount) {
            Toast.makeText(ctx, "Insufficient funds in this pocket",
                Toast.LENGTH_SHORT).show();
            return;
        }

        Pocket.withdraw(userId, pocketId, amount, new Database.Data<Pocket>() {
            @Override
            public void onSuccess(Pocket data) {
                setPocketDetails(data);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PocketDetails", "Failed to withdraw funds", e);
                Toast.makeText(ctx, "Failed to withdraw funds", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWithdrawDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_withdraw, null);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        
        new MaterialAlertDialogBuilder(ctx)
            .setTitle("Withdraw Funds")
            .setView(dialogView)
            .setPositiveButton("Withdraw", (dialog, which) -> {
                String amountStr = amountInput.getText().toString();
                if (!amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        processWithdrawal(amount);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ctx,
                            "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx,
                        "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}