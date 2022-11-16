package com.github.fearmygaze.mercury.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.fearmygaze.mercury.BuildConfig;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.interfaces.IVolley;
import com.github.fearmygaze.mercury.util.NetworkConnection;
import com.github.fearmygaze.mercury.util.RequestSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IssueController {

    public static void uploadBug(int id, String description, String device, boolean consent, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);
        body.put("description", description);
        body.put("device", device);
        body.put("consent", consent);
        body.put("state", "bug");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(0, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iVolley.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iVolley.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iVolley.onError(context.getString(R.string.jsonError));
            }
        }, error -> iVolley.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iVolley.onError(context.getString(R.string.sampleLastMessage));
        }
    }

    public static void uploadFeature(int id, String description, boolean consent, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);
        body.put("description", description);
        body.put("consent", consent);
        body.put("state", "feature");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(0, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iVolley.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iVolley.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iVolley.onError(context.getString(R.string.jsonError));
            }
        }, error -> iVolley.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iVolley.onError(context.getString(R.string.sampleLastMessage));
        }
    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.issue);
        return server + url[pos];
    }
}