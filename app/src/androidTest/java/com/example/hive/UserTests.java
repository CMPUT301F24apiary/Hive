package com.example.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.graphics.drawable.Drawable;

import com.example.hive.Models.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTests {

    private User user;

    @Before
    public void setUp() {
        // Create a new User instance for each test
        user = new User();
    }

    @Test
    public void testDefaultValues() {
        // Check default values
        assertEquals("", user.getDeviceId());
        assertEquals("", user.getUserName());
        assertEquals("", user.getEmail());
        assertNull(user.getPhoneNumber());
        assertEquals("entrant", user.getRole());
        assertEquals(1, user.getRoleList().size());
        assertEquals("entrant", user.getRoleList().get(0));
        assertTrue(user.getNotificationChosen());
        assertTrue(user.getNotificationNotChosen());
        assertTrue(user.getNotificationOrganizer());
        assertEquals("", user.getProfileImageUrl());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setting and getting deviceId
        user.setDeviceId("12345");
        assertEquals("12345", user.getDeviceId());

        // Test setting and getting userName
        user.setUserName("John Doe");
        assertEquals("John Doe", user.getUserName());

        // Test setting and getting email
        user.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", user.getEmail());

        // Test setting and getting phoneNumber
        user.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getPhoneNumber());

        // Test setting and getting role
        user.setRole("admin");
        assertEquals("admin", user.getRole());

        // Test setting and getting roleList
        List<String> roles = Arrays.asList("entrant", "admin");
        user.setRoleList(roles);
        assertEquals(roles, user.getRoleList());

        // Test setting and getting profileImageUrl
        user.setProfileImageUrl("https://example.com/profile.jpg");
        assertEquals("https://example.com/profile.jpg", user.getProfileImageUrl());

        // Test setting and getting notification preferences
        user.setNotificationChosen(false);
        assertTrue(!user.getNotificationChosen());

        user.setNotificationNotChosen(false);
        assertTrue(!user.getNotificationNotChosen());

        user.setNotificationOrganizer(false);
        assertTrue(!user.getNotificationOrganizer());
    }

    @Test
    public void testGetInitials() {
        // Test initials generation
        user.setUserName("John Doe");
        assertEquals("JD", user.getInitials());

        user.setUserName("Jane");
        assertEquals("J", user.getInitials());

        user.setUserName("Jane Marie Doe");
        assertEquals("JD", user.getInitials());

        user.setUserName("");
        assertEquals("PN", user.getInitials()); // Default initials
    }

    @Test
    public void testGenerateInitialsDrawable() {
        // Test initials drawable generation
        user.setDeviceId("12345");
        user.setUserName("John Doe");
        Drawable drawable = user.getDisplayDrawable();

        assertNotNull(drawable);
    }

    @Test
    public void testRoleListDefaultValues() {
        // Test default role list
        List<String> roles = user.getRoleList();
        assertEquals(1, roles.size());
        assertEquals("entrant", roles.get(0));
    }

    @Test
    public void testAddRoleToRoleList() {
        // Test adding roles to the role list
        List<String> roles = new ArrayList<>(user.getRoleList());
        roles.add("admin");
        user.setRoleList(roles);

        assertEquals(2, user.getRoleList().size());
        assertTrue(user.getRoleList().contains("admin"));
    }

    @Test
    public void testEventIDs() {
        // Test setting and getting event IDs
        ArrayList<String> eventIDs = new ArrayList<>(Arrays.asList("event1", "event2"));
        user.setEventIDs(eventIDs);

        assertEquals(2, user.getEventIDs().size());
        assertEquals("event1", user.getEventIDs().get(0));
        assertEquals("event2", user.getEventIDs().get(1));
    }

    @Test
    public void testFacilityID() {
        // Test setting and getting facility ID
        user.setFacilityID("facility123");
        assertEquals("facility123", user.getFacilityID());
    }

    @Test
    public void testCustomProfileImage() {
        // Test that custom profile image returns null when set
        user.setProfileImageUrl("https://example.com/profile.jpg");
        Drawable drawable = user.getDisplayDrawable();

        assertNull(drawable);
    }

    @Test
    public void testInitialsDrawableWithNoProfileImage() {
        // Test that initials drawable is generated when no profile image is set
        user.setUserName("John Doe");
        user.setProfileImageUrl("");
        Drawable drawable = user.getDisplayDrawable();

        assertNotNull(drawable);
    }
}
