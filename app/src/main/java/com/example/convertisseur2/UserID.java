package com.example.convertisseur2;


/**
 * Manages information of user for log and sin in firebase
 * Represented by the mail and password
 */
public class UserID {

    public String mailOfUser;
    public String passwordOfUser;

    /**
     * default constructor
     */
    public UserID() {
    }


    /**
     * second constructor
     * @param mailOfUser : mail
     * @param passwordOfUser : password
     */
    public UserID(String mailOfUser, String passwordOfUser) {
        this.mailOfUser = mailOfUser;
        this.passwordOfUser = passwordOfUser;
    }
}
