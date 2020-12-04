package com.example.onlinestore.gps;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.example.onlinestore.activity.MyAlertDialogActivity;

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED") || intent.getAction().matches("oms.gps.status.check")) {
//            Intent pushIntent = new Intent(context, LocalService.class);
//            context.startService(pushIntent);
            ContentResolver contentResolver = context.getContentResolver();
            // Find out what the settings say about which providers are enabled
            int mode = Settings.Secure.getInt(
                    contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            String locationMode = "";
            if (mode != Settings.Secure.LOCATION_MODE_OFF) {
                if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                    locationMode = "Device only. Uses GPS to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                    locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                }
            }



            if(mode == 0){
                Intent i = new Intent(context, MyAlertDialogActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("mode", "disabled");
                context.startActivity(i);
            }else if(mode == 2){
                Intent i = new Intent(context, MyAlertDialogActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("mode", "battery-saving");
                context.startActivity(i);

                // buildAlertMessageNoBatterySavingMode(context);
            }

//            Toast.makeText(context, String.valueOf(mode),
//                    Toast.LENGTH_SHORT).show();
        }
    }
}


