package com.fearmygaze.mApp.model;

public class miniUser {
    private final String id;
    private final String image;
    private final String name;
    private final String username;

    public miniUser(String id, String image, String name, String username) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
