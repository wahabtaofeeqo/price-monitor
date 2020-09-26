package com.taocoder.pricemonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.models.User;

import java.util.List;

public class ManagersAdapter extends RecyclerView.Adapter<ManagersAdapter.UserViewHolder> {

    private Context context;
    private List<User> users;

    private UserClickLister clickLister;

    public ManagersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        String label = "Approve";

        if (user.getStatus() == 1)
            label = "Suspend";

        holder.name.setText(user.getName());
        holder.action.setText(label);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        MaterialButton action;

        public UserViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            action = view.findViewById(R.id.action);

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickLister.onUserClick(users.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface UserClickLister {
        void onUserClick(User user, int position);
    }

    public void setClickLister(UserClickLister clickLister) {
        this.clickLister = clickLister;
    }
}
