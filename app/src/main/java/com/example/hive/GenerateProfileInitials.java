package com.example.hive;


import android.graphics.Bitmap;
import android.util.DisplayMetrics;

/*
This can go in the Profile class. or not.
Generates the profile image if user does not have an image uploaded.
 */
public class GenerateProfileInitials {

    public GenerateProfileInitials() {
    }

    public String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "PN";
        }

        String[] nameSliced = name.trim().split("\\s+");  // split name based on whitespace
        StringBuilder initials = new StringBuilder();
        if (nameSliced.length > 0) {
            initials.append(nameSliced[0].charAt(0));
        }
        if (nameSliced.length > 1) {
            initials.append(nameSliced[nameSliced.length - 1].charAt(0));
        }
        return initials.toString().toUpperCase();
    }
}
