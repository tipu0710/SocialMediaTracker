package com.example.socialmediatracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.socialmediatracker.R;

import static com.example.socialmediatracker.Activities.StartActivity.IS_OLD;
import static com.example.socialmediatracker.Activities.StartActivity.STARTED_PRE;

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
                        changePreference();
                        startActivity(new Intent(TermsAndConditionActivity.this, LoginActivity.class));
                        finish();
                    }else {
                        Toast.makeText(TermsAndConditionActivity.this, "Accept terms and conditions first!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(TermsAndConditionActivity.this, "Read carefully terms and conditions first!", Toast.LENGTH_LONG).show();
                }
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
