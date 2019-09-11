package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;

public class TermsAndConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        final CheckBox read, accept;
        ImageButton continueBtn;

        read = findViewById(R.id.read_check);
        accept = findViewById(R.id.accept_check);
        continueBtn = findViewById(R.id.terms_continue_btn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (read.isChecked()){
                    if (accept.isChecked()){
                        AppInfo.PreferencesHelper.setAsInstalledBefore(getApplicationContext());
                        AppInfo.PreferencesHelper.setTermsConditionsAsRead(getApplicationContext());
                        startActivity(new Intent(TermsAndConditionActivity.this, LoginActivity.class));
                        finish();
                    }else {
                        Toast.makeText(TermsAndConditionActivity.this, "To continue accept terms and conditions!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(TermsAndConditionActivity.this, "Read carefully terms and conditions first!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppInfo.PreferencesHelper.isReadTermsConditions(this)){
            startActivity(new Intent(TermsAndConditionActivity.this, LoginActivity.class));
        }
    }
}
