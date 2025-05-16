package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;

public class ChangePinFragment extends Fragment {
    private Context ctx;

    private TextView[] pinDigits;
    private Button[] numberButtons;
    private Button btnNext, btnCancel;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private ProgressBar loadingProgressBar;
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
        return inflater.inflate(R.layout.fragment_change_pin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        initViews(view);
        setupNumberPad();
        setupBackspaceButton();
        setupNextButton();
        animateUI(view);
    }

    private void initViews(View view) {
        pinDigits = new TextView[4];
        pinDigits[0] = view.findViewById(R.id.pinDigit1);
        pinDigits[1] = view.findViewById(R.id.pinDigit2);
        pinDigits[2] = view.findViewById(R.id.pinDigit3);
        pinDigits[3] = view.findViewById(R.id.pinDigit4);

        numberButtons = new Button[10];
        numberButtons[0] = view.findViewById(R.id.btn0);
        numberButtons[1] = view.findViewById(R.id.btn1);
        numberButtons[2] = view.findViewById(R.id.btn2);
        numberButtons[3] = view.findViewById(R.id.btn3);
        numberButtons[4] = view.findViewById(R.id.btn4);
        numberButtons[5] = view.findViewById(R.id.btn5);
        numberButtons[6] = view.findViewById(R.id.btn6);
        numberButtons[7] = view.findViewById(R.id.btn7);
        numberButtons[8] = view.findViewById(R.id.btn8);
        numberButtons[9] = view.findViewById(R.id.btn9);

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        btnBackspace = view.findViewById(R.id.btnBackspace);
        btnNext = view.findViewById(R.id.btnNext);

        view.findViewById(R.id.btnCancel).setOnClickListener(this::back);
    }

    private void setupNumberPad() {
        for (int i = 0; i < 10; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> {
                if (pinBuilder.length() < 4) {
                    pinBuilder.append(number);
                    updatePinDisplay();
                    btnNext.setEnabled(pinBuilder.length() == 4);
                }
            });
        }
    }

    private void setupBackspaceButton() {
        btnBackspace.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                updatePinDisplay();
                btnNext.setEnabled(false);
            }
        });
    }

    private void setupNextButton() {
        btnNext.setOnClickListener(v -> {
            char[] enteredPin = pinBuilder.toString().toCharArray();

            loadingProgressBar.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);

            Database.db().collection("users").document(userId).get()
                    .addOnSuccessListener(snapshot -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        btnNext.setEnabled(true);

                        if (!snapshot.exists()) {
                            Toast.makeText(ctx, "User not found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String storedPin = snapshot.getString("credentials.pin");
                        if (storedPin == null || !storedPin.equals(pinBuilder.toString())) {
                            Toast.makeText(ctx, "Incorrect old PIN.", Toast.LENGTH_SHORT).show();
                            clearPinInput();
                        } else {
                            Navigation.findNavController(v)
                                    .navigate(ChangePinFragmentDirections
                                            .actionChangePinFragmentToCreatePinFragment());
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        btnNext.setEnabled(true);
                        Toast.makeText(ctx, "Error verifying PIN: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void clearPinInput() {
        pinBuilder.setLength(0);
        updatePinDisplay();
    }

    private void updatePinDisplay() {
        for (int i = 0; i < 4; i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_empty);
        }
        for (int i = 0; i < pinBuilder.length(); i++) {
            pinDigits[i].setBackgroundResource(R.drawable.pin_filled);
        }
    }

    private void animateUI(View view) {
        Animation slideUp = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        int delay = 100;
        animateViewWithDelay(view.findViewById(R.id.tvPinDescription), slideUp, delay * 2);
        animateViewWithDelay(view.findViewById(R.id.pinDisplay), slideUp, delay * 3);
        animateViewWithDelay(view.findViewById(R.id.keypad), slideUp, delay * 4);
        animateViewWithDelay(view.findViewById(R.id.btnNext), slideUp, delay * 5);
        animateViewWithDelay(view.findViewById(R.id.btnCancel), slideUp, delay * 5);
    }

    private void animateViewWithDelay(View view, Animation animation, int delay) {
        view.setVisibility(View.INVISIBLE);
        view.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }, delay);
    }

    private void back(View view) {
        Log.d("ChangePinFragment", "Back button clicked");
        Navigation.findNavController(view).navigateUp();
    }
}