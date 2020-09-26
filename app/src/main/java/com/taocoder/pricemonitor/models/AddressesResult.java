package com.taocoder.pricemonitor.models;

import java.util.List;

public class AddressesResult {

    private boolean error;
    private String message;
    private List<StationAddress> addresses;

    public AddressesResult(boolean status) {
        this.error = status;
    }

    public AddressesResult(boolean status, String message) {
        this.error = status;
        this.message = message;
    }

    public AddressesResult(boolean status, List<StationAddress> addresses) {
        this.error = status;
        this.addresses = addresses;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StationAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<StationAddress> addresses) {
        this.addresses = addresses;
    }
}
