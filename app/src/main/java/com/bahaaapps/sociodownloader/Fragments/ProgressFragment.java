package com.bahaaapps.sociodownloader.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bahaaapps.sociodownloader.Adapter.ProgressAdapter;
import com.bahaaapps.sociodownloader.R;
import com.bahaaapps.sociodownloader.WrapContentLinearLayoutManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bahaaapps.sociodownloader.HomeActivity.progressList;

public class ProgressFragment extends Fragment {


    private ProgressAdapter adapter;
    private WrapContentLinearLayoutManager wrapContentLinearLayoutManager;

    @BindView(R.id.progress_rv)
    RecyclerView progressRV;
    @BindView(R.id.progress_adView)
    AdView adView;



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

        setupProgressRV();
        loadBannerAd();

        return v;
    }



    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    private void setupProgressRV() {
        adapter = new ProgressAdapter(getActivity(), progressList);
        progressRV.setAdapter(adapter);
        wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(getActivity());
        progressRV.setLayoutManager(wrapContentLinearLayoutManager);

    }

    private void loadBannerAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
