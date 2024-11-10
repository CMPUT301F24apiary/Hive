package com.example.hive.Models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * create a user object. Singleton i.e. only one user object is active at a time
 */
public class User {
    private static User instance = null;
    private String deviceId;
    private String userName;
    private String email;
    private String phoneNumber = null;  // optional param
    private String role;
    private List<String> roleList;
    private String profileImageUrl;
    FirebaseFirestore db;

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

    private void generateInitialsDrawable() {
        if (userName != null && !userName.trim().isEmpty()) {
            String initials = userName.substring(0, 1).toUpperCase();
            initialsDrawable = TextDrawable
        }
    }
}
