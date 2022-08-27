package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IUser;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.util.PrivatePreference;
import com.fearmygaze.mApp.util.RequestSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * TODO: I don't know if we need the user Model if we write all the stuff on sharedPreferences maybe
 *           after writing it in sharedPreferences then we pass them to the Model ? so we can simply call user.get..();
 *           like the FirebaseUser(???)
 * TODO: Maybe add the error codes as an enum ?? so the server has to send only the number
 * */

public class UserController {

    public static void signUp(String username, String email, String password, String image, Context context, IVolley iVolley) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        body.put("imagePath", image);

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
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void signIn(String credential, String password, Context context, IUser iUser) {
        Map<String, String> body = new HashMap<>();
        body.put("loginCredential", credential);
        body.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(1, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        int id = response.getJSONObject("data").getInt("id");
                        String username = response.getJSONObject("data").getString("username");
                        String email = response.getJSONObject("data").getString("email");
                        String image = response.getJSONObject("data").getString("imagePath");

                        User user = new User(id, username,  BuildConfig.PROFILE + image, email);

                        PrivatePreference preference = new PrivatePreference(context);

                        preference.putInt("id", id);
                        preference.putString("username", username);
                        preference.putString("email", email);
                        preference.putString("image", BuildConfig.PROFILE + image);

                        iUser.onSuccess(user, message);
                        break;
                    case "404":
                    case "405":
                        iUser.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iUser.onError(e.getMessage());
            }
        }, error -> iUser.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void updatePassword(int id, String newPassword, String oldPassword, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);
        body.put("newPassword", newPassword);
        body.put("oldPassword", oldPassword);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(2, context), new JSONObject(body), response -> {
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
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    /*
     * TODO: We cant let the user to change the image all the time but only once a month
     * */
    public static void updateImage(String email, String image, Context context, IVolley iVolley) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(3, context), new JSONObject(body), response -> {
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
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void statusCheck(int id, Context context, IVolley iVolley) { //TODO: This will be for checking if the user is banned, OK or disabled
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(4, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        //iVolley.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iVolley.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void delete(int id, Context context, IVolley iVolley) {//TODO: This will be inside the settings activity
        Map<String, Object> body = new HashMap<>();
        body.put("userID", 20);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(5, context), new JSONObject(body), response -> {
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
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void report(int id, String reportedUserID, String reason, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);
        body.put("reportedUserID", reportedUserID);
        body.put("reason", reason);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(6, context), new JSONObject(body), response -> {
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
                iVolley.onError(e.getMessage());
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.user);
        return server + url[pos];
    }

}