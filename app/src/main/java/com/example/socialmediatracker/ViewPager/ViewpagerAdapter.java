package com.example.socialmediatracker.ViewPager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.socialmediatracker.fragments.StatisticFragment;

public class ViewpagerAdapter extends FragmentStatePagerAdapter {
    public ViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        StatisticFragment statisticFragment = new StatisticFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        statisticFragment.setArguments(bundle);
        return statisticFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
