package com.example.socialmediatracker.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.background.AlarmReceiver;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AppInfo {
    private static final long A_DAY = 86400 * 1000;

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
            Name = (String)packageManager.getApplicationLabel(applicationInfo);

        }catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return Name;
    }


    private static boolean isSystemApp(ApplicationInfo applicationInfo){
        boolean b=false;

        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            b = true;
        }
        /*if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            b = true;
        }*/
        return b;
    }

    public static UsageStats getUsagesStatusByPackage(Context context, String packageName){
        long[] range = dailyRange();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> stats = new ArrayList<>();
        List<AppInformation> appInformationList = new ArrayList<>();

        if (mUsageStatsManager != null) {
            stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, range[0], range[1]);
        }

        for (UsageStats usageStats: stats){
            if (usageStats.getPackageName().equals(packageName)){
                return usageStats;
            }
        }

        return null;
    }

    public static List<AppInformation> getAppInformationList(Context context){
        long[] range = dailyRange();
        List<String>allAppList = AppInfo.getAllInstalledApp(context);
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> stats = new ArrayList<>();
        List<AppInformation> appInformationList = new ArrayList<>();

        if (mUsageStatsManager != null) {
            stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, range[0], range[1]);
        }

        for (String packageName: allAppList){
            int i;
            for (i=0; i<stats.size(); i++){
                if (packageName.equals(stats.get(i).getPackageName())){
                    AppInformation appInformation = new AppInformation();
                    appInformation.setAppName(AppInfo.GetAppName(stats.get(i).getPackageName(), context));
                    appInformation.setPackageName(stats.get(i).getPackageName());
                    appInformation.setUsagesTime(stats.get(i).getTotalTimeInForeground());
                    appInformation.setFixedTime(new DBcreation(context).getDataByPackage(packageName).getTime());
                    Drawable drawable = AppInfo.getAppIconByPackageName(stats.get(i).getPackageName(), context);
                    Bitmap myLogo = AppInfo.drawableToBitmap(drawable);
                    appInformation.setAppIcon(AppInfo.getBitmapAsString(myLogo));
                    appInformationList.add(appInformation);
                    break;
                }
            }
            if (i==stats.size()){
                AppInformation appInformation = new AppInformation();
                appInformation.setAppName(AppInfo.GetAppName(packageName, context));
                appInformation.setPackageName(packageName);
                appInformation.setUsagesTime(0);
                appInformation.setFixedTime(new DBcreation(context).getDataByPackage(packageName).getTime());
                Drawable drawable = AppInfo.getAppIconByPackageName(packageName, context);
                Bitmap myLogo = AppInfo.drawableToBitmap(drawable);
                appInformation.setAppIcon(AppInfo.getBitmapAsString(myLogo));
                appInformationList.add(appInformation);
            }
        }

        return appInformationList;
    }


    public static ArrayList<UsageStats> AppUpdateStatus(Context context, int INTERVAL) {
        ArrayList<UsageStats> mPackageStats = new ArrayList<>();
        long[] range;
        if (INTERVAL == UsageStatsManager.INTERVAL_MONTHLY){
            range = monthlyRange();
        }else if (INTERVAL == UsageStatsManager.INTERVAL_WEEKLY){
            range = weeklyRange();
        }else if (INTERVAL == UsageStatsManager.INTERVAL_DAILY){
            range = dailyRange();
        }else {
            range = getYesterday();
        }

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager mPm = context.getPackageManager();

        List<UsageStats> stats = new ArrayList<>();


        if (mUsageStatsManager != null) {
            stats = mUsageStatsManager.queryUsageStats(INTERVAL, range[0], range[1]);
        }
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


    public static void SetAlarm(Context context, int extraTime){
        final int PERIOD=60000;
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(context, AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(context, 312, i, 0);
        Calendar calender = Calendar.getInstance();
        Calendar calender1 = Calendar.getInstance();
        calender.set(Calendar.MONTH, calender1.get(Calendar.MONTH));
        calender.set(Calendar.DAY_OF_MONTH, calender1.get(Calendar.DAY_OF_MONTH));
        calender.set(Calendar.YEAR, calender1.get(Calendar.YEAR));
        calender.set(Calendar.HOUR, calender1.get(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender1.get(Calendar.MINUTE));
        calender.set(Calendar.SECOND, 0);
        long alarmTime = System.currentTimeMillis()+60000+(extraTime*60*1000);
        if (mgr != null) {
            mgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    alarmTime, PERIOD, pi);
            Log.v("ExtraTime", alarmTime+"");
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static String getBitmapAsString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();

        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    public static Bitmap getImage(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static boolean isConnectedToNetwork(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null && wifi.isConnectedOrConnecting()) {
            return true;
        } else return mobile != null && mobile.isConnectedOrConnecting();
    }

    public static String getTime(long time){
        long totalSecs = time/1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;


        String hourString = (hours == 0)
                ? "00"
                : ((hours < 10)
                ? "0" + hours
                : "" + hours);

        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);

        return hourString + ":" + minsString + ":" + secsString;
    }

    public static List<String> getAllInstalledApp(Context context){
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> packagesSystem = pm.getInstalledApplications(PackageManager.MATCH_SYSTEM_ONLY);
        List<String> allPackages = new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();
        list.add("com.google.android.youtube");
        list.add("com.google.android.apps.tachyon");
        list.add("com.google.android.talk");
        list.add("com.android.chorme");

        for (ApplicationInfo applicationInfo: packages){
            allPackages.add(applicationInfo.packageName);
        }

        for (ApplicationInfo applicationInfo: packagesSystem){
            if (!list.contains(applicationInfo.packageName)){
                allPackages.remove(applicationInfo.packageName);
            }
        }

        return allPackages;
    }

    public static class PreferencesHelper{

        static final String PARENTAL_CONTROL = "parental_control";
        static final String IS_PARENT = "is_parent";
        static final String STARTED_PRE = "startedPreference";
        static final String IS_OLD = "isOld";
        static final String IS_CHILD = "isChild";
        static final String READ_TERMS_AND_CONDITIONS_PRE = "terms_and_conditions";
        static final String IS_READ = "isRead";
        static final String STEP = "step";
        static final String STEP_PREF = "step_pref";
        static final String USER_INFO = "user_info";
        static final String USER_MAIL = "user_mail";
        static final String USER_SECU = "user_secu";
        static final String USER_IS_FIRST = "first_time";

        public static boolean isParentalControlOn(Context context){
            SharedPreferences preferences = context.getSharedPreferences(PARENTAL_CONTROL, MODE_PRIVATE);
            return preferences.getBoolean(IS_PARENT,false);
        }
        public static boolean isChild(Context context){
            SharedPreferences preferences = context.getSharedPreferences(PARENTAL_CONTROL, MODE_PRIVATE);
            return preferences.getBoolean(IS_CHILD,true);
        }
        public static int getStep(Context context){
            SharedPreferences preferences = context.getSharedPreferences(STEP_PREF, MODE_PRIVATE);
            return preferences.getInt(STEP,4);
        }
        public static boolean getIsFirstTime(Context context){
            SharedPreferences preferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
            return preferences.getBoolean(USER_IS_FIRST,true);
        }
        public static String getUserMail(Context context){
            SharedPreferences preferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
            return preferences.getString(USER_MAIL,null);
        }
        public static String getUsersec(Context context){
            SharedPreferences preferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
            return preferences.getString(USER_SECU,null);
        }

        public static void saveInfo(Context context, String mail, String sec){
            SharedPreferences preferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putString(USER_MAIL, mail);
            ed.putString(USER_SECU, sec);
            ed.apply();
        }
        public static void setIsFirstTime(Context context, boolean isFirstTime){
            SharedPreferences preferences = context.getSharedPreferences(USER_INFO, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(USER_IS_FIRST, isFirstTime);
            ed.apply();
        }


        public static void setStep(Context context, int step){
            SharedPreferences preferences = context.getSharedPreferences(STEP_PREF, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putInt(STEP, step);
            ed.apply();
        }

        public static void setIsChild(Context context, boolean isChild){
            SharedPreferences preferences = context.getSharedPreferences(PARENTAL_CONTROL, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(IS_CHILD, isChild);
            ed.apply();
        }

        public static void changeParentalControl(Context context){
            SharedPreferences preferences = context.getSharedPreferences(PARENTAL_CONTROL, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(IS_PARENT, !isParentalControlOn(context));
            ed.apply();
        }
        public static void setParentalControl(Context context, boolean isParentalOn){
            SharedPreferences preferences = context.getSharedPreferences(PARENTAL_CONTROL, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(IS_PARENT, isParentalOn);
            ed.apply();
        }

        public static void setAsInstalledBefore(Context context){
            SharedPreferences preferences = context.getSharedPreferences(STARTED_PRE, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(IS_OLD, true);
            ed.apply();
        }

        public static boolean isInstalledBefore(Context context){
            SharedPreferences preferences = context.getSharedPreferences(STARTED_PRE, MODE_PRIVATE);
            return preferences.getBoolean(IS_OLD, false);
        }

        public static void setTermsConditionsAsRead(Context context){
            SharedPreferences preferences = context.getSharedPreferences(READ_TERMS_AND_CONDITIONS_PRE, MODE_PRIVATE);
            SharedPreferences.Editor ed;
            ed = preferences.edit();
            ed.putBoolean(IS_READ, true);
            ed.apply();
        }


        public static boolean isReadTermsConditions(Context context){
            SharedPreferences preferences = context.getSharedPreferences(READ_TERMS_AND_CONDITIONS_PRE, MODE_PRIVATE);
            return preferences.getBoolean(IS_READ, false);
        }
    }
}
