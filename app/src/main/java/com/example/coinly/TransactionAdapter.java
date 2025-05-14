package com.example.coinly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinly.db.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

        if (transaction == null) {
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        
        holder.titleText.setText(transaction.name);
        holder.dateText.setText(formatter.format(transaction.date.getTime()));
        holder.amountText.setText(transaction.formattedAmount());
        holder.amountText.setTextColor(transaction.amountColor());
        holder.referenceText.setText("Ref Number: " + transaction.id);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransactionDetailsActivity.class);
            intent.putExtra(TransactionDetailsActivity.EXTRA_TITLE, transaction.name);
            intent.putExtra(TransactionDetailsActivity.EXTRA_DATE, transaction.date);
            intent.putExtra(TransactionDetailsActivity.EXTRA_AMOUNT, transaction.amount);
            intent.putExtra(TransactionDetailsActivity.EXTRA_REFERENCE, transaction.id);
            intent.putExtra(TransactionDetailsActivity.EXTRA_SENDER, transaction.senderId);
            intent.putExtra(TransactionDetailsActivity.EXTRA_RECEIVER, transaction.receiveId);
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