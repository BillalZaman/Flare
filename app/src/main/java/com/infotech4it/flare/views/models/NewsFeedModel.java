package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 16/10/2020.
 */
public class NewsFeedModel {
    private String userName;
    private String userImage;
    private String userNewsTime;
    private String userStatus;
    private String userNewsStatusImage;

    public NewsFeedModel(String userName, String userNewsTime, String userStatus) {
        this.userName = userName;
        this.userNewsTime = userNewsTime;
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserNewsTime() {
        return userNewsTime;
    }

    public void setUserNewsTime(String userNewsTime) {
        this.userNewsTime = userNewsTime;
    }

    public String getUserNewsStatusImage() {
        return userNewsStatusImage;
    }

    public void setUserNewsStatusImage(String userNewsStatusImage) {
        this.userNewsStatusImage = userNewsStatusImage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
