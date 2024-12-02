package com.example.hive.Models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * The Facility class represents a facility with its information such as name, email, phone, and picture URL.
 * It also provides functionality to generate a default profile picture (drawable) based on the facility's name.
 */
public class Facility {

    private String name;
    private String email;
    private String phone;
    private String pictureURL;
    private String ID;

    public Facility() {

    }

    /**
     * Constructor for creating a Facility object with name, email, phone, and optional picture URL.
     *
     * @param name      The name of the facility.
     * @param email     The email address associated with the facility.
     * @param phone     The phone number associated with the facility.
     * @param pictureURL The URL of the facility's picture (nullable).
     */

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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Generates a default profile picture for the facility based on its name's first letter in a
     * circular shape
     * The background color is chosen randomly from a predefined color generator.
     *
     * @return A Drawable representing the default profile picture for the facility.
     */
    public Drawable generateDefaultPic() {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int key = Math.abs((String.valueOf((int) Math.floor(Math.random()))).hashCode());

        int color1 = generator.getColor(key % 0xFFFFFF);
        int color3 = generator.getColor((key + 82) % 0xFFFFFF);

        String initials = this.name.substring(0, 1).toUpperCase();

        return new TextDrawable.Builder()
                .setColor(color1)
                .setBold()
                .setTextColor(color3)
                .setShape(TextDrawable.SHAPE_ROUND)
                .setText(initials)
                .setBorder(20)
                .setBorderColor(Color.BLACK)
                .build();
    }

}
