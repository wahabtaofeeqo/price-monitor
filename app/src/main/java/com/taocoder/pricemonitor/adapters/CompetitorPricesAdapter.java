package com.taocoder.pricemonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.CompetitorPriceAndAddress;

import java.util.List;

public class CompetitorPricesAdapter extends RecyclerView.Adapter<CompetitorPricesAdapter.PriceViewModel> {

    private Context context;
    private List<CompetitorPriceAndAddress> competitorPriceAndAddresses;
    private OnAddressClickListener clickListener;

    public CompetitorPricesAdapter(Context context, List<CompetitorPriceAndAddress> competitorPriceAndAddresses) {
        this.context = context;
        this.competitorPriceAndAddresses = competitorPriceAndAddresses;
    }

    @NonNull
    @Override
    public PriceViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.price_view, parent, false);
        return new PriceViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewModel holder, int position) {
        CompetitorPriceAndAddress competitorPriceAndAddress = competitorPriceAndAddresses.get(position);
        holder.price.setText(competitorPriceAndAddress.getVolume());
        holder.address.setText(competitorPriceAndAddress.getAddress());
    }

    @Override
    public int getItemCount() {
        return competitorPriceAndAddresses.size();
    }

    class PriceViewModel extends RecyclerView.ViewHolder {

        TextView address;
        TextView price;
        ImageButton map;

        public PriceViewModel(View view) {
            super(view);

            address = view.findViewById(R.id.address);
            price = view.findViewById(R.id.price);
            map = view.findViewById(R.id.map);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onAddressClick(competitorPriceAndAddresses.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnAddressClickListener {
        void onAddressClick(CompetitorPriceAndAddress address);
    }

    public void setClickListener(OnAddressClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
