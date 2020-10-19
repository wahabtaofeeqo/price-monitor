package com.taocoder.pricemonitor.models;

import java.util.List;

public class PricesResult {

    private boolean error;
    private String message;
    private List<CompetitorPriceAndAddress> competitorPriceAndAddresses;

    public PricesResult(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public PricesResult(boolean error, List<CompetitorPriceAndAddress> competitorPriceAndAddresses) {
        this.error = error;
        this.competitorPriceAndAddresses = competitorPriceAndAddresses;
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

    public List<CompetitorPriceAndAddress> getCompetitorPriceAndAddresses() {
        return competitorPriceAndAddresses;
    }

    public void setCompetitorPriceAndAddresses(List<CompetitorPriceAndAddress> competitorPriceAndAddresses) {
        this.competitorPriceAndAddresses = competitorPriceAndAddresses;
    }
}
