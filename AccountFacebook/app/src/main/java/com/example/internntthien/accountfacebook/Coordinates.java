package com.example.internntthien.accountfacebook;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by intern.ntthien on 7/6/2017.
 */

public class Coordinates {
    private double longitude;
    private double latitude;
    private String userID;
    private String name;
    private String deviceToken;
    private String status;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUserID(){ return userID;}

    public String getName(){ return name;}

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getStatus(){return status;}

    public Coordinates(){

    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setStatus(String status){ this.status = status;}

    public Coordinates(String userID, double latitude, double longitude, String name, String deviceToken, String status) {
        this.userID = userID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.deviceToken = deviceToken;
        this.status = status;
    }


}
