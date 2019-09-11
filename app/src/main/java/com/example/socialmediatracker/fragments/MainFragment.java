package com.example.socialmediatracker.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.socialmediatracker.Activities.AppUsagesActivity;
import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment {

    private int position;
    private TextView[] appName = new TextView[3];
    private ImageView[] appIcon = new ImageView[3];
    private PieChart pieChart;
    private ArrayList<UsageStats> usageStats;
    private ProgressBar progressBar;
    private Sprite doubleBounce;
    private boolean databaseStatus = false;
    private DBcreation dBcreation;
    private ArrayList<DatabaseModel> databaseModels = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_main, container, false);
        TextView daily = view.findViewById(R.id.daily);
        appIcon[0] = view.findViewById(R.id.app1);
        appIcon[1] = view.findViewById(R.id.app2);
        appIcon[2] = view.findViewById(R.id.app3);
        appName[0] = view.findViewById(R.id.app_name1);
        appName[1] = view.findViewById(R.id.app_name2);
        appName[2] = view.findViewById(R.id.app_name3);
        pieChart = view.findViewById(R.id.pie);
        progressBar = view.findViewById(R.id.progress_bar);
        doubleBounce = new DoubleBounce();
        position = getArguments().getInt("position");


        dBcreation = new DBcreation(getContext());
        databaseModels = dBcreation.getAllData();
        databaseStatus = databaseModels.size() <= 0;

        if (position==0){
            daily.setText("Most usages app of today");
        }else {
            daily.setText("Most usages app of this month");
        }

        new LoadData().execute();
        return view;
    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (position==0){
                usageStats = AppInfo.AppUpdateStatus(getContext(), UsageStatsManager.INTERVAL_DAILY);
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
            pieChart.setVisibility(View.VISIBLE);
            int size = usageStats.size();
            for (int i=0; i<3;i++){
                if (i<size){
                    appIcon[i].setBackground(AppInfo.getAppIconByPackageName(usageStats.get(i).getPackageName(),getContext()));
                    appName[i].setText(AppInfo.GetAppName(usageStats.get(i).getPackageName(),getContext()));
                }else {
                    appIcon[i].setVisibility(View.INVISIBLE);
                    appName[i].setVisibility(View.INVISIBLE);
                }
            }
            if (size>0){
                createBar();
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

    private void createBar() {
        long totalUsagesDay = 0;
        long timeUse;

        if (position==0){
            timeUse = 1000*60;
        }else {
            timeUse = 1000*60*60;
        }

        ArrayList<String> pkgList = new ArrayList<>();
        for (int i=0; i<databaseModels.size();i++){
            pkgList.add(databaseModels.get(i).getPackageName());
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        long[] timeUsed = new long[3];
        for (int i = 0; i< usageStats.size(); i++){
            UsageStats pkgStats = usageStats.get(i);
            long time = pkgStats.getTotalTimeInForeground() / timeUse;
            if (i<3){
                timeUsed[i]=time;
            }
            totalUsagesDay = totalUsagesDay+time;
            if (totalUsagesDay == 0){
                totalUsagesDay = 1;
            }
            String packageName = usageStats.get(i).getPackageName();
            if (databaseStatus){
                dBcreation.AddAppInfo(new DatabaseModel(packageName, 2*3600*1000));
            }
            if (!pkgList.contains(packageName)){
                dBcreation.AddAppInfo(new DatabaseModel(packageName, 2*3600*1000));
            }
        }

        pieEntries.add(new PieEntry(timeUsed[0], AppInfo.GetAppName(usageStats.get(0).getPackageName(),getContext()), (timeUsed[0]*100)/totalUsagesDay));
        pieEntries.add(new PieEntry(timeUsed[1], AppInfo.GetAppName(usageStats.get(1).getPackageName(),getContext()), (timeUsed[1]*100)/totalUsagesDay));
        pieEntries.add(new PieEntry(timeUsed[2], AppInfo.GetAppName(usageStats.get(2).getPackageName(),getContext()), (timeUsed[2]*100)/totalUsagesDay));
        long l = totalUsagesDay - timeUsed[2] - timeUsed[1] - timeUsed[0];
        pieEntries.add(new PieEntry(l, "Others", (l *100)/totalUsagesDay));

        pieChart.animateXY(1000,1000);
        PieDataSet pieDataSet;
        Description description = new Description();
        if (position==0){
            description = new Description();
            description.setText("in minutes");
            pieDataSet = new PieDataSet(pieEntries,"");
        }else {
            description.setText("in hours");
            pieDataSet = new PieDataSet(pieEntries,"");
        }

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setValueTextSize(2);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setDescription(description);
        pieChart.setEntryLabelColor(Color.parseColor("#000000"));
        pieChart.invalidate();
    }

}
