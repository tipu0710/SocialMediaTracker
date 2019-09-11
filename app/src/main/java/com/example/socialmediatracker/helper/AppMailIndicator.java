package com.example.socialmediatracker.helper;

public class AppMailIndicator {
    private String packageName;
    private int count;

    public AppMailIndicator(String packageName, int count) {
        this.packageName = packageName;
        this.count = count;
    }

    public AppMailIndicator() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
