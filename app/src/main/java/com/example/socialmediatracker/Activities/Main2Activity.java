package com.example.socialmediatracker.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.Settings;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ArrayList<UsageStats> dailyUsageStats, weeklyUsageStats, monthlyUsageStats;
    ProgressBar progressBar;
    Sprite doubleBounce;
    BarChart dailyBarChart, weeklyBarChart, monthlyBarChart;
    private boolean databaseStatus = false;
    DBcreation dBcreation;
    ArrayList<DatabaseModel> databaseModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progress_main_2);
        doubleBounce = new DoubleBounce();
        dailyBarChart = findViewById(R.id.daily_chart);
        weeklyBarChart = findViewById(R.id.weekly_chart);
        monthlyBarChart = findViewById(R.id.monthly_chart);

        if (!hasUsageStatsPermission(Main2Activity.this))
            requestUsageStatsPermission();


        dailyUsageStats = new ArrayList<>();

        weeklyUsageStats = new ArrayList<>();

        monthlyUsageStats = new ArrayList<>();

        dBcreation = new DBcreation(Main2Activity.this);
        ArrayList<DatabaseModel> databaseModels = dBcreation.getAllData();
        databaseStatus = databaseModels.size() <= 0;
        databaseModels = dBcreation.getAllData();
        new LoadData().execute();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Main2Activity.this, MainActivity.class));
                            }
                        }).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            dailyUsageStats = AppInfo.AppUpdateStatus(Main2Activity.this, UsageStatsManager.INTERVAL_DAILY);
            weeklyUsageStats = AppInfo.AppUpdateStatus(Main2Activity.this, UsageStatsManager.INTERVAL_WEEKLY);
            monthlyUsageStats = AppInfo.AppUpdateStatus(Main2Activity.this, UsageStatsManager.INTERVAL_MONTHLY);

            AppUsagesActivity.UsageTimeComparator mUsageTimeComparator = new AppUsagesActivity.UsageTimeComparator();
            Collections.sort(dailyUsageStats, mUsageTimeComparator);
            Collections.sort(weeklyUsageStats, mUsageTimeComparator);
            Collections.sort(monthlyUsageStats, mUsageTimeComparator);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createBar();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminateDrawable(doubleBounce);
        }
    }

    private void createBar() {
        dailyBarChart.setDrawBarShadow(false);
        dailyBarChart.setDrawValueAboveBar(true);
        dailyBarChart.animateXY(1000,1000);
        dailyBarChart.moveViewToX(3);
        dailyBarChart.setVisibleXRangeMaximum(3);


        weeklyBarChart.setDrawBarShadow(false);
        weeklyBarChart.setDrawValueAboveBar(true);
        weeklyBarChart.animateXY(1000,1000);
        weeklyBarChart.moveViewToX(3);
        weeklyBarChart.setVisibleXRangeMaximum(3);



        monthlyBarChart.setDrawBarShadow(false);
        monthlyBarChart.setDrawValueAboveBar(true);
        monthlyBarChart.animateXY(1000,1000);
        monthlyBarChart.moveViewToX(3);
        monthlyBarChart.setVisibleXRangeMaximum(3);



        ArrayList<BarEntry> barEntries = new ArrayList<>();
        List<String> dailyAppName = new ArrayList<>();
        for (int i = 0; i< dailyUsageStats.size(); i++){
            UsageStats pkgStats = dailyUsageStats.get(i);
            long time = pkgStats.getTotalTimeInForeground() / (1000*60*60);
            String packageName = dailyUsageStats.get(i).getPackageName();
            String name = AppInfo.GetAppName(packageName, Main2Activity.this);
            dailyAppName.add(name);
            barEntries.add(new BarEntry(i, time));

            if (databaseStatus){
                dBcreation.AddAppInfo(new DatabaseModel(packageName, 2*3600*1000));
            }else {
                if (!databaseModels.get(i).getPackageName().contains(packageName)){
                    dBcreation.AddAppInfo(new DatabaseModel(packageName, 2*3600*1000));
                }
            }
        }

        IAxisValueFormatter xDAxisValueFormatter = new DayAxisValueFormatter(dailyBarChart,dailyAppName);
        XAxis xAxis = dailyBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(dailyAppName.size());
        xAxis.setValueFormatter(xDAxisValueFormatter);

        YAxis leftAxis = dailyBarChart.getAxisLeft();
        leftAxis.setLabelCount(10, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = dailyBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = dailyBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Daily Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        dailyBarChart.setData(barData);

        ArrayList<BarEntry> weeklyBarEntries = new ArrayList<>();
        List<String> weeklyAppName = new ArrayList<>();
        for (int i = 0; i< weeklyUsageStats.size(); i++){
            UsageStats pkgStats = weeklyUsageStats.get(i);
            long time = pkgStats.getTotalTimeInForeground() / (1000*60*60);
            String packageName = weeklyUsageStats.get(i).getPackageName();
            String name = AppInfo.GetAppName(packageName, Main2Activity.this);
            weeklyAppName.add(name);
            weeklyBarEntries.add(new BarEntry(i, time));
        }
        IAxisValueFormatter xWAxisValueFormatter = new DayAxisValueFormatter(weeklyBarChart,weeklyAppName);
        XAxis xWAxis = weeklyBarChart.getXAxis();
        xWAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xWAxis.setDrawGridLines(false);
        xWAxis.setGranularity(1f); // only intervals of 1 day
        xWAxis.setLabelCount(weeklyAppName.size());
        xWAxis.setValueFormatter(xWAxisValueFormatter);

        YAxis leftAxisW = weeklyBarChart.getAxisLeft();
        leftAxisW.setLabelCount(10, false);
        leftAxisW.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxisW.setSpaceTop(15f);
        leftAxisW.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxisW = weeklyBarChart.getAxisRight();
        rightAxisW.setEnabled(false);

        Legend lw = weeklyBarChart.getLegend();
        lw.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        lw.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lw.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lw.setDrawInside(false);
        lw.setForm(Legend.LegendForm.SQUARE);
        lw.setFormSize(9f);
        lw.setTextSize(11f);


        BarDataSet weeklyBarDataSet = new BarDataSet(weeklyBarEntries, "Weekly Data");
        weeklyBarDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData weeklyBarData = new BarData(weeklyBarDataSet);
        weeklyBarData.setBarWidth(0.5f);
        weeklyBarChart.setData(weeklyBarData);

        ArrayList<BarEntry> monthlyBarEntries = new ArrayList<>();
        List<String> monthlyAppName = new ArrayList<>();
        for (int i = 0; i< monthlyUsageStats.size(); i++){
            UsageStats pkgStats = monthlyUsageStats.get(i);
            long time = pkgStats.getTotalTimeInForeground() / (1000*60*60);
            String packageName = monthlyUsageStats.get(i).getPackageName();
            String name = AppInfo.GetAppName(packageName, Main2Activity.this);
            monthlyAppName.add(name);
            monthlyBarEntries.add(new BarEntry(i, time));
        }

        IAxisValueFormatter xMAxisValueFormatter = new DayAxisValueFormatter(monthlyBarChart,monthlyAppName);
        XAxis xMAxis = monthlyBarChart.getXAxis();
        xMAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xMAxis.setDrawGridLines(false);
        xMAxis.setGranularity(1f); // only intervals of 1 day
        xMAxis.setLabelCount(monthlyAppName.size());
        xMAxis.setValueFormatter(xMAxisValueFormatter);

        YAxis leftAxisM = monthlyBarChart.getAxisLeft();
        leftAxisM.setLabelCount(10, false);
        leftAxisM.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxisM.setSpaceTop(15f);
        leftAxisM.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxisM = monthlyBarChart.getAxisRight();
        rightAxisM.setEnabled(false);

        Legend lm = monthlyBarChart.getLegend();
        lm.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        lm.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lm.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lm.setDrawInside(false);
        lm.setForm(Legend.LegendForm.SQUARE);
        lm.setFormSize(9f);
        lm.setTextSize(11f);

        BarDataSet monthlyBarDataSet = new BarDataSet(monthlyBarEntries, "Data");
        monthlyBarDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData monthlyBarData = new BarData(monthlyBarDataSet);
        monthlyBarData.setBarWidth(0.5f);
        monthlyBarChart.setData(monthlyBarData);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

}
