package com.taocoder.pricemonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.Approval;

import java.util.List;

public class ApprovalsStatusAdapter extends RecyclerView.Adapter<ApprovalsStatusAdapter.StatusViewHolder> {

    private Context context;
    private List<Approval> approvals;

    public ApprovalsStatusAdapter(Context context, List<Approval> approvals) {
        this.approvals = approvals;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_view, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
       Approval approval = approvals.get(position);
       holder.price.setText(String.valueOf(approval.getPrice()));
       String status = "pending";
       if (approval.getStatus().equalsIgnoreCase("replied")) {
           status = (approval.isApproved()) ? "Approved" : "Rejected";
       }

       holder.status.setText(status);
    }

    @Override
    public int getItemCount() {
        return approvals.size();
    }

    class StatusViewHolder extends RecyclerView.ViewHolder {
        View v;
        TextView price;
        TextView status;

        public StatusViewHolder(View view) {
            super(view);
            v = view;

            price = view.findViewById(R.id.price);
            status = view.findViewById(R.id.status);
        }
    }
}
