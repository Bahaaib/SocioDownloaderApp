package com.bahaa.sociodownloader.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bahaa.sociodownloader.Fragments.DownloadsFragment;
import com.bahaa.sociodownloader.Fragments.HomeFragment;
import com.bahaa.sociodownloader.Fragments.ProgressFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    //Setting up the View Pager with tabs
    private int mTabsNum;

    public PagerAdapter(FragmentManager fm, int tabsNum) {
        super(fm);
        this.mTabsNum = tabsNum;
    }

    //Here we control the flow of the pager, What Fragment to go on clicking to which Tab..
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ProgressFragment();
            case 2:
                return new DownloadsFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return mTabsNum;
    }
}
