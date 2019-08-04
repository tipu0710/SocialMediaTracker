package com.example.socialmediatracker.background;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.socialmediatracker.Activities.Alert;
import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.helper.AppInfo;
import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("alarm1","start receiver");
        Intent x = new Intent(context, Alert.class);
        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DBcreation dBcreation = new DBcreation(context);

        ArrayList<DatabaseModel> databaseModels = dBcreation.getAllData();
        ArrayList<String> pkgList = new ArrayList<>();
        for (int i = 0; i< databaseModels.size(); i++){
            pkgList.add(databaseModels.get(i).getPackageName());
        }

        AppChecker appChecker = new AppChecker();
        String foregroundPackageName = appChecker.getForegroundApp(context);

        ArrayList<UsageStats> appInfo = AppInfo.AppUpdateStatus(context, UsageStatsManager.INTERVAL_DAILY);
        for (int i = 0; i< appInfo.size(); i++){
            long usedTime = appInfo.get(i).getTotalTimeInForeground();
            String packageName = appInfo.get(i).getPackageName();
            DatabaseModel databaseModel = dBcreation.getDataByPackage(packageName);
            if (!pkgList.contains(packageName)){
                dBcreation.AddAppInfo(new DatabaseModel(packageName, 2*3600*1000));
            }
            long saveTime = databaseModel.getTime();
            if (foregroundPackageName.equals(packageName)){
                if (saveTime<= usedTime){
                    x.putExtra("appName", AppInfo.GetAppName(packageName,context));
                    x.putExtra("packageName", packageName);
                    x.putExtra("usedTime", usedTime);
                    context.startActivity(x);
                }
                break;
            }
        }
    }
}
