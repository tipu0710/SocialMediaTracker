package com.example.socialmediatracker.Adapter;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
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
import com.jackandphantom.circularimageview.CircleImage;

import java.text.DateFormat;
import java.util.ArrayList;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UsageStats> mPackageStats;
    public AppAdapter(Context context, ArrayList<UsageStats> packageList) {
        this.context = context;
        this.mPackageStats = packageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_layout, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UsageStats pkgStats = mPackageStats.get(position);
        if (pkgStats!=null){
            DBcreation dBcreation = new DBcreation(context);
            final String packageName = mPackageStats.get(position).getPackageName();
            long fixedTime = dBcreation.getDataByPackage(packageName).getTime()/1000;
            long usedTime = pkgStats.getTotalTimeInForeground() / 1000;
            holder.appIcon.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,context));
            holder.appName.setText(AppInfo.GetAppName(packageName,context));
            if (fixedTime>usedTime){
                holder.appUsagesTime.setTextColor(Color.parseColor("#FF4CAF50"));
            }else {
                holder.appUsagesTime.setTextColor(Color.parseColor("#ed2e2e"));
            }
            holder.appUsagesTime.setText(DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));
            holder.lastTimeUsages.setText(DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
                    System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(packageName, context, pkgStats);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mPackageStats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, appUsagesTime, lastTimeUsages;
        CardView view;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appUsagesTime = itemView.findViewById(R.id.app_usages_time);
            lastTimeUsages = itemView.findViewById(R.id.app_last_usages_time);
            view = itemView.findViewById(R.id.view);
        }
    }

    private void showDialog(final String packageName, final Context context, UsageStats usageStats) {

        final DBcreation dBcreation = new DBcreation(context);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View dialogView =LayoutInflater.from(context).inflate(R.layout.app_info_dialoge, null);

        Button cancelBTN = dialogView.findViewById(R.id.button_cancle);
        Button acceptBTN = dialogView.findViewById(R.id.button_save);
        TextView appNameD = dialogView.findViewById(R.id.app_name_dialog);
        CircleImage appIcon = dialogView.findViewById(R.id.app_icon_dialog);
        appIcon.setBackground(AppInfo.getAppIconByPackageName(packageName,context));
        CircularProgressIndicator circularProgressIndicator = dialogView.findViewById(R.id.circular_progress);
        final EditText hourEt = dialogView.findViewById(R.id.hour);
        final EditText minuteEt = dialogView.findViewById(R.id.minute);

        appNameD.setText(AppInfo.GetAppName(packageName,context));
        DatabaseModel databaseModel = dBcreation.getDataByPackage(packageName);
        long time = databaseModel.getTime();
        int minute = (int) time/(1000*60);
        int hours = (minute/60);
        minute = (minute%60);
        hourEt.setHint("hh");
        hourEt.setText(String.valueOf(hours));
        minuteEt.setHint("mm");
        minuteEt.setText(String.valueOf(minute));
        long timeUsed = usageStats.getTotalTimeInForeground()/60000;
        long timeFixed = dBcreation.getDataByPackage(packageName).getTime()/60000;
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
                    boolean b = dBcreation.UpdateAppBasicInfo(new DatabaseModel(packageName, d));
                    if (b){
                        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }
}
