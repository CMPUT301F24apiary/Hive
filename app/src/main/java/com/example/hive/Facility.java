package com.example.hive;

import androidx.annotation.Nullable;

public class Facility {

    private String name;
    private String email;
    private String phone;
    private String pictureURL;

    public Facility(String name, String email, String phone, @Nullable String pictureURL) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pictureURL = pictureURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
