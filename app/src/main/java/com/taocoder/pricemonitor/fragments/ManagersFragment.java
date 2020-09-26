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
import com.taocoder.pricemonitor.adapters.ManagersAdapter;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.models.User;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagersFragment extends Fragment implements ManagersAdapter.UserClickLister {


    private MainViewModel viewModel;

    private List<User> users;
    private ManagersAdapter adapter;

    //Object and position of user to update
    private int position;

    public ManagersFragment() {
        // Required empty public constructor
    }

    public static ManagersFragment newInstance(String param1, String param2) {
        return new ManagersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_managers, container, false);

        final LinearLayout holder = rootView.findViewById(R.id.content);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        viewModel.managers();

        users = new ArrayList<>();
        adapter = new ManagersAdapter(getContext(), users);
        adapter.setClickLister(this);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        viewModel.getManagers().observe(requireActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> list) {
                if (list == null) return;

                if (list.size() > 0) {
                    users.clear();
                    users.addAll(list);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Utils.toastMessage(getContext(), "No Record Found");
                }

                progressBar.setVisibility(View.GONE);
                holder.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getUpdateResult().observe(requireActivity(), new Observer<ResponseInfo<User>>() {
            @Override
            public void onChanged(ResponseInfo<User> responseInfo) {
                if (responseInfo == null) return;

                if (responseInfo.isError()) {
                    Utils.toastMessage(getContext(), responseInfo.getMessage());
                }
                else {
                    users.set(position, responseInfo.getData());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onUserClick(User u, int p) {
        position = p;
        viewModel.updateUser(u);
    }
}