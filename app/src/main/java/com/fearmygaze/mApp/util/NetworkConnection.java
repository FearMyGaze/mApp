package com.fearmygaze.mApp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkConnection {

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : manager.getAllNetworks()) {
            NetworkInfo networkInfo = manager.getNetworkInfo(network);
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    private static boolean isMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : manager.getAllNetworks()) {
            NetworkInfo networkInfo = manager.getNetworkInfo(network);
            return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static boolean isConnectionAlive(Context context) {
        return isMobileConnected(context) || isWifiConnected(context);
    }

}