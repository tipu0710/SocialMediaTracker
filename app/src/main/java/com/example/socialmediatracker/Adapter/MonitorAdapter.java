package com.example.socialmediatracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.DBoperation.DatabaseModel;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.example.socialmediatracker.helper.AppInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jackandphantom.circularimageview.CircleImage;

import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

import static com.example.socialmediatracker.Activities.SignupActivity.CHILD;
import static com.example.socialmediatracker.Activities.SignupActivity.PARENT;


public class MonitorAdapter  extends RecyclerView.Adapter<MonitorAdapter.ViewHolder> {
    private Context context;
    private List<AppInformation> appInformationList;
    private String deviceToken;

    public MonitorAdapter(Context context, List<AppInformation> appInformationList, String deviceToken) {
        this.context = context;
        this.appInformationList = appInformationList;
        this.deviceToken = deviceToken;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_layout, parent, false);
        return new MonitorAdapter.ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.appName.setText(appInformationList.get(position).getAppName());
        holder.appUsagesTime.setText(AppInfo.getTime(appInformationList.get(position).getUsagesTime()));
        holder.appIcon.setImageBitmap(AppInfo.getImage(appInformationList.get(position).getAppIcon()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(appInformationList.get(position).getPackageName(), context, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInformationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, appUsagesTime, lastTimeUsages;
        CardView view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appUsagesTime = itemView.findViewById(R.id.app_usages_time);
            lastTimeUsages = itemView.findViewById(R.id.app_last_usages_time);
            view = itemView.findViewById(R.id.view);
        }
    }

    private void showDialog(final String packageName, final Context context, final int position) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View dialogView =LayoutInflater.from(context).inflate(R.layout.app_info_dialoge, null);

        Button cancelBTN = dialogView.findViewById(R.id.button_cancle);
        Button acceptBTN = dialogView.findViewById(R.id.button_save);
        TextView appNameD = dialogView.findViewById(R.id.app_name_dialog);
        ImageView appIcon = dialogView.findViewById(R.id.app_icon_dialog);

        appIcon.setImageBitmap(AppInfo.getImage(appInformationList.get(position).getAppIcon()));
        CircularProgressIndicator circularProgressIndicator = dialogView.findViewById(R.id.circular_progress);
        final EditText hourEt = dialogView.findViewById(R.id.hour);
        final EditText minuteEt = dialogView.findViewById(R.id.minute);

        appNameD.setText(AppInfo.GetAppName(packageName,context));
        long time = appInformationList.get(position).getFixedTime();
        int minute = (int) time/(1000*60);
        int hours = (minute/60);
        minute = (minute%60);
        hourEt.setHint("hh");
        hourEt.setText(String.valueOf(hours));
        minuteEt.setHint("mm");
        minuteEt.setText(String.valueOf(minute));
        double timeUsed = appInformationList.get(position).getUsagesTime();
        long timeFixed = appInformationList.get(position).getFixedTime();
        circularProgressIndicator.setProgress(timeUsed, timeFixed);

        if (timeFixed>timeUsed){
            circularProgressIndicator.setProgressColor(Color.parseColor("#FF4CAF50"));
            circularProgressIndicator.setDotColor(Color.parseColor("#FF4CAF50"));
        }else {
            circularProgressIndicator.setProgressColor(Color.parseColor("#ed2e2e"));
            circularProgressIndicator.setDotColor(Color.parseColor("#ed2e2e"));
        }
        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String h = hourEt.getText().toString();
                String m = minuteEt.getText().toString();
                if (h.isEmpty()){
                    h="0";
                }

                if (m.isEmpty()){
                    m = "0";
                }

                if (m.equals("0") && h.equals("0")){
                    minuteEt.setError("Please enter the value!");
                }else {
                    long d = (Long.parseLong(h)*3600*1000)+(Long.parseLong(m)*60*1000);
                    appInformationList.get(position).setFixedTime(d);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String UID = null;
                    if (mAuth!=null){
                        UID = mAuth.getCurrentUser().getUid();
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(UID).
                            child("child").
                            child(deviceToken).
                            child("Data");
                    databaseReference.setValue(appInformationList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.v("UploadData", "Success: "+task.isSuccessful());
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }else {
                                Log.v("UploadData", "Failed: "+task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }
}
