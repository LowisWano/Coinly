package com.example.coinly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private Context context;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context)
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransactionDetailsActivity.class);
            intent.putExtra(TransactionDetailsActivity.EXTRA_TITLE, transaction.getTitle());
            intent.putExtra(TransactionDetailsActivity.EXTRA_DATE, transaction.getDate());
            intent.putExtra(TransactionDetailsActivity.EXTRA_AMOUNT, transaction.getAmount());
            intent.putExtra(TransactionDetailsActivity.EXTRA_REFERENCE, transaction.getRefNumber());
            intent.putExtra(TransactionDetailsActivity.EXTRA_SENDER, transaction.getSender());
            intent.putExtra(TransactionDetailsActivity.EXTRA_RECEIVER, transaction.getReceiver());
            context.startActivity(intent);
        });
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