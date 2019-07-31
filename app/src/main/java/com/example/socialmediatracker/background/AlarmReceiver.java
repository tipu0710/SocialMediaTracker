package com.example.socialmediatracker.background;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.helper.AppInfo;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long minimumTime = 2*3600*1000;
        String Title = intent.getStringExtra("title");
        Intent x = new Intent(context, Alert.class);
        x.putExtra("title", Title);
        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DBcreation dBcreation = new DBcreation(context);
        ArrayList<DatabaseModel> databaseModels = new ArrayList<>();
        databaseModels = dBcreation.getAllData();

        for (int i=0; i<databaseModels.size(); i++){
            if (databaseModels.get(i).getTime()<minimumTime){
                minimumTime = databaseModels.get(i).getTime();
            }
        }

        AppInfo.SetAlarms(context, minimumTime);

        AppChecker appChecker = new AppChecker();
        String packageName = appChecker.getForegroundApp(context);

        ArrayList<UsageStats> appInfo = AppInfo.AppUpdateStatus(context, UsageStatsManager.INTERVAL_DAILY);

        context.startActivity(x);
    }
}
