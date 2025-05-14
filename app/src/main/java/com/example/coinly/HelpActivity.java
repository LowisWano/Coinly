package com.example.coinly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpActivity extends AppCompatActivity {
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);

        sendButton = findViewById(R.id.sendButton);


        sendButton.setOnClickListener(v -> {
            String subject = "Subject";
            String message = "Tell us your concern!";

            Intent intent = new Intent(HelpActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}