package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;

public class RoomController {

    public static void CreateRoom() {

    }

    public static void OpenRoom() {

    }

    public static void changeName() {

    }

    public static void addMember() {

    }

    public static void deleteRoom() {

    }

    public static void reportRoom() {

    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.issue);
        return server + url[pos];
    }
}