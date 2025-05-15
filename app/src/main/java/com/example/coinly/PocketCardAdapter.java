package com.example.coinly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinly.db.Pocket;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PocketCardAdapter extends RecyclerView.Adapter<PocketCardAdapter.ViewHolder> {
    
    private List<Pocket> pockets;
    private OnPocketCardListener listener;

    public interface OnPocketCardListener {
        void onPocketClick(Pocket pocket, int position);
        void onViewDetailsClick(Pocket pocket, int position);
        void onAddFundsClick(Pocket pocket, int position);
    }

    public PocketCardAdapter(List<Pocket> pockets, OnPocketCardListener listener) {
        this.pockets = pockets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pocket_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Pocket pocket = pockets.get(position);
        final int adapterPosition = position;
        
        // Set pocket name and icon
        holder.pocketName.setText(pocket.name);

        // Set description (this is a new field in pocket_card.xml)
        // In a real app, you would have this data in your Pocket model
        holder.pocketDescription.setText("Saving for " + pocket.name.toLowerCase() + " expenses");
        
        holder.pocketTarget.setText("Php " + Util.amountFormatter(pocket.target));
        holder.pocketBalance.setText("Php " + Util.amountFormatter(pocket.balance));
        
        // Set progress percentage and progress bar
        holder.pocketProgress.setProgress(pocket.percent());
        holder.pocketProgressPercent.setText(pocket.percent() + "%");
        
        // Show lock icon if pocket is locked
        holder.pocketLockIcon.setVisibility(pocket.locked ? View.VISIBLE : View.GONE);
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPocketClick(pocket, adapterPosition);
            }
        });
        
        holder.viewDetailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(pocket, adapterPosition);
            }
        });
        
        holder.addFundsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddFundsClick(pocket, adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pockets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pocketName;
        TextView pocketDescription;
        TextView pocketTarget;
        TextView pocketBalance;
        TextView pocketProgressPercent;
        ImageView pocketIcon;
        ImageView pocketLockIcon;
        ProgressBar pocketProgress;
        MaterialButton viewDetailsButton;
        MaterialButton addFundsButton;

        ViewHolder(View itemView) {
            super(itemView);
            pocketName = itemView.findViewById(R.id.pocketName);
            pocketDescription = itemView.findViewById(R.id.pocketDescription);
            pocketTarget = itemView.findViewById(R.id.pocketTarget);
            pocketBalance = itemView.findViewById(R.id.pocketBalance);
            pocketProgressPercent = itemView.findViewById(R.id.pocketProgressPercent);
            pocketIcon = itemView.findViewById(R.id.pocketIcon);
            pocketLockIcon = itemView.findViewById(R.id.pocketLockIcon);
            pocketProgress = itemView.findViewById(R.id.pocketProgress);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            addFundsButton = itemView.findViewById(R.id.addFundsButton);
        }
    }
}
