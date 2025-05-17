package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coinly.db.Pocket;
import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;
import com.example.coinly.db.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
    private Context ctx;

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
    private String userId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", "");

        initViews(view);
        setupHideBalanceButton();
        fetchUserBalance();
        loadPocketData(view);
        loadTransactionData();
        setupSeeAllButtons(view);
    }

    private void initViews(View view) {
        pocketsRecyclerView = view.findViewById(R.id.pocketsRecyclerView);
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        balanceAmount = view.findViewById(R.id.balanceAmount);
        hideBalanceButton = view.findViewById(R.id.hideBalanceButton);

        balanceAmount.setText(actualBalance);

        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        ((TextView) view.findViewById(R.id.userName)).setText(data.fullName.formatted());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Wallet", "Tried to get user's details", e);
                    }
                }
        );
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

    private void loadPocketData(View view) {
        Pocket.get(userId, new Database.Data<List<Pocket>>() {
            @Override
            public void onSuccess(List<Pocket> data) {
                pocketsList = data;
                if (pocketsList == null) {
                    pocketsList = new ArrayList<>();
                }
                setupPocketsRecyclerView(view);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load pockets", e);
                Toast.makeText(ctx, "Failed to load pockets", Toast.LENGTH_SHORT).show();
                pocketsList = new ArrayList<>();
                setupPocketsRecyclerView(view);
            }
        });
    }

    private void loadTransactionData() {
        Transaction.get(userId, new Database.Data<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> data) {
                transactionsList = data;
                setupTransactionsRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Wallet", "Tried to get user's transactions", e);
            }
        });
    }

    private void setupPocketsRecyclerView(View view) {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
            pocketsRecyclerView.setLayoutManager(layoutManager);

            pocketAdapter = new PocketAdapter(pocketsList, (pocket, position) -> {
                Navigation.findNavController(view)
                        .navigate(WalletFragmentDirections.actionWalletFragmentToPocketDetailsFragment(pocket.id));
            });

            pocketsRecyclerView.setAdapter(pocketAdapter);
        }
    }

    private void setupTransactionsRecyclerView() {
        if (transactionsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
            transactionAdapter = new TransactionAdapter(transactionsList);

            transactionsRecyclerView.setLayoutManager(layoutManager);
            transactionsRecyclerView.setAdapter(transactionAdapter);
            transactionsRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void setupSeeAllButtons(View view) {
        TextView seeAllPockets = view.findViewById(R.id.seeAllPockets);
        if (seeAllPockets != null) {
            seeAllPockets.setOnClickListener(v -> {
                Navigation.findNavController(view)
                        .navigate(WalletFragmentDirections.actionWalletFragmentToPocketFragment());
            });
        }
        
        TextView seeAllTransactions = view.findViewById(R.id.seeAllTransactions);
        if (seeAllTransactions != null) {
            seeAllTransactions.setOnClickListener(v -> {
                Navigation.findNavController(view)
                        .navigate(WalletFragmentDirections.actionWalletFragmentToTransactionsFragment());
            });
        }
    }

    private void fetchUserBalance() {
        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>() {
                    @Override
                    public void onSuccess(User.Wallet data) {
                        actualBalance = String.format("Php %s", Util.amountFormatter(data.balance));

                        if (!isBalanceHidden) {
                            balanceAmount.setText(actualBalance);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Wallet", "Tried to get user's balance", e);
                    }
                }
        );
    }
}