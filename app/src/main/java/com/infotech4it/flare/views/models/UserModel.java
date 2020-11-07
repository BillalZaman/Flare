package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 06/11/2020.
 */
public class UserModel {
    private String id;
    private String name;
    private String email;
    private String number;
    private String password;
    private String confirmPassword;
    private double latitude;
    private double longitude;

    public UserModel() {
    }

    public UserModel(String name, String email, String number, String password, double _latitude, double _longitude) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.password = password;
        this.latitude = _latitude;
        this.longitude = _longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
