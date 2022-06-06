package com.fearmygaze.mApp.model;

public class Friend {
    private final String id;
    private final String image;
    private final String username;

    public Friend(String id, String image, String username) {
        this.id = id;
        this.image = image;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }
}
