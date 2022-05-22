package com.fearmygaze.mApp.model;

public class Conversation {
    private final String id;
    private final String image;
    private final String username;
    private final String lastMessage;
    private final String lastMessageTime;

    public Conversation(String id, String image, String username, String lastMessage, String lastMessageTime) {
        this.id = id;
        this.image = image;
        this.username = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
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
