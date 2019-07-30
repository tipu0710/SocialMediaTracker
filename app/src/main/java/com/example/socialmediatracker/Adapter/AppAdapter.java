package com.example.socialmediatracker.Adapter;

import android.app.usage.UsageStats;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
            String packageName = mPackageStats.get(position).getPackageName();
            holder.appIcon.setImageDrawable(AppInfo.getAppIconByPackageName(packageName,context));
            holder.appName.setText(AppInfo.GetAppName(packageName,context));
            holder.appUsagesTime.setText(DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));
            holder.lastTimeUsages.setText(DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
                    System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));

        }
    }

    @Override
    public int getItemCount() {
        return mPackageStats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, appUsagesTime, lastTimeUsages;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appUsagesTime = itemView.findViewById(R.id.app_usages_time);
            lastTimeUsages = itemView.findViewById(R.id.app_last_usages_time);
        }
    }
}
