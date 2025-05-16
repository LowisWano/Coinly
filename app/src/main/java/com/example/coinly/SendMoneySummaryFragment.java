package com.example.coinly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SendMoneySummaryFragment extends Fragment {
    private TextView userNameText, transactionNumberText, transactionAmountText, totalAmountText, referenceNumberText, transactionDateText;

    private String referenceNumber;
    private String recipientName;
    private String recipientPhone;
    private String date;
    private double amount;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money_summary, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SendMoneySummaryFragmentArgs args = SendMoneySummaryFragmentArgs.fromBundle(getArguments());

        referenceNumber = args.getReferenceNumber();
        recipientName = args.getName();
        recipientPhone = args.getPhoneNumber();
        date = args.getDate();
        amount = Double.parseDouble(args.getAmount());

        initViews(view);
        loadData();
    }

    private void initViews(View view) {
        userNameText = view.findViewById(R.id.userName);
        transactionNumberText = view.findViewById(R.id.transactionNumber);
        transactionAmountText = view.findViewById(R.id.transactionAmount);
        totalAmountText = view.findViewById(R.id.totalAmount);
        referenceNumberText = view.findViewById(R.id.referenceNumber);
        transactionDateText = view.findViewById(R.id.transactionDate);

        view.findViewById(R.id.backButton).setOnClickListener(this::back);
    }

    private void loadData() {
        transactionNumberText.setText(recipientPhone);
        userNameText.setText(recipientName);
        transactionAmountText.setText("Php " + Util.amountFormatter(amount));
        totalAmountText.setText("Php " + Util.amountFormatter(amount));
        referenceNumberText.setText("Ref Number:" + referenceNumber);
        transactionDateText.setText(date);
    }

    private void back(View view) {
        Navigation.findNavController(view)
                .navigate(SendMoneySummaryFragmentDirections.actionSendMoneySummaryFragmentToQrscannerFragment());
    }
}
