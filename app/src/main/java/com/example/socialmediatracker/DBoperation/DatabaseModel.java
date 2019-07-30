package com.example.socialmediatracker.DBoperation;

public class DatabaseModel {
    private String packageName;
    private long time;

    public DatabaseModel(String packageName, long time) {
        this.packageName = packageName;
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
