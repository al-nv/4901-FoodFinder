package com.example.onlinestore;

import android.app.Application;

import com.onesignal.OneSignal;

public class YourAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).init();
    }
}
