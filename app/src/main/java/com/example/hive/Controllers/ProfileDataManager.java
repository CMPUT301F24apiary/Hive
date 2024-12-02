package com.example.hive.Controllers;

import android.content.SharedPreferences;

public class ProfileDataManager {

    private final SharedPreferences sharedPreferences;

    public ProfileDataManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveProfileData(String name, String username, String email, String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("personName", name);
        editor.putString("userName", username);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    public String getProfileData(String key) {
        return sharedPreferences.getString(key, "");
    }
}

