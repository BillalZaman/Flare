package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 11/01/2021.
 */
public class PostModel {
    private String image;
    private String usertext;

    public PostModel(String image, String usertext) {
        this.image = image;
        this.usertext = usertext;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsertext() {
        return usertext;
    }

    public void setUsertext(String usertext) {
        this.usertext = usertext;
    }
}
