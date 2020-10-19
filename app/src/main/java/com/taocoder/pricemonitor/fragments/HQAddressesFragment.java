package com.taocoder.pricemonitor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.adapters.StationAddressAdapter;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.AddressesResult;
import com.taocoder.pricemonitor.models.StationAddress;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HQAddressesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HQAddressesFragment extends Fragment {

    private List<StationAddress> addresses;
    private StationAddressAdapter adapter;

    private MainViewModel viewModel;

    public HQAddressesFragment() {
        // Required empty public constructor
    }

    public static HQAddressesFragment newInstance(String param1, String param2) {
        return new HQAddressesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_h_q_addresses, container, false);

        //Load address
        viewModel.addresses();

        final LinearLayout holder = rootView.findViewById(R.id.content);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        addresses = new ArrayList<>();
        adapter = new StationAddressAdapter(getContext(), addresses);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);


        //Observe result
        viewModel.getAddressesResult().observe(requireActivity(), new Observer<AddressesResult>() {
           @Override
           public void onChanged(AddressesResult addressesResult) {

               if (addressesResult == null) return;

               progressBar.setVisibility(View.GONE);
               holder.setVisibility(View.VISIBLE);

               if (addressesResult.isError()) {
                   Utils.toastMessage(getContext(), addressesResult.getMessage());
               }
               else {
                   addresses.clear();
                   addresses.addAll(addressesResult.getAddresses());
                   adapter.notifyDataSetChanged();
               }
           }
        });

        return rootView;
    }
}