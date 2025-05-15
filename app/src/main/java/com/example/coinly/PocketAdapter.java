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

import java.util.List;

public class PocketAdapter extends RecyclerView.Adapter<PocketAdapter.ViewHolder> {
    
    private List<Pocket> pockets;
    private OnPocketClickListener listener;

    public interface OnPocketClickListener {
        void onPocketClick(Pocket pocket, int position);
    }

    public PocketAdapter(List<Pocket> pockets, OnPocketClickListener listener) {
        this.pockets = pockets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pocket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pocket pocket = pockets.get(position);
        
        holder.pocketName.setText(pocket.name);
        holder.pocketTarget.setText(String.format("Php %s", Util.amountFormatter(pocket.target)));
        holder.pocketProgress.setProgress(pocket.percent());
        holder.pocketProgressPercent.setText(pocket.percent() + "%");
        
        // Show lock icon if pocket is locked
        holder.pocketLockIcon.setVisibility(pocket.locked ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPocketClick(pocket, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pockets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pocketName;
        TextView pocketTarget;
        TextView pocketProgressPercent;
        ImageView pocketIcon;
        ImageView pocketLockIcon;
        ProgressBar pocketProgress;

        ViewHolder(View itemView) {
            super(itemView);
            pocketName = itemView.findViewById(R.id.pocketName);
            pocketTarget = itemView.findViewById(R.id.pocketTarget);
            pocketProgressPercent = itemView.findViewById(R.id.pocketProgressPercent);
            pocketIcon = itemView.findViewById(R.id.pocketIcon);
            pocketLockIcon = itemView.findViewById(R.id.pocketLockIcon);
            pocketProgress = itemView.findViewById(R.id.pocketProgress);
        }
    }
}