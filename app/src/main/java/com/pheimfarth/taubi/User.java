package com.pheimfarth.taubi;

public class User {

    private double latitude;
    private double longitude;

    public User(  double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public User() {

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
