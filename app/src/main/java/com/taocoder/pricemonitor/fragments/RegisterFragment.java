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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.activities.MainActivity;
import com.taocoder.pricemonitor.activities.ManagerHomeActivity;
import com.taocoder.pricemonitor.helpers.SessionManager;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.interfaces.OnFragmentChangeListener;
import com.taocoder.pricemonitor.models.ServerResponse;
import com.taocoder.pricemonitor.models.User;
import com.taocoder.pricemonitor.viewModels.AccountViewModel;

import java.util.List;

public class RegisterFragment extends Fragment implements Validator.ValidationListener {

    private OnFragmentChangeListener listener;

    @NotEmpty
    private TextInputEditText name;

    @Email
    private TextInputEditText email;

    @NotEmpty
    @Password(min = 6, scheme = Password.Scheme.ANY)
    private TextInputEditText password;

    @NotEmpty
    private TextInputEditText phone;

    private MaterialButton create;

    private ProgressBar progressBar;

    private Validator validator;

    private AccountViewModel viewModel;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);

        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragmen_register, container, false);

        name = (TextInputEditText) rootView.findViewById(R.id.name);
        email = (TextInputEditText) rootView.findViewById(R.id.email);
        password = (TextInputEditText) rootView.findViewById(R.id.password);
        phone = (TextInputEditText) rootView.findViewById(R.id.phone);
        create = (MaterialButton) rootView.findViewById(R.id.create);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        MaterialButton login = (MaterialButton) rootView.findViewById(R.id.login);

        final SessionManager sessionManager = SessionManager.getInstance(getContext());

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStack();
                else
                    listener.onFragmentChange(new LoginFragment());
            }
        });

        viewModel.getRegisterResult().observe(requireActivity(), new Observer<ServerResponse<User>>() {
            @Override
            public void onChanged(ServerResponse<User> userServerResponse) {
                if (userServerResponse == null) return;

                if (userServerResponse.isError()) {
                    Utils.toastMessage(getContext(), userServerResponse.getMessage());
                }
                else {

                    sessionManager.setEmail(email.getText().toString().trim());
                    sessionManager.setType("hq");
                    sessionManager.setName(name.getText().toString().trim());
                    sessionManager.setIsFirstTime(false);

                    startActivity(new Intent(getContext(), MainActivity.class));

                    getActivity().finish();
                }

                create.setEnabled(true);
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
        create.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        //Hide Keyboard
        Utils.hideKeyboard(getContext(), create);

        User user = new User();
        user.setName(name.getText().toString().trim());
        user.setEmail(email.getText().toString().trim());
        user.setType("hq");
        user.setStatus(1);
        user.setPhone(phone.getText().toString().trim());

        viewModel.createAccount(user, password.getText().toString().trim());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error: errors) {
            View view = error.getView();
            String errorMessage = error.getCollatedErrorMessage(getContext());
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(errorMessage);
            }

            if (view instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) view).setError(errorMessage);
            }
        }
    }
}