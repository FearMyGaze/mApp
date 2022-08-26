package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IFriend;
import com.fearmygaze.mApp.interfaces.ISearch;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.model.Friend;
import com.fearmygaze.mApp.model.SearchedUser;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.util.RequestSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendController {

    public static void searchUser(User user, String username, int offset, Context context, ISearch iSearch) {//TODO: We need to check if the user is already friends with the user
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("limit", 10);
        body.put("offset", offset);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(0, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String code = response.getString("code");

                switch (code) {
                    case "200":
                        List<SearchedUser> searchedUserList = new ArrayList<>();
                        JSONArray array = response.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {

                            int id = array.getJSONObject(i).getInt("id");
                            String imagePath = array.getJSONObject(i).getString("imagePath");
                            String name = array.getJSONObject(i).getString("username");
                            String friendshipState = array.getJSONObject(i).getString("friendshipState");
                            String user1_id = array.getJSONObject(i).getString("user1_id");
                            String user2_id = array.getJSONObject(i).getString("user2_id");

                            if (!user.getUsername().equals(name)){//This is removing our name from the list
                                if ((user1_id.equals(String.valueOf(user.getId())) || user2_id.equals(String.valueOf(user.getId()))) && friendshipState.equals("true")){
                                    searchedUserList.add(new SearchedUser(id, imagePath, name, true));
                                }else{
                                    searchedUserList.add(new SearchedUser(id, imagePath, name, false));
                                }
                            }

                        }
                        iSearch.onSuccess(searchedUserList);
                        break;
                    case "404":
                    case "405":
                        iSearch.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iSearch.onError(e.getMessage());
            }
        }, error -> iSearch.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void sendFriendRequest(int userID, int friendID, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", userID);
        body.put("friendID",friendID);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(1, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String code = response.getString("code");

                switch (code) {
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

    public static void showFriends(int userID, int offset, Context context, IFriend iFriend) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", userID);
        body.put("limit", 10);
        body.put("offset", offset);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(4, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String code = response.getString("code");

                switch (code) {
                    case "200":
                        List<Friend> friendList = new ArrayList<>();
                        JSONArray array = response.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            int id = array.getJSONObject(i).getInt("id");
                            String imagePath = array.getJSONObject(i).getString("imagePath");
                            String name = array.getJSONObject(i).getString("username");

                            friendList.add(new Friend(id, imagePath, name));
                        }
                        iFriend.onSuccess(friendList);
                        break;
                    case "404":
                    case "405":
                        iFriend.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iFriend.onError(e.getMessage());
            }
        }, error -> iFriend.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void acceptOrDenyFriendRequest(int userID, String friendID, boolean choice, Context context, IVolley iVolley) {


    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.friends);
        return server + url[pos];
    }
}