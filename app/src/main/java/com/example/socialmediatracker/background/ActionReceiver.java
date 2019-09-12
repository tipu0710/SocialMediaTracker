package com.example.socialmediatracker.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.socialmediatracker.Activities.AppUsagesActivity;
import com.example.socialmediatracker.helper.AppInfo;

import static com.example.socialmediatracker.background.AlarmReceiver.ADD_TEN_MINUTES;
import static com.example.socialmediatracker.background.AlarmReceiver.GOTO_DETAILS;

public class ActionReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action=intent.getAction();
        Log.v("ExtraTime", action);
        if(action.equals(ADD_TEN_MINUTES)){
            performAction1();
        }else if (action.equals(GOTO_DETAILS)){
            performAction2();
        }

    }

    private void performAction2() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(6654);
        context.startActivity(new Intent(context, AppUsagesActivity.class));
    }

    public void performAction1(){
        Toast.makeText(context, "Added 10 more minutes!", Toast.LENGTH_SHORT).show();
        AppInfo.SetAlarm(context, 10);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(6654);
    }
}
