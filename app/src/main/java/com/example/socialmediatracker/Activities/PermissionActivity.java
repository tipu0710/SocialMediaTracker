package com.example.socialmediatracker.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialmediatracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.socialmediatracker.Activities.StartActivity.IS_OLD;
import static com.example.socialmediatracker.Activities.StartActivity.STARTED_PRE;


public class PermissionActivity extends AppCompatActivity {

    private SharedPreferences startPre;

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
        startPre = getSharedPreferences(STARTED_PRE, MODE_PRIVATE);
        if(hasUsageStatsPermission(PermissionActivity.this)){
            boolean b = startPre.getBoolean(IS_OLD, false);
            if (!b){
                startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                finish();
            }else {
                Log.v("isOld","onStart: "+startPre.getBoolean(IS_OLD, false));
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
            if (startPre.getBoolean(IS_OLD, false)){
                startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                finish();
            }else {
                Log.v("isOld","CheckPermission: "+startPre.getBoolean(IS_OLD, false));
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
                if (startPre.getBoolean(IS_OLD, false)){
                    startActivity(new Intent(PermissionActivity.this, ParentalControlActivity.class));
                    finish();
                }else {
                    Log.v("isOld","OnActivity: "+startPre.getBoolean(IS_OLD, false));
                    startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }
}
