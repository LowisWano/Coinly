package com.example.coinly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.Transaction;
import com.example.coinly.db.User;

import java.util.GregorianCalendar;
import java.util.Locale;

public class SendMoneyConfirmFragment extends Fragment {
    private static final String TAG = "SendMoneyConfirm";

    private Context ctx;

    private ImageView swipeHandle;
    private FrameLayout swipeContainer;
    private boolean transactionDone = false;
    private TextView userNameText, recipientNumberText, amountText, totalAmountText, transactionDateText, swipeText;

    private String userId;
    private String recipientName;
    private String recipientPhone;
    private double amount;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money_confirm, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        SendMoneyConfirmFragmentArgs args = SendMoneyConfirmFragmentArgs.fromBundle(getArguments());

        recipientPhone = args.getPhoneNumber();
        amount = Double.parseDouble(args.getAmount());

        initViews(view);
        loadData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews(View view) {
        swipeHandle = view.findViewById(R.id.swipe_handle);
        swipeContainer = view.findViewById(R.id.swipe_container);
        swipeText = view.findViewById(R.id.swipe_text);

        userNameText = view.findViewById(R.id.userName);
        recipientNumberText = view.findViewById(R.id.recipientNumber);
        amountText = view.findViewById(R.id.amount);
        totalAmountText = view.findViewById(R.id.totalAmount);
        transactionDateText = view.findViewById(R.id.transactionDate);

        view.findViewById(R.id.backButton).setOnClickListener(this::back);

        swipeHandle.setOnTouchListener(new View.OnTouchListener() {
            float downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float containerWidth = swipeContainer.getWidth();
                float handleWidth = swipeHandle.getWidth();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float translationX = moveX - downX;
                        if (translationX >= 0 && translationX <= containerWidth - handleWidth) {
                            swipeHandle.setTranslationX(translationX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (swipeHandle.getTranslationX() >= (containerWidth - handleWidth - 20) && !transactionDone) {
                            swipeText.setText("Confirmed");
                            swipeHandle.setEnabled(false);
                            swipeHandle.setTranslationX(containerWidth - handleWidth);
                            performTransaction();
                        } else {
                            swipeHandle.animate().translationX(0).setDuration(200).start();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void loadData() {
        recipientNumberText.setText(recipientPhone);
        amountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        totalAmountText.setText(String.format(Locale.getDefault(), "Php %.2f", amount));
        transactionDateText.setText(Util.dateFormatter(new GregorianCalendar()));

        User.getFromPhoneNumber(recipientPhone, User.Details.class, new Database.Data<User.Details>() {
            @Override
            public void onSuccess(User.Details data) {
                recipientName = data.fullName.formatted();

                userNameText.setText(recipientName);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SendMoneyConfirm", "Tried to get user's details", e);
                back(requireView());
            }
        });
    }

    private void performTransaction() {
        transactionDone = true;

        User.sendMoney(userId, recipientPhone, amount, new Database.Data<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                Toast.makeText(ctx, "Transaction successful!", Toast.LENGTH_SHORT).show();

                Navigation.findNavController(requireView())
                        .navigate(SendMoneyConfirmFragmentDirections.actionSendMoneyConfirmFragmentToSendMoneySummaryFragment(
                                data.id,
                                recipientName,
                                recipientPhone,
                                Util.dateFormatter(data.date),
                                Double.toString(data.amount)
                        ));
            }

            @Override
            public void onFailure(Exception e) {
                transactionDone = false;
                Log.e(TAG, "Tried to create transaction", e);
            }
        });
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}
