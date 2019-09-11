package com.example.socialmediatracker.helper;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Data {
    private List<AppInformation> appInformations;
    private @ServerTimestamp Date date;

    public Data(List<AppInformation> appInformations) {
        this.appInformations = appInformations;
    }

    public List<AppInformation> getAppInformations() {
        return appInformations;
    }

    public void setAppInformations(List<AppInformation> appInformations) {
        this.appInformations = appInformations;
    }
}
