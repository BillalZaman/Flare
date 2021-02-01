package com.infotech4it.flare.views.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class SelectStudentForChat implements Parcelable {

    Chat chats;
    public String email;
    public String firebaseID;
    public String name;
    public String number;
    public String password;
    public String profile;

    public SelectStudentForChat() {
    }

    public SelectStudentForChat(Chat chats, String email, String firebaseID, String name, String number, String password, String profile) {
        this.chats = chats;
        this.email = email;
        this.firebaseID = firebaseID;
        this.name = name;
        this.number = number;
        this.password = password;
        this.profile = profile;
    }

    protected SelectStudentForChat(Parcel in) {
        chats = in.readParcelable(Chat.class.getClassLoader());
        email = in.readString();
        firebaseID = in.readString();
        name = in.readString();
        number = in.readString();
        password = in.readString();
        profile = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(chats, flags);
        dest.writeString(email);
        dest.writeString(firebaseID);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(password);
        dest.writeString(profile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectStudentForChat> CREATOR = new Creator<SelectStudentForChat>() {
        @Override
        public SelectStudentForChat createFromParcel(Parcel in) {
            return new SelectStudentForChat(in);
        }

        @Override
        public SelectStudentForChat[] newArray(int size) {
            return new SelectStudentForChat[size];
        }
    };

    public Chat getChats() {
        return chats;
    }

    public void setChats(Chat chats) {
        this.chats = chats;
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

    @Override
    public String toString() {
        return "SelectStudentForChat{" +
                "chats=" + chats +
                ", email='" + email + '\'' +
                ", firebaseID='" + firebaseID + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", password='" + password + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
