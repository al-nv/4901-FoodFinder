package com.example.onlinestore.gps;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Globle {
    Context context;

    // constructor

    public Globle(Context context) {
        this.context = context;
    }


    public static Boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                //return TYPE_WIFI;
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                //return TYPE_MOBILE;
                return true;
        }
        return false;
    }
}

