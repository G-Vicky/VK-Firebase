package com.vkapps.vkfirebase.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String username;
    private String emailAddress;
    private String UID;
    private String phoneNumber;

    public User() {}

    public User(String username, String emailAddress, String UID) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.UID = UID;
    }

    public User(String username, String emailAddress, String UID, String phoneNumber) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.UID = UID;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
