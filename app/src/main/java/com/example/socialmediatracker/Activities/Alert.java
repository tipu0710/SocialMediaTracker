package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;

public class Alert extends AppCompatActivity {

    TextView appNameTv;
    ImageView appIcon;
    Button addMoreTimeBtn;
    long usedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String appName = intent.getStringExtra("appName");
        final String packageName = intent.getStringExtra("packageName");
        usedTime = intent.getLongExtra("usedTime",2*3600*1000);
        appNameTv = findViewById(R.id.app_name_service_tv);
        appIcon = findViewById(R.id.app_icon_service);
        addMoreTimeBtn = findViewById(R.id.add_more_btn);
        appNameTv.setText("You are using too much time on "+appName+". Take a break!");
        appIcon.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,this));
        
        addMoreTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Alert.this, "Added more 10 minute!", Toast.LENGTH_SHORT).show();
                AppInfo.SetAlarm(Alert.this, 10);
                finish();
                System.exit(0);
            }
        });
    }
}
