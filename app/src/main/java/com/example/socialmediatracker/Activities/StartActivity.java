package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.socialmediatracker.R;

public class StartActivity extends AppCompatActivity {

    public static final String STARTED_PRE = "startedPreference";
    public static final String IS_OLD = "isOld";
    ImageButton btn;
    SharedPreferences pref;

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
        pref = getSharedPreferences(STARTED_PRE, MODE_PRIVATE);
        if (pref.getBoolean(IS_OLD, false)){
            startActivity(new Intent(StartActivity.this, PermissionActivity.class));
        }
    }
}
