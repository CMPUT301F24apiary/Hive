package com.example.hive.Controllers;

import android.content.SharedPreferences;

public class FacilityDataManager {

    private final SharedPreferences sharedPreferences;

    public FacilityDataManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveFacilityData(String name, String email, String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("facilityName", name);
        editor.putString("facilityEmail", email);
        editor.putString("facilityPhone", phone);
        editor.apply();
    }

    public String getFacilityData(String key) {
        return sharedPreferences.getString(key, "");
    }}
