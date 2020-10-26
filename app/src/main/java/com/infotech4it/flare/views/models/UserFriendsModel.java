package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 26/10/2020.
 */
public class UserFriendsModel {
    private String userFriendImage;
    private String userFriendName;
    private String userFriendLocation;

    public UserFriendsModel(String userFriendName, String userFriendLocation) {
        this.userFriendName = userFriendName;
        this.userFriendLocation = userFriendLocation;
    }

    public String getUserFriendImage() {
        return userFriendImage;
    }

    public void setUserFriendImage(String userFriendImage) {
        this.userFriendImage = userFriendImage;
    }

    public String getUserFriendName() {
        return userFriendName;
    }

    public void setUserFriendName(String userFriendName) {
        this.userFriendName = userFriendName;
    }

    public String getUserFriendLocation() {
        return userFriendLocation;
    }

    public void setUserFriendLocation(String userFriendLocation) {
        this.userFriendLocation = userFriendLocation;
    }
}
