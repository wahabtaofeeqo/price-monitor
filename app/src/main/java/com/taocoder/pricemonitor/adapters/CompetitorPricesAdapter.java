package com.taocoder.pricemonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.Price;

import java.util.List;

public class CompetitorPricesAdapter extends RecyclerView.Adapter<CompetitorPricesAdapter.PriceViewModel> {

    private Context context;
    private List<Price> prices;

    public CompetitorPricesAdapter(Context context, List<Price> prices) {
        this.context = context;
        this.prices = prices;
    }

    @NonNull
    @Override
    public PriceViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.price_view, parent, false);
        return new PriceViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewModel holder, int position) {
        Price price = prices.get(position);
        holder.price.setText(price.getVolume());
        holder.address.setText(price.getAddress());
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    class PriceViewModel extends RecyclerView.ViewHolder {

        TextView address;
        TextView price;

        public PriceViewModel(View view) {
            super(view);

            address = view.findViewById(R.id.address);
            price = view.findViewById(R.id.price);
        }
    }
}
