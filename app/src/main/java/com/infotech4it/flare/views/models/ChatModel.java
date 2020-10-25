package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 23/10/2020.
 */
public class ChatModel {
    private String userImage;
    private String userName;
    private String userMessage;
    private String userLastMessage;
    private String userCurrentLocation;

    public ChatModel(String userName, String userMessage, String userLastMessage, String userCurrentLocation) {
        this.userName = userName;
        this.userMessage = userMessage;
        this.userLastMessage = userLastMessage;
        this.userCurrentLocation = userCurrentLocation;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserLastMessage() {
        return userLastMessage;
    }

    public void setUserLastMessage(String userLastMessage) {
        this.userLastMessage = userLastMessage;
    }

    public String getUserCurrentLocation() {
        return userCurrentLocation;
    }

    public void setUserCurrentLocation(String userCurrentLocation) {
        this.userCurrentLocation = userCurrentLocation;
    }
}
