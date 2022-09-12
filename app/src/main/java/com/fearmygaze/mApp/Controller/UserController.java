package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IUser;
import com.fearmygaze.mApp.interfaces.IUserStatus;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.util.NetworkConnection;
import com.fearmygaze.mApp.util.PrivatePreference;
import com.fearmygaze.mApp.util.RequestSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 *  TODO: Maybe add the device language to send back the correct error messages in the appropriate
 *   language
 */
public class UserController {

    public static void signUp(String username, String email, String password, String image, Context context, IVolley iVolley) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        body.put("image", image);

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
            iVolley.onError(context.getString(R.string.networkError));
        }
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
                        String image = response.getJSONObject("data").getString("image");

                        User user = new User(id, username, BuildConfig.PROFILE + image, email);

                        PrivatePreference preference = new PrivatePreference(context);

                        preference.putInt("id", id);
                        preference.putString("username", username);
                        preference.putString("image", BuildConfig.PROFILE + image);
                        preference.putString("email", email);

                        iUser.onSuccess(user, message);
                        break;
                    case "404":
                    case "405":
                        iUser.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iUser.onError(context.getString(R.string.jsonError));
            }
        }, error -> iUser.onError(context.getString(R.string.volleyError))) {

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
            iUser.onError(context.getString(R.string.networkError));
        }
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
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    public static void updateImage(int id, String image, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("image", image);

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
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    public static void statusCheck(int id, Context context, IUserStatus iUserStatus) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(4, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        int _id = response.getJSONObject("data").getInt("id");
                        String username = response.getJSONObject("data").getString("username");
                        String image = response.getJSONObject("data").getString("image");
                        String email = response.getJSONObject("data").getString("email");

                        PrivatePreference preference = new PrivatePreference(context);
                        preference.putInt("id", id);
                        preference.putString("username", username);
                        preference.putString("image", BuildConfig.PROFILE + image);
                        preference.putString("email", email);

                        User user = new User(_id, username, BuildConfig.PROFILE + image, email);

                        iUserStatus.onSuccess(user);
                        break;
                    case "404":
                    case "405":
                        iUserStatus.onExit(message);
                        break;
                }

            } catch (JSONException e) {
                iUserStatus.onError(context.getString(R.string.jsonError));
            }
        }, error -> iUserStatus.onError(context.getString(R.string.volleyError))) {

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
            iUserStatus.onError(context.getString(R.string.networkError));
        }
    }

    public static void delete(int id, Context context, IVolley iVolley) {//TODO: This will be inside the settings activity
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);

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
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    /*
    * TODO: This will change with more options and stuff
    * */

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
                iVolley.onError(context.getString(R.string.jsonError));
            }
        }, error -> iVolley.onError(error.getMessage())) {

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
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.user);
        return server + url[pos];
    }

}