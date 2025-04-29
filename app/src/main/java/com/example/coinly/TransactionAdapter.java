package com.example.coinly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        holder.titleText.setText(transaction.getTitle());
        holder.dateText.setText(transaction.getDate());
        holder.amountText.setText(transaction.getFormattedAmount());
        holder.amountText.setTextColor(transaction.getAmountColor());
        holder.referenceText.setText("Ref Number: " + transaction.getRefNumber());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView dateText;
        TextView amountText;
        TextView referenceText;

        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.transactionTitle);
            dateText = itemView.findViewById(R.id.transactionDate);
            amountText = itemView.findViewById(R.id.transactionAmount);
            referenceText = itemView.findViewById(R.id.transactionReference);
        }
    }
}