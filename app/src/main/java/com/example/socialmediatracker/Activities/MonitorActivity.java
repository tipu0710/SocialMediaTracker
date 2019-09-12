package com.example.socialmediatracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.socialmediatracker.Adapter.MonitorAdapter;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInformation;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.socialmediatracker.Activities.SignupActivity.CHILD;

public class MonitorActivity extends AppCompatActivity {

    public static final String CHILD_DEVICE_ID = "connected_child_did";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppInformation> appInformationList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String deviceToken, UID;
    private TextView warningTv;
    ProgressBar progressBar;
    Sprite doubleBounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        recyclerView = findViewById(R.id.child_data);
        progressBar = findViewById(R.id.progress_monitor);
        warningTv = findViewById(R.id.monitor_warning_tv);
        doubleBounce = new DoubleBounce();
        progressBar.setVisibility(View.VISIBLE);


        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        appInformationList = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(UID);
        /*new GetDeviceId().execute();*/

        databaseReference.child(CHILD_DEVICE_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deviceToken = dataSnapshot.getValue().toString();
                Log.v("DataUpload", deviceToken);
                if (!deviceToken.isEmpty()) {
                    getData();
                }else {
                    warningTv.setVisibility(View.VISIBLE);
                    warningTv.setText("Child is not connected");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getData() {
        databaseReference.child(CHILD).child(deviceToken).child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String appName = snapshot.child("appName").getValue().toString();
                    String packageName = snapshot.child("packageName").getValue().toString();
                    String appIcon = snapshot.child("appIcon").getValue().toString();
                    long usagesTime = (long) snapshot.child("usagesTime").getValue();
                    long fixedTime = (long) snapshot.child("fixedTime").getValue();
                    appInformationList.add(new AppInformation(appName, packageName, appIcon, usagesTime, fixedTime));
                }
                if (appInformationList.size()>0){
                    createList();
                }else {
                    warningTv.setVisibility(View.VISIBLE);
                    warningTv.setText("Nothing to show!");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createList() {
        Log.v("UploadData", appInformationList.size()+"");
        mAdapter = new MonitorAdapter(MonitorActivity.this, appInformationList, deviceToken);
        recyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private class GetDeviceId extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class LoadData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
