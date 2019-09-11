package com.example.socialmediatracker.background;

import android.app.usage.UsageStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatracker.Activities.Alert;
import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.example.socialmediatracker.helper.AppInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;
import java.util.List;

import static com.example.socialmediatracker.Activities.SignupActivity.CHILD;
import static com.example.socialmediatracker.Activities.SignupActivity.PARENT;

public class AlarmReceiver extends BroadcastReceiver {
    String deviceToken;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("ExtraTime","start receiver");
        Intent x = new Intent(context, Alert.class);
        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DBcreation dBcreation = new DBcreation(context);

        deviceToken = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ArrayList<DatabaseModel> databaseModels = dBcreation.getAllData();
        ArrayList<String> pkgList = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        list.add("com.google.android.youtube");
        list.add("com.google.android.apps.tachyon");
        list.add("com.google.android.talk");
        list.add("com.android.chorme");

        for (int i = 0; i< databaseModels.size(); i++){
            pkgList.add(databaseModels.get(i).getPackageName());
        }

        AppChecker appChecker = new AppChecker();
        String foregroundPackageName = appChecker.getForegroundApp(context);

        PackageManager mPm = context.getPackageManager();
        boolean isSystemApp = false;
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = mPm.getApplicationInfo(foregroundPackageName, 0);
            isSystemApp = ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (list.contains(foregroundPackageName)||!isSystemApp && !foregroundPackageName.equals(context.getPackageName())){
            UsageStats usageStats = AppInfo.getUsagesStatusByPackage(context, foregroundPackageName);
            if (usageStats!= null){
                if (!pkgList.contains(foregroundPackageName) && !isSystemApp){
                    dBcreation.AddAppInfo(new DatabaseModel(foregroundPackageName, 2*3600*1000));
                }else {
                    DatabaseModel databaseModel = dBcreation.getDataByPackage(foregroundPackageName);
                    long saveTime = databaseModel.getTime();
                    if (foregroundPackageName.equals(usageStats.getPackageName())){
                        if (saveTime<= usageStats.getTotalTimeInForeground() && usageStats.getTotalTimeInForeground()>6*60*1000 && !foregroundPackageName.equals(context.getPackageName())){
                            x.putExtra("appName", AppInfo.GetAppName(foregroundPackageName,context));
                            x.putExtra("packageName", foregroundPackageName);
                            x.putExtra("usedTime", usageStats.getTotalTimeInForeground());
                            context.startActivity(x);
                        }
                    }
                }
            }
        }
        int step = AppInfo.PreferencesHelper.getStep(context);
        if (step>=4 && AppInfo.PreferencesHelper.isChild(context) && AppInfo.PreferencesHelper.isParentalControlOn(context)){
            Log.v("UploadData", "DataUpload step if: "+step);
            uploadData(context);
            //uploadUpdateData(context, deviceToken);
        }else if (step==2 && AppInfo.PreferencesHelper.isChild(context) && AppInfo.PreferencesHelper.isParentalControlOn(context)){
            Log.v("UploadData", "DataUpload step if: "+step);
            AppInfo.PreferencesHelper.setStep(context, ++step);
            uploadData(context);
        }else if (AppInfo.PreferencesHelper.isChild(context) && AppInfo.PreferencesHelper.isParentalControlOn(context)){
            Log.v("UploadData", "DataUpload step else: "+step);
            AppInfo.PreferencesHelper.setStep(context, ++step);
        }
    }

    private void uploadData(Context context) {
        Log.v("UploadData", "DataUpload start ");

        if(AppInfo.PreferencesHelper.isChild(context)){
            if (AppInfo.isConnectedToNetwork(context)){
                updateOfflineDatabase(context, deviceToken);
            }else {
                Log.v("UploadData", "Uploader: net problem");
            }
        }else {
            Log.v("UploadData", "Parent Device");
        }
    }

    public static void updateOfflineDatabase(final Context context, final String deviceToken) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String UID = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(UID);
        /*new GetDeviceId().execute();*/
        final List<DatabaseModel> databaseModels = new ArrayList<>();
        databaseReference.child(CHILD).child(deviceToken).child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String packageName = snapshot.child("packageName").getValue().toString();
                    long fixedTime = (long) snapshot.child("fixedTime").getValue();
                    databaseModels.add(new DatabaseModel(packageName, fixedTime));
                }
                update(context, databaseModels, deviceToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void update(Context context, List<DatabaseModel> databaseModels, String deviceToken) {
        for (DatabaseModel databaseModel: databaseModels){
            new DBcreation(context).UpdateAppBasicInfo(databaseModel);
        }
        if (AppInfo.PreferencesHelper.getStep(context)>=4){
            uploadUpdateData(context, deviceToken);
        }
    }

    private static void uploadUpdateData(final Context context, final String deviceToken) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String UID = null;
        if (mAuth!=null){
            UID = mAuth.getCurrentUser().getUid();
        }
        List<AppInformation> appInformationList = AppInfo.getAppInformationList(context);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(UID).
            child("child").
            child(deviceToken).
            child("Data");
        databaseReference.setValue(appInformationList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.v("UploadData", "Success: "+task.isSuccessful());
                    AppInfo.PreferencesHelper.setStep(context, 0);
                }else {
                    Log.v("UploadData", "Failed: "+task.getException().getMessage());
                }
            }
        });
    }
}
