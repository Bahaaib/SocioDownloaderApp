package com.bahaa.sociodownloader.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bahaa.sociodownloader.Facebook.FacebookActivity;
import com.bahaa.sociodownloader.Instagram.InstagramActivity;
import com.bahaa.sociodownloader.R;
import com.bahaa.sociodownloader.Youtube.view.YoutubeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private Unbinder unbinder;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        unbinder = ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.fb_button)
    void openFBDownloader(){
        Intent intent = new Intent(getActivity(), FacebookActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.yt_button)
    void openTYDownloader(){
        Intent intent = new Intent(getActivity(), YoutubeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.ig_button)
    void openIGDownloader(){
        Intent intent = new Intent(getActivity(), InstagramActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
