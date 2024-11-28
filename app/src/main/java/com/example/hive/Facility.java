package com.example.hive;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.w3c.dom.Text;

public class Facility {

    private String name;
    private String email;
    private String phone;
    private String pictureURL;
    private String ID;

    public Facility() {

    }

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
