package com.github.fearmygaze.mercury.model;

public class Friend {
    private final int id;
    private final String image;
    private final String username;

    public Friend(int id, String image, String username) {
        this.id = id;
        this.image = image;
        this.username = username;
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
}