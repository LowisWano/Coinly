package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SendMoneyConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_confirm);

        // Reference views
        ImageView swipeHandle = findViewById(R.id.swipe_handle);
        FrameLayout swipeContainer = findViewById(R.id.swipe_container);
        TextView swipeText = findViewById(R.id.swipe_text);

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
                        if (swipeHandle.getTranslationX() >= (containerWidth - handleWidth - 20)) {
                            swipeText.setText("Confirmed");
                            swipeHandle.setEnabled(false);
                            swipeHandle.setTranslationX(containerWidth - handleWidth);

                            Toast.makeText(getApplicationContext(), "Confirmed!", Toast.LENGTH_SHORT).show();

                            // ✅ Navigate to summary screen
                            Intent intent = new Intent(SendMoneyConfirmActivity.this, SendMoneySummaryActivity.class);
                            startActivity(intent);
                        } else {
                            swipeHandle.animate().translationX(0).setDuration(200).start();
                        }
                        return true;
                }
                return false;
            }
        });
    }

}