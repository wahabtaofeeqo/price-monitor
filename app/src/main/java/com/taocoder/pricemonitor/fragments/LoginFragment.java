package com.taocoder.pricemonitor.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.activities.MainActivity;
import com.taocoder.pricemonitor.activities.ManagerHomeActivity;
import com.taocoder.pricemonitor.helpers.SessionManager;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.interfaces.OnFragmentChangeListener;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.models.User;
import com.taocoder.pricemonitor.viewModels.AccountViewModel;

import java.util.List;


public class LoginFragment extends Fragment implements Validator.ValidationListener {


    private OnFragmentChangeListener listener;

    @NotEmpty
    private TextInputEditText username;

    @NotEmpty
    private TextInputEditText password;

    private Validator validator;

    private AccountViewModel viewModel;

    private ProgressBar progressBar;

    private MaterialButton login;

    SessionManager sessionManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);

        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        sessionManager = SessionManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        login = rootView.findViewById(R.id.login);
        username = rootView.findViewById(R.id.username);
        password = rootView.findViewById(R.id.password);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        final TextView forgotPassword = (TextView) rootView.findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentChange(new ForgotPasswordFragment());
            }
        });

        TextView create = rootView.findViewById(R.id.join);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentChange(new RegisterFragment());
            }
        });

        viewModel.getLoginResult().observe(requireActivity(), new Observer<ResponseInfo<User>>() {
            @Override
            public void onChanged(ResponseInfo<User> userResponseInfo) {
                if (userResponseInfo == null) return;

                if (userResponseInfo.isError()) {
                    Utils.toastMessage(getContext(), userResponseInfo.getMessage());
                }
                else {

                    User user = userResponseInfo.getData();
                    sessionManager.setEmail(username.getText().toString().trim());
                    sessionManager.setType(user.getType());
                    sessionManager.setIsFirstTime(false);

                    if (user.getStatus() == 0) {
                        Utils.toastMessage(getContext(), "Your account has been Temporarily block. Contact HQ");
                    }
                    else {
                        if (user.getType().equalsIgnoreCase("hq")) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                        else {
                            startActivity(new Intent(getContext(), ManagerHomeActivity.class));
                        }
                        getActivity().finish();
                    }
                }

                login.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentChangeListener) {
            listener = (OnFragmentChangeListener) getActivity();
        }
        else {
            throw new IllegalStateException("Fragment cannot be attached to this activity");
        }
    }

    @Override
    public void onValidationSucceeded() {
        login.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        //Hide Keyboard
        Utils.hideKeyboard(getContext(), login);
        viewModel.login(username.getText().toString().trim(), password.getText().toString().trim());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error: errors) {
            View view = error.getView();
            String errorMessage = error.getCollatedErrorMessage(getContext());
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(errorMessage);
            }
        }
    }
}