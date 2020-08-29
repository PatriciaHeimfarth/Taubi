package com.pheimfarth.taubi;

import android.location.Location;

public class Taube {

    private String latitude;
    private String longitude;

    public Taube(  String latitude, String longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String distanceBetweenTaubenAddressAndCurrentLocation(double [] usersAddress) {
        Location taubeLocation = new Location("");
        Location usersLocation = new Location("");

        taubeLocation.setLatitude(Double.valueOf(latitude));
        taubeLocation.setLongitude(Double.valueOf(longitude));

        usersLocation.setLatitude(usersAddress[0]);
        usersLocation.setLongitude(usersAddress[1]);

        float distance = taubeLocation.distanceTo(usersLocation);
        return String.valueOf(distance);
    }
}
