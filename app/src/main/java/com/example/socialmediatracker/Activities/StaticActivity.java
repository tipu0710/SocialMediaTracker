package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.ViewPager.MainViewPagerAdapter;
import com.example.socialmediatracker.ViewPager.ViewpagerAdapter;
import com.example.socialmediatracker.fragments.MainFragment;
import com.example.socialmediatracker.fragments.StatisticFragment;

public class StaticActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewpagerAdapter viewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);
        getSupportActionBar().hide();
        viewPager = findViewById(R.id.statistic_view_pager);


        viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewpagerAdapter);
    }
}
