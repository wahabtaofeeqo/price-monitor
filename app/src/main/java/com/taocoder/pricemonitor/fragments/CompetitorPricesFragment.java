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
import com.taocoder.pricemonitor.adapters.ApprovalsStatusAdapter;
import com.taocoder.pricemonitor.adapters.CompetitorPricesAdapter;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.Approval;
import com.taocoder.pricemonitor.models.ApprovalsResult;
import com.taocoder.pricemonitor.models.Price;
import com.taocoder.pricemonitor.models.PricesResult;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompetitorPricesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompetitorPricesFragment extends Fragment {

    private List<Price> prices;
    private CompetitorPricesAdapter adapter;

    private MainViewModel viewModel;

    public CompetitorPricesFragment() {
        // Required empty public constructor
    }

    public static CompetitorPricesFragment newInstance(String param1, String param2) {
        return new CompetitorPricesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_competitor_prices, container, false);

        //Load approval
        viewModel.checkPrices();

        final LinearLayout holder = rootView.findViewById(R.id.content);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        prices = new ArrayList<>();
        adapter = new CompetitorPricesAdapter(getContext(), prices);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        viewModel.getPricesResult().observe(requireActivity(), new Observer<PricesResult>() {
            @Override
            public void onChanged(PricesResult pricesResult) {
                if (pricesResult == null) return;

                progressBar.setVisibility(View.GONE);
                holder.setVisibility(View.VISIBLE);

                if (pricesResult.isError()) {
                    Utils.toastMessage(getContext(), pricesResult.getMessage());
                }
                else {
                    prices.addAll(pricesResult.getPrices());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }

}