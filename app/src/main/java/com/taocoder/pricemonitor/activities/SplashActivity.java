package com.taocoder.pricemonitor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.taocoder.pricemonitor.helpers.SessionManager;
import com.taocoder.pricemonitor.models.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isFirstTime()) {
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final Intent intent = new Intent(SplashActivity.this, CreateAccountActivity.class);
            intent.putExtra("page", "login");

            //User Type (HQ or Manager)
            String type = sessionManager.getType();

            if (user == null) {
                startActivity(intent);
                finish();
            }
            else {
                if (type.equalsIgnoreCase("manager")) {
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot: task.getResult()) {
                                    User u = snapshot.toObject(User.class);
                                    if (u != null) {
                                        if (u.getStatus() == 0) {
                                            startActivity(intent);
                                        }
                                        else {
                                            startActivity(new Intent(SplashActivity.this, ManagerHomeActivity.class));
                                        }

                                        finish();
                                    }
                                }
                            }
                        }
                    });
                }
                else {

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        }
    }
}