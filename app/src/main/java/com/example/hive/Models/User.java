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
 * Represents a user in the Hive application.
 * This class includes user details such as device ID, username, email, phone number, role, and profile image.
 * The user can have multiple roles and notifications settings.
 *
 * @Author Aleena.
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
    private boolean notificationChosen = true;       // Default to true
    private boolean notificationNotChosen = true;    // Default to true
    private boolean notificationOrganizer = true;    // Default to true
    FirebaseFirestore db;
    private Drawable initialsDrawable;
    private String facilityID;
    @PropertyName("events")
    private ArrayList<String> eventIDs;

    /**
     * Returns the singleton instance of the User class.
     *
     * @return The singleton User instance.
     */
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    /**
     * Constructor for creating a user with known details.
     *
     * @param deviceId The device ID.
     * @param userName The name of the user.
     * @param email The email address of the user.
     * @param phoneNumber The phone number of the user (optional).
     * @param role The current role of the user.
     * @param roleList All roles the user can have.
     * @param db The Firebase Firestore database instance.
     * @param profileImageUrl The URL of the user's profile image.
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

    /**
     * Default constructor for creating a user with default details.
     */
    public User() {
        this.deviceId = "";
        this.userName = "";
        this.email = "";
        this.phoneNumber = null;
        this.role = "entrant";
        this.roleList = new ArrayList<>();
        roleList.add("entrant"); // default role
        this.profileImageUrl = ""; // default profile image
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

    /**
     * Returns whether notifications for being chosen are enabled.
     *
     * @return true if notifications are enabled, false otherwise.
     */
    public boolean getNotificationChosen() {
        return notificationChosen;
    }

    /**
     * Sets whether notifications for being chosen are enabled.
     *
     * @param notificationChosen true to enable notifications, false to disable.
     */
    public void setNotificationChosen(boolean notificationChosen) {
        this.notificationChosen = notificationChosen;
    }

    /**
     * Returns whether notifications for not being chosen are enabled.
     *
     * @return true if notifications are enabled, false otherwise.
     */
    public boolean getNotificationNotChosen() {
        return notificationNotChosen;
    }

    /**
     * Sets whether notifications for not being chosen are enabled.
     *
     * @param notificationNotChosen true to enable notifications, false to disable.
     */
    public void setNotificationNotChosen(boolean notificationNotChosen) {
        this.notificationNotChosen = notificationNotChosen;
    }

    /**
     * Returns whether organizer notifications are enabled.
     *
     * @return true if notifications are enabled, false otherwise.
     */
    public boolean getNotificationOrganizer() {
        return notificationOrganizer;
    }

    /**
     * Sets whether organizer notifications are enabled.
     *
     * @param notificationOrganizer true to enable notifications, false to disable.
     */
    public void setNotificationOrganizer(boolean notificationOrganizer) {
        this.notificationOrganizer = notificationOrganizer;
    }

    @PropertyName("roleSet")
    public List<String> getRoleList() {
        return roleList;
    }

    @PropertyName("roleSet")
    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        generateInitialsDrawable();
    }

    @PropertyName("events")
    public ArrayList<String> getEventIDs() {
        return eventIDs;
    }

    @PropertyName("events")
    public void setEventIDs(ArrayList<String> eventIDs) {
        this.eventIDs = eventIDs;
    }

    /**
     * Generates the initials for the user based on their username.
     *
     * @return The initials of the user.
     */
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
     * Generates an initials profile picture if no profile picture is uploaded.
     *
     * @return A Drawable representing the initials.
     */
    private Drawable generateInitialsDrawable() {
        // Log.d(TAG, "Generating initials drawable for user: " + userName);

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
     * Gets either the profile image URL or the initials drawable.
     *
     * @return The Drawable representing the user's display picture, or null if a profile image URL is available.
     */
    public Drawable getDisplayDrawable() {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            return null;
        } else {
            return generateInitialsDrawable();
        }
    }
}
