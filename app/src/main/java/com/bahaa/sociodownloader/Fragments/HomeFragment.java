package com.bahaa.sociodownloader.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bahaa.sociodownloader.Facebook.FacebookActivity;
import com.bahaa.sociodownloader.Instagram.InstagramActivity;
import com.bahaa.sociodownloader.R;
import com.bahaa.sociodownloader.Youtube.view.YoutubeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    @BindView(R.id.arrow_left)
    ImageView arrowLeft;

    @BindView(R.id.arrow_center)
    ImageView arrowCenter;

    @BindView(R.id.arrow_right)
    ImageView arrowRight;

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

        startAnimation(arrowLeft, 20);
        startAnimation(arrowCenter, 10);
        startAnimation(arrowRight, 20);

        return v;
    }

    private void startAnimation(ImageView view, int YDelta){
        Animation animation = new TranslateAnimation(0, 0,0, YDelta);
        animation.setDuration(500);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setFillAfter(true);
        view.startAnimation(animation);
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
