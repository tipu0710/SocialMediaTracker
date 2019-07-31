package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;

public class Alert extends AppCompatActivity {

    TextView appNameTv;
    ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Intent intent = getIntent();
        String appName = intent.getStringExtra("appName");
        String packageName = intent.getStringExtra("packageName");
        appNameTv = findViewById(R.id.app_name_service_tv);
        appIcon = findViewById(R.id.app_icon_service);
        appNameTv.setText(appName);
        appIcon.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,this));
    }
}
