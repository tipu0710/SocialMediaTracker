package com.example.socialmediatracker.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.background.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppInfo {
    private Context context;
    private static final long A_DAY = 86400 * 1000;


    public AppInfo(Context context) {
        this.context = context;
    }


    public static List<String> GetAllInstalledApkInfo(Context context){

        List<String> ApkPackageName = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN,null);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent,0);

        for(ResolveInfo resolveInfo : resolveInfoList){

            ActivityInfo activityInfo = resolveInfo.activityInfo;

            if(!isSystemPackage(resolveInfo)){

                ApkPackageName.add(activityInfo.applicationInfo.packageName);
            }
        }

        return ApkPackageName;
    }



    public static boolean isSystemPackage(ResolveInfo resolveInfo){

        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }



    public static Drawable getAppIconByPackageName(String ApkTempPackageName, Context context){

        Drawable drawable;

        try{
            drawable = context.getPackageManager().getApplicationIcon(ApkTempPackageName);

        }
        catch (PackageManager.NameNotFoundException e){

            e.printStackTrace();

            drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        }
        return drawable;
    }



    public static String GetAppName(String ApkPackageName, Context context){

        String Name = "";

        ApplicationInfo applicationInfo;

        PackageManager packageManager = context.getPackageManager();

        try {

            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);

            if(applicationInfo!=null){

                Name = (String)packageManager.getApplicationLabel(applicationInfo);
            }

        }catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return Name;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static List<UsageStats> getAppDetails(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        long time = System.currentTimeMillis();
        assert usm != null;
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, time);
    }


    public static boolean isSystemApp(ApplicationInfo applicationInfo){
        boolean b=false;

        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            b = true;
        }
        /*if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            b = true;
        }*/
        return b;
    }


    public static ArrayList<UsageStats> AppUpdateStatus(Context context, int INTERVAL) {

        ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
        ArrayList<UsageStats> mPackageStats = new ArrayList<>();
        long[] range;
        if (INTERVAL == UsageStatsManager.INTERVAL_MONTHLY){
            range = monthlyRange();
        }else if (INTERVAL == UsageStatsManager.INTERVAL_WEEKLY){
            range = weeklyRange();
        }else{
            range = dailyRange();
        }

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PackageManager mPm = context.getPackageManager();

        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(INTERVAL,
                        range[0], range[1]);
        if (stats == null) {
            return mPackageStats;
        }


        ArrayMap<String, UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();
        ArrayList<String> list = new ArrayList<>();
        list.add("com.google.android.youtube");
        list.add("com.google.android.apps.tachyon");
        list.add("com.google.android.talk");
        list.add("com.android.chorme");
        for (int i = 0; i < statCount; i++) {
            final android.app.usage.UsageStats pkgStats = stats.get(i);

            // load application labels for each application
            try {
                if (list.contains(pkgStats.getPackageName())){
                    ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);
                    String label = appInfo.loadLabel(mPm).toString();
                    mAppLabelMap.put(pkgStats.getPackageName(), label);

                    UsageStats existingStats =
                            map.get(pkgStats.getPackageName());
                    if (existingStats == null) {
                        map.put(pkgStats.getPackageName(), pkgStats);
                    } else {
                        existingStats.add(pkgStats);
                    }
                }else {
                    ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);

                    if (AppInfo.isSystemApp(appInfo)){
                        String label = appInfo.loadLabel(mPm).toString();
                        mAppLabelMap.put(pkgStats.getPackageName(), label);

                        UsageStats existingStats =
                                map.get(pkgStats.getPackageName());
                        if (existingStats == null) {
                            map.put(pkgStats.getPackageName(), pkgStats);
                        } else {
                            existingStats.add(pkgStats);
                        }
                    }
                }


            } catch (PackageManager.NameNotFoundException e) {
                // This package may be gone.
            }
        }
        mPackageStats.addAll(map.values());

        return mPackageStats;
    }


    private static long[] dailyRange(){
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new long[]{cal.getTimeInMillis(), timeNow};
    }

    private static long[] weeklyRange(){
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        long end = start + A_DAY > timeNow ? timeNow : start + A_DAY;
        return new long[]{start, end};
        }

    private static long[] monthlyRange(){
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new long[]{cal.getTimeInMillis(), timeNow};
        }

    private static long[] getYesterday() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeNow - A_DAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        long end = start + A_DAY > timeNow ? timeNow : start + A_DAY;
        return new long[]{start, end};
    }

//    public static void SetAlarms(Context context, long minimumTime) {
//        Calendar calender = Calendar.getInstance();
//        int month = calender.get(Calendar.MONTH);
//        int day = calender.get(Calendar.DAY_OF_MONTH);
//        int year = calender.get(Calendar.YEAR);
//        int minute = (int) minimumTime/(1000*60);
//        int hours = (minute/60);
//        minute = (minute%60);
//        calender.set(Calendar.MONTH, month);
//        calender.set(Calendar.DAY_OF_MONTH, day);
//        calender.set(Calendar.YEAR, year);
//        calender.set(Calendar.HOUR, calender.get(Calendar.HOUR)+hours);
//        calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE)+minute);
//        calender.set(Calendar.SECOND, 0);
//        Log.v("alarm2","start: "+calender.get(Calendar.YEAR)+
//                "/"+calender.get(Calendar.MONTH)+
//                "/"+calender.get(Calendar.DAY_OF_MONTH)+
//                "   "+calender.get(Calendar.HOUR)+
//                ":"+calender.get(Calendar.MINUTE));
//
//        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 125, intent, 0);
//
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
//
//    }

    public static void SetAlarm(Context context){
        Log.v("alarm1","start alarm");
        final int PERIOD=60000;
        AlarmManager mgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(context, AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+60000, PERIOD, pi);
    }
}
