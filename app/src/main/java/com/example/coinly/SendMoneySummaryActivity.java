package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SendMoneySummaryActivity extends AppCompatActivity {

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_summary);

        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SendMoneySummaryActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }
}