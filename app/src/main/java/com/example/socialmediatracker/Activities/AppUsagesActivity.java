package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialmediatracker.Adapter.AppAdapter;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AppUsagesActivity extends AppCompatActivity {

    private RecyclerView appList;
    private ArrayList<UsageStats> dailyUsageStats;
    ProgressBar progressBar;
    Sprite doubleBounce;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);
        appList = findViewById(R.id.app_list);
        progressBar = findViewById(R.id.progress);
        doubleBounce = new DoubleBounce();
        dailyUsageStats = new ArrayList<>();

        appList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        appList.setLayoutManager(mLayoutManager);

        mAdapter = new AppAdapter(AppUsagesActivity.this, dailyUsageStats);
        appList.setAdapter(mAdapter);
        new LoadData().execute();
    }


    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            dailyUsageStats = AppInfo.AppUpdateStatus(AppUsagesActivity.this, UsageStatsManager.INTERVAL_DAILY);
            AppUsagesActivity.UsageTimeComparator mUsageTimeComparator = new AppUsagesActivity.UsageTimeComparator();
            Collections.sort(dailyUsageStats, mUsageTimeComparator);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new AppAdapter(AppUsagesActivity.this, dailyUsageStats);
            appList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminateDrawable(doubleBounce);
        }
    }

    public static class UsageTimeComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            return (int)(b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }
}
