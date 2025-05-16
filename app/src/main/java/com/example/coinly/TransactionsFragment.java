package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;
import com.example.coinly.db.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {
    private Context ctx;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private EditText searchEditText;
    private ImageButton filterButton;
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
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", "");

        initViews(view);
        loadTransactions(view);
        setupSearch();
        setupFilter();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.transactionsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.filterButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.viewAllButton).setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(TransactionsFragmentDirections.actionTransactionsFragmentToRequestTransactionHistoryFragment());
        });

        ((TextView) view.findViewById(R.id.referralCodeText)).setText(userId);

        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        ((TextView) view.findViewById(R.id.fullNameText)).setText(data.fullName.formatted());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TransactionHistory", "Tried to get user's details", e);
                    }
                }
        );

        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>() {
                    @Override
                    public void onSuccess(User.Wallet data) {
                        ((TextView) view.findViewById(R.id.balanceText)).setText(Util.amountFormatter(data.balance));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TransactionHistory", "Tried to get user's wallet", e);
                    }
                }
        );
    }

    private void loadTransactions(View view) {
        Transaction.get(userId, new Database.Data<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> data) {
                allTransactions = data;

                filteredTransactions.clear();
                filteredTransactions.addAll(allTransactions);

                ((TextView) view.findViewById(R.id.transactionCount)).setText(
                        String.format("Last 7 days (%d)", allTransactions.size())
                );

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TransactionHistory", "Tried to get user's transactions", e);
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilter() {
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.dialog_filter_transactions);

        dialog.findViewById(R.id.filterAll).setOnClickListener(v -> {
            filteredTransactions.clear();
            filteredTransactions.addAll(allTransactions);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterDeposit).setOnClickListener(v -> {
            filterByType(true);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.filterExpenses).setOnClickListener(v -> {
            filterByType(false);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterTransactions(String query) {
        filteredTransactions.clear();
        if (query.isEmpty()) {
            filteredTransactions.addAll(allTransactions);
        } else {
            for (Transaction transaction : allTransactions) {
                if (transaction.name.toLowerCase().contains(query.toLowerCase()) ||
                        transaction.date.toString().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add(transaction);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByType(boolean isCoinsAdded) {
        filteredTransactions.clear();
        for (Transaction transaction : allTransactions) {
            if ((isCoinsAdded && transaction.type == Transaction.Type.Receive) ||
                    (!isCoinsAdded && transaction.type == Transaction.Type.Transfer)) {
                filteredTransactions.add(transaction);
            }
        }
        adapter.notifyDataSetChanged();
    }
}