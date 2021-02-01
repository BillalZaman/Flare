package com.infotech4it.flare.views.models;

import java.io.Serializable;

public class MessageModelClass implements Serializable {

    private int imgProfile;
    private String tvName;
    private String tvMsg;
    private String tvMsgTime;
    private String tvMsgCount;

    private String chatId;
    private String recentMessage;
    public String uId;
    private int unReadMessage;

    public MessageModelClass(MessageModelClass object) {
        this.imgProfile = object.imgProfile;
        this.tvName = object.tvName;
        this.tvMsg = object.tvMsg;
        this.tvMsgTime = object.tvMsgTime;
        this.tvMsgCount = object.tvMsgCount;
        this.chatId = object.chatId;
        this.recentMessage = object.recentMessage;
        this.uId = object.uId;
        this.unReadMessage = object.unReadMessage;
        this.profile = object.profile;
        this.sortingTime = object.sortingTime;
    }

    private String profile;
    private long sortingTime=0;


    public MessageModelClass(int imgProfile, String tvName, String tvMsg, String tvMsgTime, String tvMsgCount) {
        this.imgProfile = imgProfile;
        this.tvName = tvName;
        this.tvMsg = tvMsg;
        this.tvMsgTime = tvMsgTime;
        this.tvMsgCount = tvMsgCount;
    }

    public MessageModelClass() {
    }

    public int getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(int imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getTvMsg() {
        return tvMsg;
    }

    public void setTvMsg(String tvMsg) {
        this.tvMsg = tvMsg;
    }

    public String getTvMsgTime() {
        return tvMsgTime;
    }

    public void setTvMsgTime(String tvMsgTime) {
        this.tvMsgTime = tvMsgTime;
    }

    public String getTvMsgCount() {
        return tvMsgCount;
    }

    public void setTvMsgCount(String tvMsgCount) {
        this.tvMsgCount = tvMsgCount;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public int getUnReadMessage() {
        return unReadMessage;
    }

    public void setUnReadMessage(int unReadMessage) {
        this.unReadMessage = unReadMessage;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public long getSortingTime() {
        return sortingTime;
    }

    public void setSortingTime(long sortingTime) {
        this.sortingTime = sortingTime;
    }

    @Override
    public String toString() {
        return "MessageModelClass{" +
                ", tvMsgTime='" + tvMsgTime + '\'' +
                '}';
    }
}
