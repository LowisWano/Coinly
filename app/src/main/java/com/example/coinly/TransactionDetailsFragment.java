package com.example.coinly;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.Transaction;

public class TransactionDetailsFragment extends Fragment {
    TextView amountText, titleText, dateText, senderText, receiverText, referenceText;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_transaction, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        displayTransactionDetails();
    }

    private void initViews(View view) {
        amountText = view.findViewById(R.id.transactionAmount);
        titleText = view.findViewById(R.id.transactionTitle);
        dateText = view.findViewById(R.id.transactionDate);
        senderText = view.findViewById(R.id.senderText);
        receiverText = view.findViewById(R.id.receiverText);
        referenceText = view.findViewById(R.id.referenceText);
    }

    private void displayTransactionDetails() {
        String id = TransactionDetailsFragmentArgs.fromBundle(getArguments()).getTransactionId();

        Transaction.getFrom(id, new Database.Data<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                amountText.setText(data.formattedAmount());
                amountText.setTextColor(data.amountColor());
                titleText.setText(data.name);
                dateText.setText(Util.dateFormatter(data.date));
                senderText.setText(data.senderId);
                receiverText.setText(data.receiveId);
                referenceText.setText(data.id);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TransactionDetails", "Tried to get transaction", e);
                back(requireView());
            }
        });
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}