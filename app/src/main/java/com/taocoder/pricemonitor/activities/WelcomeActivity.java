package com.taocoder.pricemonitor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.helpers.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final SessionManager sessionManager = SessionManager.getInstance(this);

        MaterialButton hq = findViewById(R.id.hq);
        hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                if (!sessionManager.isFirstTime()) {
                    intent.putExtra("page", "login");
                }
                startActivity(intent);
            }
        });
    }
}