package com.taocoder.pricemonitor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.ServerResponse;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestApprovalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestApprovalFragment extends Fragment {

    private MainViewModel viewModel;

    public RequestApprovalFragment() {
        // Required empty public constructor
    }

    public static RequestApprovalFragment newInstance(String param1, String param2) {
        return new RequestApprovalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_request_approval, container, false);

        final TextInputEditText price = rootView.findViewById(R.id.price);
        final MaterialButton request = rootView.findViewById(R.id.request);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (price.getText().toString().trim().isEmpty()) {
                    price.setError("Enter Price");
                }
                else {

                    String today = "";

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        today = simpleDateFormat.format(new Date());
                    }
                    catch (Exception e) {

                    }

                    Utils.hideKeyboard(getContext(), request);
                    request.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    viewModel.requestPriceApproval(price.getText().toString(), today);
                }
            }
        });

        viewModel.getApprovalResult().observe(requireActivity(), new Observer<ServerResponse<Boolean>>() {
            @Override
            public void onChanged(ServerResponse<Boolean> serverResponse) {
                if (serverResponse == null) return;

                if (serverResponse.isError()) {
                    Utils.toastMessage(getContext(), serverResponse.getMessage());
                }
                else {
                    price.setText("");
                    Utils.toastMessage(getContext(), "Approval has been sent");
                }

                request.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        return rootView;
    }
}