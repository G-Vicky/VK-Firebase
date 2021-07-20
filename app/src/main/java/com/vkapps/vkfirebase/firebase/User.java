package com.vkapps.vkfirebase.firebase;

public class User {
    String username;
    String emailAddress;
    String UID;

    public User() {}

    public User(String username, String emailAddress, String UID) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.UID = UID;
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
