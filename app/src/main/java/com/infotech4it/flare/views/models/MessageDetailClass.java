package com.infotech4it.flare.views.models;

public class MessageDetailClass {

    private String message="";

    private String image_path ="";
    private String video_path ="";
    private String audio_path ="";
    private String file_path ="";

    private String place_name ="";
    private String latitude="";
    private String longtitude="";

    private String file_name ="";

    private String time="";
    private int view_type =0;
    private int message_type =0;

    private String senderName="";


    public MessageDetailClass() {
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }

    public MessageDetailClass(String message, String imagePath, String videoPath, String audioPath, String filePath, String placeName, String latitude, String longtitude, String fileName, String time, int viewType, int messageType, String senderName) {
        this.message = message;
        this.image_path = imagePath;
        this.video_path = videoPath;
        this.audio_path = audioPath;
        this.file_path = filePath;
        this.place_name = placeName;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.file_name = fileName;
        this.time = time;
        this.view_type = viewType;
        this.message_type = messageType;
        this.senderName = senderName;
    }

    public MessageDetailClass(int viewType, int messageType, String message, String time,
                              String imagePath, String videoPath, String latitude,
                              String longtitude, String placeName, String filePath, String fileName) {
        this.view_type = viewType;
        this.message = message;
        this.message_type = messageType;
        this.time = time;
        this.image_path = imagePath;
        this.video_path = videoPath;
        this.latitude= latitude;
        this.longtitude= longtitude;
        this.place_name = placeName;
        this.file_path = filePath;
        this.file_name = fileName;

    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }


    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getView_type() {
        return view_type;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    @Override
    public String toString() {
        return "MessageDetailClass{" +
                "message='" + message + '\'' +
                ", image_path='" + image_path + '\'' +
                ", video_path='" + video_path + '\'' +
                ", audio_path='" + audio_path + '\'' +
                ", file_path='" + file_path + '\'' +
                ", place_name='" + place_name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longtitude='" + longtitude + '\'' +
                ", file_name='" + file_name + '\'' +
                ", time='" + time + '\'' +
                ", view_type=" + view_type +
                ", message_type=" + message_type +
                ", senderName='" + senderName + '\'' +
                '}';
    }


}
