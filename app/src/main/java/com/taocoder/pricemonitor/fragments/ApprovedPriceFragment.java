package com.taocoder.pricemonitor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.Approval;
import com.taocoder.pricemonitor.models.ServerResponse;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApprovedPriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApprovedPriceFragment extends Fragment {

    private MainViewModel viewModel;

    public ApprovedPriceFragment() {
        // Required empty public constructor
    }

    public static ApprovedPriceFragment newInstance() {
        return new ApprovedPriceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_approved_price, container, false);

        final TextView price = rootView.findViewById(R.id.price);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        //Logged in user ID
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        viewModel.myApprovedPrice(user.getUid());

        viewModel.getApprovedPrice().observe(requireActivity(), new Observer<ServerResponse<Approval>>() {
            @Override
            public void onChanged(ServerResponse<Approval> serverResponse) {
                if (serverResponse == null) return;
                if (!serverResponse.isError()) {
                    Approval approval = serverResponse.getData();
                    price.setText(String.valueOf(approval.getPrice()));
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        return rootView;
    }
}