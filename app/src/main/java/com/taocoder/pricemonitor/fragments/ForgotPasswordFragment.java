package com.taocoder.pricemonitor.fragments;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.helpers.Utils;

public class ForgotPasswordFragment extends Fragment {

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final MaterialButton request = (MaterialButton) view.findViewById(R.id.request);
        final TextInputEditText email = (TextInputEditText) view.findViewById(R.id.email);

        final TextView login = (TextView) view.findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = email.getText().toString().trim();

                if (!val.isEmpty()) {
                    auth.sendPasswordResetEmail(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            email.setText("");
                            request.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Utils.toastMessage(getContext(), "Reset link has been sent!");
                            }
                            else {
                                Utils.toastMessage(getContext(), "Email reset link not sent.");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.toastMessage(getContext(), "Email not recognised");
                        }
                    });
                    request.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}