package com.taocoder.pricemonitor.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.activities.LocationActivity;
import com.taocoder.pricemonitor.adapters.CompetitorPricesAdapter;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.interfaces.OnFragmentChangeListener;
import com.taocoder.pricemonitor.models.CompetitorPriceAndAddress;
import com.taocoder.pricemonitor.models.PricesResult;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompetitorPricesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompetitorPricesFragment extends Fragment implements CompetitorPricesAdapter.OnAddressClickListener {

    private List<CompetitorPriceAndAddress> competitorPriceAndAddresses;
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

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Competitors");

        //Load approval
        viewModel.checkPrices();

        final LinearLayout holder = rootView.findViewById(R.id.content);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);
        final ImageButton filter = rootView.findViewById(R.id.filter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });

        competitorPriceAndAddresses = new ArrayList<>();
        adapter = new CompetitorPricesAdapter(getContext(), competitorPriceAndAddresses);
        adapter.setClickListener(this);
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
                    competitorPriceAndAddresses.clear();
                    competitorPriceAndAddresses.addAll(pricesResult.getCompetitorPriceAndAddresses());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAddressClick(CompetitorPriceAndAddress address) {
        if (address.getLatitude() == 0 || address.getLongitude() == 0) {
            Utils.toastMessage(getContext(), "Lat and Long is not know");
        }
        else {
            Intent intent = new Intent(getContext(), LocationActivity.class);
            intent.putExtra("Lat", String.valueOf(address.getLatitude()));
            intent.putExtra("Long", String.valueOf(address.getLongitude()));
            startActivity(intent);
        }
    }

    private void dateDialog() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + month + "/" + year;
                filterList(date);
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void filterList(String date) {

        List<CompetitorPriceAndAddress> list = new ArrayList<>();

        for(CompetitorPriceAndAddress address: competitorPriceAndAddresses) {
            if (address.getDate().equalsIgnoreCase(date)) {
                list.add(address);
            }
        }

        if (list.size() == 0) {
            Utils.toastMessage(getContext(), "No record for this Date");
        }
        else {
            competitorPriceAndAddresses.clear();
            competitorPriceAndAddresses.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }
}