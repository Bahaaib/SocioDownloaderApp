package com.bahaa.sociodownloader.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bahaa.sociodownloader.Adapter.ProgressAdapter;
import com.bahaa.sociodownloader.R;
import com.bahaa.sociodownloader.WrapContentLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bahaa.sociodownloader.HomeActivity.progressList;

public class ProgressFragment extends Fragment {


    private ProgressAdapter adapter;
    private WrapContentLinearLayoutManager wrapContentLinearLayoutManager;

    @BindView(R.id.progress_rv)
    RecyclerView progressRV;

    private Unbinder unbinder;

    private static int oldSize;


    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_progress, container, false);

        unbinder = ButterKnife.bind(this, v);

        setupDownloadsRV();

        return v;
    }

    @Override
    public void onResume() {
        Log.i("Statuss", "onResume/ OLD SIZE= " + oldSize);
        if (oldSize >= 1) {
            Log.i("Statuss", "onResume/ Larger than or equal 1");
            adapter.notifyItemRangeInserted(oldSize - 1, progressList.size() - 1);
            oldSize = progressList.size();
        } else {
            Log.i("Statuss", "onResume/ Less than 1");
            adapter.notifyItemRangeInserted(oldSize + 1, progressList.size());
            oldSize = progressList.size();
        }
        Log.i("Statuss", "onResume/ I have: " + progressList.size() + " items");
        super.onResume();
    }

    private void setupDownloadsRV() {
        adapter = new ProgressAdapter(getActivity(), progressList);
        progressRV.setAdapter(adapter);
        wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(getActivity());
        progressRV.setLayoutManager(wrapContentLinearLayoutManager);

    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
