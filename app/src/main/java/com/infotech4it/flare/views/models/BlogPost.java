package com.infotech4it.flare.views.models;
import com.google.firebase.Timestamp;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String image_url, image_thumb, desc,user_id, user_name;
    public Timestamp timeStamp;

    public BlogPost(){

    }

    public BlogPost(String image_url, String image_thumb, String desc, String user_id, Timestamp timeStamp, String user_name1) {
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.desc = desc;
        this.user_id = user_id;
       this.timeStamp = timeStamp;
       this.user_name = user_name1;
    }

    public String getImage_url() {

        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
