package com.taocoder.pricemonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.StationAddress;

import java.util.List;

public class StationAddressAdapter extends RecyclerView.Adapter<StationAddressAdapter.StationViewHolder> {

    private Context context;
    private List<StationAddress> addresses;

    public StationAddressAdapter(Context context, List<StationAddress> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.station_view, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        StationAddress address = addresses.get(position);
        holder.address.setText(address.getAddress());
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class StationViewHolder extends RecyclerView.ViewHolder {

        TextView address;

        public StationViewHolder(View view) {
            super(view);
            address = view.findViewById(R.id.address);
        }
    }
}
