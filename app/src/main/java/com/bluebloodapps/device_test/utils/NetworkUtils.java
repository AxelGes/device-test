package com.bluebloodapps.device_test.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

public final class NetworkUtils {
    private Context context = null;

    private static NetworkUtils instance = null;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public static NetworkUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtils(context);
        }
        return instance;
    }

    public boolean isMobileOn() {
        try{
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo i = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (i == null) {
                return false;
            }
            if(i.isAvailable() && i.getDetailedState() == NetworkInfo.DetailedState.CONNECTED){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean isBluetoothOn(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }


    public boolean isWifiOn() {
        try{
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo i = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (i == null) {
                return false;
            }
            if(i.isAvailable() && i.getDetailedState() == NetworkInfo.DetailedState.CONNECTED){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    // turn on use network for location
    public void turnNetWorkLocationOn(Activity activity) {
        String provider = Settings.Secure.getString(
                activity.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("1"));
            activity.sendBroadcast(poke);
        }
        // String provider = Settings.Secure.getString(
        // activity.getContentResolver(),
        // Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        // Log.e("provider", "ok " + provider);
        // final Intent poke = new Intent();
        // poke.setClassName("com.android.settings",
        // "com.android.settings.widget.SettingsAppWidgetProvider");
        // poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        // poke.setData(Uri.parse("3"));
        // activity.sendBroadcast(poke);
        // Settings.Secure.setLocationProviderEnabled(
        // activity.getContentResolver(),
        // LocationManager.NETWORK_PROVIDER, true);
    }

    // turn on GPS
    // public void turnGPSOn(Activity activity) {
    //
    // String provider = Settings.Secure.getString(
    // activity.getContentResolver(),
    // Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    // if (!provider.contains("gps")) { // if gps is disabled
    // final Intent poke = new Intent();
    // poke.setClassName("com.android.settings",
    // "com.android.settings.widget.SettingsAppWidgetProvider");
    // poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
    // poke.setData(Uri.parse("3"));
    // activity.sendBroadcast(poke);
    // }
    //
    // }
}
