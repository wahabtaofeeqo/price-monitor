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
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.Approval;
import com.taocoder.pricemonitor.models.ApprovalsResult;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepliedApprovalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepliedApprovalFragment extends Fragment {

    private List<Approval> approvals;
    private ApprovalsStatusAdapter adapter;

    private MainViewModel viewModel;

    public RepliedApprovalFragment() {
        // Required empty public constructor
    }

    public static RepliedApprovalFragment newInstance(String param1, String param2) {
        return new RepliedApprovalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_replied_approval, container, false);
        final LinearLayout holder = rootView.findViewById(R.id.content);
        final ProgressBar progressBar = rootView.findViewById(R.id.progress);

        viewModel.checkApprovals("replied");

        approvals = new ArrayList<>();
        adapter = new ApprovalsStatusAdapter(getContext(), approvals);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        viewModel.getApprovals().observe(requireActivity(), new Observer<ApprovalsResult>() {
            @Override
            public void onChanged(ApprovalsResult approvalsResult) {
                if (approvalsResult == null) return;

                progressBar.setVisibility(View.GONE);
                holder.setVisibility(View.VISIBLE);

                if (approvalsResult.isError()) {
                    Utils.toastMessage(getContext(), approvalsResult.getMessage());
                }
                else {
                    approvals.clear();
                    approvals.addAll(approvalsResult.getApprovals());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }
}