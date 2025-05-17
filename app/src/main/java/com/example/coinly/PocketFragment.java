package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinly.db.Database;
import com.example.coinly.db.Pocket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class PocketFragment extends Fragment {
    private Context ctx;

    private RecyclerView pocketsRecyclerView;
    private PocketCardAdapter pocketAdapter;
    private List<Pocket> pocketsList;
    private FloatingActionButton addPocketButton;
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
        return inflater.inflate(R.layout.fragment_pocket, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        initViews(view);
        loadPocketData(view);
    }

    private void initViews(View view) {
        pocketsRecyclerView = view.findViewById(R.id.pocketsRecyclerView);
        addPocketButton = view.findViewById(R.id.addPocketButton);
        
        addPocketButton.setOnClickListener(v -> showCreatePocketDialog(view));
    }

    private void showCreatePocketDialog(View view) {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_pocket, null);
        
        TextInputEditText nameInput = dialogView.findViewById(R.id.pocketNameInput);
        TextInputEditText balanceInput = dialogView.findViewById(R.id.pocketBalanceInput);
        TextInputEditText targetInput = dialogView.findViewById(R.id.pocketTargetInput);
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ctx)
            .setView(dialogView)
            .setCancelable(true);
            
        AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.createButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String balanceStr = balanceInput.getText().toString().trim();
            String targetStr = targetInput.getText().toString().trim();
            
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
                Toast.makeText(ctx, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Pocket.add(
                    userId,
                    new Pocket()
                            .withName(name)
                            .withBalance(balance)
                            .withTarget(target)
                            .withLocked(false),
                    new Database.Data<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Toast.makeText(ctx, "Pocket created successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadPocketData(view);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (e instanceof IllegalArgumentException) {
                                Toast.makeText(ctx, "Insufficient funds", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(ctx, "Failed to create pocket", Toast.LENGTH_SHORT).show();
                            Log.e("PocketActivity", "Failed to create pocket", e);
                        }
                    }
            );
        });
        
        dialog.show();
    }
    
    private void loadPocketData(View view) {
        Pocket.get(userId, new Database.Data<List<Pocket>>() {
            @Override
            public void onSuccess(List<Pocket> data) {
                Log.i("Pocket", "Successfully retrieved pocket list");
                pocketsList = data;
                setupPocketsRecyclerView(view);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Pocket", "Tried to get user's pockets", e);
            }
        });
    }
    
    private void setupPocketsRecyclerView(View view) {
        if (pocketsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
            pocketsRecyclerView.setLayoutManager(layoutManager);
            
            pocketAdapter = new PocketCardAdapter(pocketsList, new PocketCardAdapter.OnPocketCardListener() {
                @Override
                public void onPocketClick(Pocket pocket, int position) {
                    // TODO: Navigate to pocket details activity
                    Toast.makeText(ctx, "Selected pocket: " + pocket.name, Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onViewDetailsClick(Pocket pocket, int position) {
                    Navigation.findNavController(view)
                            .navigate(PocketFragmentDirections.actionPocketFragmentToPocketDetailsFragment(pocket.id));
                }
                
                @Override
                public void onAddFundsClick(Pocket pocket, int position) {
                    Navigation.findNavController(view)
                            .navigate(PocketFragmentDirections.actionPocketFragmentToAddFundsFragment(pocket.id));
                }
            });
            
            pocketsRecyclerView.setAdapter(pocketAdapter);
            pocketsRecyclerView.setPadding(0, 0, 0, 160);
            pocketsRecyclerView.setClipToPadding(false);
        }
    }
}