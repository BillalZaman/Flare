package com.infotech4it.flare.views.models;

public class FirebaseUserTableModal {

    public int id=0;
    public String name="";
    public String profile="";
//    private String recent_message="";
//    private int un_react_count=0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

//    public String getRecent_message() {
//        return recent_message;
//    }
//
//    public void setRecent_message(String recent_message) {
//        this.recent_message = recent_message;
//    }
//
//    public int getUn_react_count() {
//        return un_react_count;
//    }
//
//    public void setUn_react_count(int un_react_count) {
//        this.un_react_count = un_react_count;
//    }

    @Override
    public String toString() {
        return "FirebaseUserTableModal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }

}
