package com.example.hive;

public class UserProfile {
    private String name;
    private String username;
    private String deviceId;
    private String profilePicture;
    private String email;
    private String phone;

    // for the Firebase
    public UserProfile() {

    }

    public UserProfile(String name, String username, String deviceId, String profilePicture, String email, String phone) {
        this.name = name;
        this.username = username;
        this.deviceId = deviceId;
        this.profilePicture = profilePicture;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
