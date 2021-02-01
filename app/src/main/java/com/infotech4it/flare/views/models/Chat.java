package com.infotech4it.flare.views.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {

    String chatid;
    String fid;
    String recentmsg;
    String uid;
    int time;
    int unreadmsg;

    public Chat() {
    }

    protected Chat(Parcel in) {
        chatid = in.readString();
        fid = in.readString();
        recentmsg = in.readString();
        uid = in.readString();
        time = in.readInt();
        unreadmsg = in.readInt();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getRecentmsg() {
        return recentmsg;
    }

    public void setRecentmsg(String recentmsg) {
        this.recentmsg = recentmsg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getUnreadmsg() {
        return unreadmsg;
    }

    public void setUnreadmsg(int unreadmsg) {
        this.unreadmsg = unreadmsg;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chatid);
        parcel.writeString(fid);
        parcel.writeString(recentmsg);
        parcel.writeString(uid);
        parcel.writeInt(time);
        parcel.writeInt(unreadmsg);
    }
}
