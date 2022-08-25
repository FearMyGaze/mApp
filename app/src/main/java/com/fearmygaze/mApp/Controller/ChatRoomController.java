package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;

public class ChatRoomController {




    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.issue);
        return server + url[pos];
    }
}