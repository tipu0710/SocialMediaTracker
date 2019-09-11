package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;

public class StartActivity extends AppCompatActivity {

    ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        btn = findViewById(R.id.get_started_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, PermissionActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppInfo.PreferencesHelper.isInstalledBefore(this)){
            startActivity(new Intent(StartActivity.this, PermissionActivity.class));
        }
    }
}
