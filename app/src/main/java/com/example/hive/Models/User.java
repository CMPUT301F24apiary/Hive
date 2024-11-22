package com.example.hive.Models;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * create a user object. Singleton i.e. only one user object is active at a time
 */
public class User {
    private static User instance = null;
    private String deviceId;
    @PropertyName("username")
    private String userName;
    private String email;
    private String phoneNumber = null;  // optional param
    private String role;
    @PropertyName("roleSet")
    private List<String> roleList;
    private String profileImageUrl;
    FirebaseFirestore db;
    private Drawable initialsDrawable;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    /**
     * constructor for user with known details
     * @param deviceId device id
     * @param userName name of user
     * @param email email
     * @param phoneNumber phone number
     * @param role current role of user
     * @param roleList all roles user can have
     * @param db database instance
     * @param profileImageUrl uses Glide
     */
    public User(String deviceId, String userName, String email, String phoneNumber,
                String role, List<String> roleList, FirebaseFirestore db, String profileImageUrl) {
        this.deviceId = deviceId;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.roleList = roleList != null ? new ArrayList<>(roleList) : new ArrayList<>();
        this.roleList.add("entrant");  // default role
        this.profileImageUrl = profileImageUrl;

    }

    public User() {
        this.deviceId = "";
        this.userName = "";
        this.email = "";
        this.phoneNumber = null;
        this.role = "entrant";
        this.roleList = new ArrayList<>();
        roleList.add("entrant");// default role
        this.profileImageUrl = ""; // this is the default profile image
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        generateInitialsDrawable();
    }

    public String getInitials() {
        String initials = "PN";
        if (userName != null && !userName.trim().isEmpty()) {
            String[] words = userName.trim().split("\\s+");
            String initial1 = words[0].substring(0, 1).toUpperCase();
            String initial2 = words.length > 1 ? words[words.length - 1].substring(0, 1).toUpperCase() : "";
            initials = initial1 + initial2;
        }
        return initials;
    }

    /**
     * generate initials profile pic if no profile pic is uploaded
     */
    private Drawable generateInitialsDrawable() {
        //Log.d(TAG, "Generating initials drawable for user: " + userName);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int key = Math.abs(deviceId.hashCode());

        int color1 = generator.getColor(key % 0xFFFFFF);
        int color3 = generator.getColor((key + 82) % 0xFFFFFF);

        int borderColor = generator.getColor(0x000000);

        String initials = getInitials();

            initialsDrawable = new TextDrawable.Builder()
                    .setColor(color1)
                    .setBold()
                    .setTextColor(color3)
                    .setShape(TextDrawable.SHAPE_ROUND)
                    .setText(initials)
                    .setBorder(20)
                    .setBorderColor(Color.BLACK)
                    .build();

        return initialsDrawable;
    }

    /**
     * get either the profile image url or the initials drawable
     * @return deterministic profile pic only if one has not been generated before
     */
    public Drawable getDisplayDrawable() {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            return null;
        } else {
            return generateInitialsDrawable();
        }
    }
}
