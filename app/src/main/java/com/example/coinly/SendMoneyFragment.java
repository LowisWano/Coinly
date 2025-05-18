package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

public class SendMoneyFragment extends Fragment {
    private Context ctx;

    private ImageButton backButton;
    private LinearLayout userCard;
    private EditText editTextNumber, editTextAmount;
    private TextView userName, phoneNumber, enterAmount;
    private Button nextButton;

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
        return inflater.inflate(R.layout.fragment_send_money, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        initViews(view);
        initButtons();
        loadUserData();
        loadFromQrScanned();
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        userCard = view.findViewById(R.id.userCard);
        editTextNumber = view.findViewById(R.id.editTextNumber);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        userName = view.findViewById(R.id.userName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        enterAmount = view.findViewById(R.id.enterAmount);
        nextButton = view.findViewById(R.id.nextButton);

        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = editTextNumber.getText().toString().trim();
                String amount = editTextAmount.getText().toString().trim();
                nextButton.setEnabled(!number.isEmpty() && !amount.isEmpty());
            }
        };

        editTextNumber.addTextChangedListener(formWatcher);
        editTextAmount.addTextChangedListener(formWatcher);
    }

    private void initButtons() {
        backButton.setOnClickListener(this::back);

        userCard.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(SendMoneyFragmentDirections.actionSendMoneyFragmentToMyQRFragment());
        });

        nextButton.setOnClickListener(v -> {
            String number = editTextNumber.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();

            if (number.isEmpty() || amount.isEmpty()) {
                Toast.makeText(ctx, "Please enter both number and amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Double.parseDouble(amount) <= 0) {
                Toast.makeText(ctx, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Navigation.findNavController(v)
                    .navigate(SendMoneyFragmentDirections.actionSendMoneyFragmentToSendMoneyConfirmFragment(
                            number,
                            amount
                    ));
        });
    }

    private void loadUserData() {
        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>(){
                    @Override
                    public void onSuccess(User.Details data) {
                        userName.setText(data.fullName.formatted());
                        phoneNumber.setText(data.phoneNumber);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("SendMoney", "Tried to send money", e);
                        back(requireView());
                    }
                }
        );

        User.get(
                userId,
                User.Wallet.class,
                new Database.Data<User.Wallet>(){
                    @Override
                    public void onSuccess(User.Wallet data) {
                        enterAmount.setText(String.format("Enter Amount (Php %s)", Util.amountFormatter(data.balance)));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Error", "Error", e);
                        back(requireView());
                    }
                }
        );
    }

    private void loadFromQrScanned() {
        String qrData = SendMoneyFragmentArgs.fromBundle(getArguments()).getQrData();

        if (!qrData.isEmpty()) {
            editTextNumber.setText(qrData);
        }
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}
