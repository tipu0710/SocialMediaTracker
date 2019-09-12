package com.example.socialmediatracker.background;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.socialmediatracker.Activities.Alert;
import com.example.socialmediatracker.Activities.AppUsagesActivity;
import com.example.socialmediatracker.Activities.MainActivity;
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

public class AlarmReceiver extends BroadcastReceiver {
    String deviceToken;
    Context context;
    private String CHANNEL_ID;
    public static final String ADD_TEN_MINUTES = "add_ten_minutes";
    public static final String GOTO_DETAILS = "goto_details";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.v("ExtraTime","start receiver");
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
                        if (saveTime<= usageStats.getTotalTimeInForeground() &&
                                usageStats.getTotalTimeInForeground()>6*60*1000 &&
                                !foregroundPackageName.equals(context.getPackageName())){
                            String appName = AppInfo.GetAppName(foregroundPackageName,context);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                String msg = "You are spending too much time on "+appName;
                                createNotification(msg);
                            }else {
                                Intent x = new Intent(context, Alert.class);
                                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                x.putExtra("appName", appName);
                                x.putExtra("packageName", foregroundPackageName);
                                x.putExtra("usedTime", usageStats.getTotalTimeInForeground());
                                context.startActivity(x);
                            }
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


    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }


    public void createNotification(String message) {

        CHANNEL_ID = context.getString(R.string.app_name);
        if (message != null ) {
            createNotificationChannel();

            Intent intentGo = new Intent(context, MainActivity.class);
            intentGo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            PendingIntent pendingIntentGo = PendingIntent.getActivity(context, 556699, intentGo, 0);

            Intent intentUse = new Intent(context, AppUsagesActivity.class);
            intentGo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            PendingIntent pendingIntentUse = PendingIntent.getActivity(context, 556699, intentUse, PendingIntent.FLAG_UPDATE_CURRENT);



            Intent intent = new Intent(context, ActionReceiver.class);
            intent.setAction(ADD_TEN_MINUTES);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 556699, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Warning!")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntentGo)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setAutoCancel(true)
                    .addAction(R.mipmap.ic_launcher, "+10 MINUTES", pendingIntent)
                    .addAction(R.mipmap.ic_launcher, "DETAILS", pendingIntentUse)
                    .setOngoing(true);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            mBuilder.setSound(uri);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = 6654;

            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

}
