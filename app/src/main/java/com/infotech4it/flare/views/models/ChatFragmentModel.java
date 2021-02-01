package com.infotech4it.flare.views.models;

public class ChatFragmentModel {

    MessageModelClass MessageModelClass;
    public String email;
    public String firebaseID;
    public String name;
    public String number;
    public String password;
    public String profile;

    public ChatFragmentModel() {
    }

    public ChatFragmentModel(com.infotech4it.flare.views.models.MessageModelClass messageModelClass, String email, String firebaseID, String name, String number, String password, String profile) {
        MessageModelClass = messageModelClass;
        this.email = email;
        this.firebaseID = firebaseID;
        this.name = name;
        this.number = number;
        this.password = password;
        this.profile = profile;
    }

    public com.infotech4it.flare.views.models.MessageModelClass getMessageModelClass() {
        return MessageModelClass;
    }

    public void setMessageModelClass(com.infotech4it.flare.views.models.MessageModelClass messageModelClass) {
        MessageModelClass = messageModelClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
