package com.example.coinly;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class CreatePinFragment extends Fragment {
    private Context ctx;

    private TextView[] pinDigits;
    private Button[] numberButtons;
    private Button btnNext;
    private ImageButton btnBackspace;
    private final StringBuilder pinBuilder = new StringBuilder();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        if (userId == null) {
            Toast.makeText(ctx, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view)
                    .navigate(CreatePinFragmentDirections.actionCreatePinFragmentToLoginFragment());
            return;
        }

        initViews(view);
        setupNumberPad();
        setupBackspaceButton();
        setupNextButton(view);
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

        btnBackspace = view.findViewById(R.id.btnBackspace);
        btnNext = view.findViewById(R.id.btnNext);
    }

    private void setupNumberPad() {
        for (int i = 0; i < 10; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> {
                if (pinBuilder.length() < 4) {
                    pinBuilder.append(number);
                    
                    updatePinDisplay();
                    
                    if (pinBuilder.length() == 4) {
                        btnNext.setEnabled(true);
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
                    btnNext.setEnabled(false);
                }
            }
        });
    }

    private void setupNextButton(View view) {
        btnNext.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(CreatePinFragmentDirections.actionCreatePinFragmentToConfirmPinFragment(
                            pinBuilder.toString()
                    ));
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
        animateViewWithDelay(view.findViewById(R.id.tvPinDescription), slideUp, delay * 2);
        animateViewWithDelay(view.findViewById(R.id.pinDisplay), slideUp, delay * 3);
        animateViewWithDelay(view.findViewById(R.id.keypad), slideUp, delay * 4);
        animateViewWithDelay(view.findViewById(R.id.btnNext), slideUp, delay * 5);
    }
    
    private void animateViewWithDelay(View view, Animation animation, int delay) {
        view.setVisibility(View.INVISIBLE);
        view.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }, delay);
    }
}