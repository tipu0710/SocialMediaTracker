package com.example.socialmediatracker.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.google.firebase.auth.FirebaseUser;


public class PermissionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        ImageView textView = findViewById(R.id.open_settings);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(hasUsageStatsPermission(PermissionActivity.this)){
            if (!AppInfo.PreferencesHelper.isInstalledBefore(this)){
                startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                finish();
            }else {
                startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null || !currentUser.isEmailVerified()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else {
            if (hasUsageStatsPermission(PermissionActivity.this)){
                startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                finish();
            }
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
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 12);
    }

    private void checkPermission(){
        if(hasUsageStatsPermission(PermissionActivity.this)){
            if (!AppInfo.PreferencesHelper.isInstalledBefore(PermissionActivity.this)){
                startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                finish();
            }else {
                startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                finish();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK){
            if(hasUsageStatsPermission(PermissionActivity.this)){
                if (!AppInfo.PreferencesHelper.isInstalledBefore(this)){
                    startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }
}
