package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.socialmediatracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmptyActivity extends AppCompatActivity {

    private boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        TextView textView = findViewById(R.id.open_settings);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        checkPermission();

        if (!permission){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            sleep(1000);
                            if (hasUsageStatsPermission(EmptyActivity.this) && !permission){
                                startActivity(new Intent(EmptyActivity.this, Main2Activity.class));
                                permission = true;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (permission){
                thread.interrupt();
            }

            thread.start();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null || !currentUser.isEmailVerified()){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOps != null) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    void requestUsageStatsPermission() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private void checkPermission(){
        if (hasUsageStatsPermission(EmptyActivity.this)){
            startActivity(new Intent(EmptyActivity.this, Main2Activity.class));
            permission = true;
        }else {
            requestUsageStatsPermission();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
