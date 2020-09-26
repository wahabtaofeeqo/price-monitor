package com.taocoder.pricemonitor.models;

import java.util.List;

public class PricesResult {

    private boolean error;
    private String message;
    private List<Price> prices;

    public PricesResult(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public PricesResult(boolean error, List<Price> prices) {
        this.error = error;
        this.prices = prices;
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

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }
}
