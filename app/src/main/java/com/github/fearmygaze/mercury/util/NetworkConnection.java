package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import androidx.annotation.IntRange;

public class NetworkConnection {

    private static boolean isWifiNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : manager.getAllNetworks()) {
            NetworkInfo networkInfo = manager.getNetworkInfo(network);
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    private static boolean isMobileNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : manager.getAllNetworks()) {
            NetworkInfo networkInfo = manager.getNetworkInfo(network);
            return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static boolean isConnectionAlive(Context context) {
        return isWifiNetworkConnected(context) || isMobileNetworkConnected(context);
    }

    public static boolean preferredChoiceAlive(@IntRange(from = 1, to = 2) int choice, Context context) { //TODO: This will be the user choice and then we update them
        switch (choice) {
            case 1:
                return isWifiNetworkConnected(context);
            case 2:
                return isMobileNetworkConnected(context);
            default:
                return isConnectionAlive(context);
        }
    }

}