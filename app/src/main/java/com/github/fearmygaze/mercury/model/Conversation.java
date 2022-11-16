package com.github.fearmygaze.mercury.model;

public class Conversation {
    private final int id;
    private final String image;
    private final String username;
    private final String lastMessage;
    private final String lastMessageTime;

    public Conversation(int id, String image, String username, String lastMessage, String lastMessageTime) {
        this.id = id;
        this.image = image;
        this.username = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime='" + lastMessageTime + '\'' +
                '}';
    }
}