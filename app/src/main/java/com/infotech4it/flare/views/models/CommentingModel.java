package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 28/10/2020.
 */
public class CommentingModel {
    private String id;
    private String userName;
    private String userReply;
    private String userImage;

    public CommentingModel(String userName, String userReply) {
        this.userName = userName;
        this.userReply = userReply;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserReply() {
        return userReply;
    }

    public void setUserReply(String userReply) {
        this.userReply = userReply;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
