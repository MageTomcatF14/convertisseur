package com.example.convertisseur2;

public class InfoRetrieve {
    private String currency;
    private String rate;


    public InfoRetrieve() {
    }

    public InfoRetrieve(String currency, String rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
