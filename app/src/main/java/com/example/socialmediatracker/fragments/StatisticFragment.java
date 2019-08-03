package com.example.socialmediatracker.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.socialmediatracker.Activities.AppUsagesActivity;
import com.example.socialmediatracker.Activities.Main2Activity;
import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.example.socialmediatracker.helper.DayAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticFragment extends Fragment {

    private ArrayList<UsageStats> usageStats;
    private BarChart barChart;
    private ProgressBar progressBar;
    private Sprite doubleBounce;
    private int position;

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_statistic, container, false);

        position = getArguments().getInt("position");

        TextView textView = view.findViewById(R.id.text_view);
        barChart = view.findViewById(R.id.chart);
        progressBar = view.findViewById(R.id.progress);
        doubleBounce = new DoubleBounce();

        if (position==0){
            textView.setText("Daily Usages");
        }else if (position==1){
            textView.setText("Weakly Usages");
        }else {
            textView.setText("Monthly Usages");
        }


        new LoadData().execute();

        return view;
    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (position==0){
                usageStats = AppInfo.AppUpdateStatus(getContext(), UsageStatsManager.INTERVAL_DAILY);
            }else if (position==1){
                usageStats = AppInfo.AppUpdateStatus(getContext(), UsageStatsManager.INTERVAL_WEEKLY);
            }else {
                usageStats = AppInfo.AppUpdateStatus(getContext(), UsageStatsManager.INTERVAL_MONTHLY);
            }

            AppUsagesActivity.UsageTimeComparator mUsageTimeComparator = new AppUsagesActivity.UsageTimeComparator();
            Collections.sort(usageStats, mUsageTimeComparator);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            barChart.setVisibility(View.VISIBLE);
            if (position==0){
                createBar("Daily usages in minutes", 1000*60);
            }else if (position==1){
                createBar("Weekly usages in hour", 1000*60*60);
            }else {
                createBar("Monthly usages in hours", 1000*60*60);
            }

            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminateDrawable(doubleBounce);
        }
    }

    private void createBar(String s, long divided) {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.animateXY(1000,1000);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        List<String> dailyAppName = new ArrayList<>();
        for (int i = 0; i< usageStats.size(); i++){
            UsageStats pkgStats = usageStats.get(i);
            long time = pkgStats.getTotalTimeInForeground() / divided;
            String packageName = usageStats.get(i).getPackageName();
            String name = AppInfo.GetAppName(packageName, getContext());
            dailyAppName.add(name);
            barEntries.add(new BarEntry(i, time));
        }
        IAxisValueFormatter xDAxisValueFormatter = new DayAxisValueFormatter(barChart,dailyAppName);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(dailyAppName.size());
        xAxis.setValueFormatter(xDAxisValueFormatter);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(10, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);


        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);

        BarDataSet barDataSet = new BarDataSet(barEntries, s);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setVisibleXRangeMaximum(3f);
        barChart.invalidate();
    }
}
