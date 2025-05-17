package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.coinly.db.User;
import com.example.coinly.db.Database;

public class ConfirmPinFragment extends Fragment {
    private Context ctx;

    private TextView[] pinDigits;
    private TextView tvPinError;
    private Button[] numberButtons;
    private Button btnConfirm;
    private ImageButton btnBackspace;
    private StringBuilder pinBuilder = new StringBuilder();
    private String originalPin;
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
        return inflater.inflate(R.layout.fragment_confirm_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        originalPin = ConfirmPinFragmentArgs.fromBundle(getArguments()).getPin();

        if (userId == null) {
            Toast.makeText(ctx, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view)
                    .navigate(ConfirmPinFragmentDirections.actionConfirmPinFragmentToLoginFragment());
        }

        initViews(view);
        setupNumberPad();
        setupBackspaceButton();
        setupConfirmButton(view);
        animateUI(view);
    }

    private void initViews(View view) {
        pinDigits = new TextView[4];
        pinDigits[0] = view.findViewById(R.id.pinDigit1);
        pinDigits[1] = view.findViewById(R.id.pinDigit2);
        pinDigits[2] = view.findViewById(R.id.pinDigit3);
        pinDigits[3] = view.findViewById(R.id.pinDigit4);
        
        tvPinError = view.findViewById(R.id.tvPinError);

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

        btnBackspace = view.findViewById(R.id.btnBackspace);
        btnConfirm = view.findViewById(R.id.btnConfirm);
    }

    private void setupNumberPad() {
        for (int i = 0; i < 10; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> {
                if (pinBuilder.length() < 4) {
                    pinBuilder.append(number);
                    tvPinError.setVisibility(View.GONE);

                    updatePinDisplay();

                    if (pinBuilder.length() == 4) {
                        btnConfirm.setEnabled(true);
                    }
                }
            });
        }
    }

    private void setupBackspaceButton() {
        btnBackspace.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                
                updatePinDisplay();
                
                if (pinBuilder.length() < 4) {
                    btnConfirm.setEnabled(false);
                }
            }
        });
    }

    private void setupConfirmButton(View view) {
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        btnConfirm.setOnClickListener(v -> {
            if (originalPin != null && originalPin.equals(pinBuilder.toString())) {
                btnConfirm.setEnabled(false);
                loadingProgressBar.setVisibility(View.VISIBLE);

                User.Credentials credentials = new User.Credentials()
                    .withPin(originalPin.toCharArray());
                
                User.setPin(userId, credentials, new Database.Data<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        loadingProgressBar.setVisibility(View.GONE);
                        
                        Toast.makeText(ctx, R.string.pin_success, Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(() -> {
                            Navigation.findNavController(view)
                                    .navigate(ConfirmPinFragmentDirections.actionConfirmPinFragmentToHomeFragment());
                        }, 1500);
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        loadingProgressBar.setVisibility(View.GONE);
                        
                        btnConfirm.setEnabled(true);
                        Toast.makeText(ctx, "Failed to save PIN", Toast.LENGTH_SHORT).show();

                        Log.e("ConfirmPin", "Tried to set user's pin", e);
                    }
                });
            } else {
                tvPinError.setVisibility(View.VISIBLE);
                
                pinBuilder.setLength(0);
                updatePinDisplay();
                btnConfirm.setEnabled(false);
                
                Animation shake = AnimationUtils.loadAnimation(ctx, R.anim.shake);
                view.findViewById(R.id.pinDisplay).startAnimation(shake);
            }
        });
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
        animateViewWithDelay(view.findViewById(R.id.tvPinConfirmDescription), slideUp, delay * 2);
        animateViewWithDelay(view.findViewById(R.id.pinDisplay), slideUp, delay * 3);
        animateViewWithDelay(view.findViewById(R.id.keypad), slideUp, delay * 4);
        animateViewWithDelay(view.findViewById(R.id.btnConfirm), slideUp, delay * 5);
    }
    
    private void animateViewWithDelay(View view, Animation animation, int delay) {
        view.setVisibility(View.INVISIBLE);
        view.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }, delay);
    }
}