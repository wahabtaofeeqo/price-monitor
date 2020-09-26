package com.taocoder.pricemonitor.models;

import com.google.android.gms.maps.model.LatLng;

public class StationAddress {

    private double latitude;
    private double longitude;
    String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
