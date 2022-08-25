package com.fearmygaze.mApp.model;

public class SearchedUser {
    private final int id;
    private final String image;
    private final String username;
    private final boolean friend;

    public SearchedUser(int id, String image, String username, boolean friend) {
        this.id = id;
        this.image = image;
        this.username = username;
        this.friend = friend;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }


    public boolean isFriend() {
        return friend;
    }
}