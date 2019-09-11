package com.example.socialmediatracker.helper;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
@IgnoreExtraProperties
public class AppInformation {
    private String packageName;
    private String appIcon;
    private long usagesTime;
    private String appName;
    private long fixedTime;
    @ServerTimestamp Date timestamp;

    public AppInformation() {
    }

    public AppInformation(String appName, String packageName, String appIcon, long usagesTime, long fixedTime) {
        this.packageName = packageName;
        this.appIcon = appIcon;
        this.usagesTime = usagesTime;
        this.appName = appName;
        this.fixedTime = fixedTime;
    }

    public long getFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(long fixedTime) {
        this.fixedTime = fixedTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public long getUsagesTime() {
        return usagesTime;
    }

    public void setUsagesTime(long usagesTime) {
        this.usagesTime = usagesTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
