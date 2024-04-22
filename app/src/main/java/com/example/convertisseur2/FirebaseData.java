package com.example.convertisseur2;

/**
 * this class will manage information stored in the firebase
 */
public class FirebaseData {
    private String currency;
    private String rate;

    /**
     * default constructor needed to work with the firebase
     */
    public FirebaseData() {

    }
    /**
     * Second constructeur
     * @param currency
     * @param rate
     */
    public FirebaseData(String currency, String rate) {
        this.currency = currency;
        this.rate = rate;
    }

    /**
     * Getter, will return the currency
     * @return : currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter, will set the value of the currency
     * @param currency : currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gettern will return the rate
     * @return : rate
     */
    public String getRate() {
        return rate;
    }



    /**
     * Setter, will set the value of the rate
     * @param rate : rate
     */
    public void setRate(String rate) {
        this.rate = rate;
    }
}
