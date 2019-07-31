package com.example.socialmediatracker.Adapter;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.content.Context;
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

import java.text.DateFormat;
import java.util.ArrayList;


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
        UsageStats pkgStats = mPackageStats.get(position);
        if (pkgStats!=null){
            final String packageName = mPackageStats.get(position).getPackageName();
            holder.appIcon.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,context));
            holder.appName.setText(AppInfo.GetAppName(packageName,context));
            holder.appUsagesTime.setText(DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));
            holder.lastTimeUsages.setText(DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
                    System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(packageName, context);

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

    public void showDialog(final String packageName, final Context context) {

        final DBcreation dBcreation = new DBcreation(context);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View dialogView =LayoutInflater.from(context).inflate(R.layout.app_info_dialoge, null);

        Button cancelBTN = dialogView.findViewById(R.id.button_cancle);
        Button acceptBTN = dialogView.findViewById(R.id.button_save);
        TextView appNameD = dialogView.findViewById(R.id.app_name_dialog);
        ImageView imageView = dialogView.findViewById(R.id.circular_progress);
        final EditText hour = dialogView.findViewById(R.id.editText2);

        appNameD.setText(AppInfo.GetAppName(packageName,context));
        DatabaseModel databaseModel = dBcreation.getDataByPackage(packageName);
        long time = databaseModel.getTime();
        int minute = (int) time/(1000*60);
        int hours = (minute/60);
        minute = (minute%60);
        hour.setHint("hh:MM");
        hour.setText(hours+":"+minute);
        imageView.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,context));


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
                String h = hour.getText().toString();
                if (h.isEmpty()){
                    hour.setError("Please enter the value!");
                }else {
                    String s = hour.getText().toString();
                    String[] sb = s.split(":");
                    long d = (Long.parseLong(sb[0])*3600*1000)+(Long.parseLong(sb[1])*60*1000);
                    boolean b = dBcreation.UpdateAppBasicInfo(new DatabaseModel(packageName, d));
                    if (b){
                        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }
}
