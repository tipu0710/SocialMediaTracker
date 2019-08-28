package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.socialmediatracker.R;

import static com.example.socialmediatracker.Activities.StartActivity.IS_OLD;
import static com.example.socialmediatracker.Activities.StartActivity.STARTED_PRE;

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
                changePreference();
                startActivity(new Intent(ParentalControlActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void changePreference(){
        SharedPreferences preferences = getSharedPreferences(STARTED_PRE, MODE_PRIVATE);
        SharedPreferences.Editor ed;
        ed = preferences.edit();
        ed.putBoolean(IS_OLD, true);
        ed.apply();
    }
}
