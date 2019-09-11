package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;

public class ParentalControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_control);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        final Switch agreeSwitch = findViewById(R.id.switch_agree);
        Button skipBtn = findViewById(R.id.parent_skip_btn);

        agreeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agreeSwitch.isChecked()){
                    startActivity(new Intent(ParentalControlActivity.this, TermsAndConditionActivity.class));
                    finish();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppInfo.PreferencesHelper.setAsInstalledBefore(ParentalControlActivity.this);
                startActivity(new Intent(ParentalControlActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
