package com.example.hive;

import org.junit.Before;
import org.junit.Test;
//import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hive.Models.User;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserModelTests {
    private User user;

    @Before
    public void setUp() {
        ArrayList<String> roleList = new ArrayList<>();
        user = new User("testingUserSetUp",
                "Test Person",
                "usersetup@gmail.com",
                "1234567899",
                "entrant",
                roleList,
                null);
    }

    @Test
    public void testUserConstructor() {
        assertEquals("testingUserSetUp", user.getDeviceId());
        assertEquals("Test Person", user.getUserName());
        assertEquals("usersetup@gmail.com", user.getEmail());
        assertEquals("1234567899", user.getPhoneNumber());
        assertEquals("entrant", user.getRole());
        assertTrue(user.getRoleList().contains("entrant"));
    }

    @Test
    public void testDefaultUserConstructor() {
        User newUser = new User();
        assertEquals("", newUser.getDeviceId());
        assertEquals("", newUser.getUserName());
        assertEquals("", newUser.getEmail());
        assertNull(newUser.getPhoneNumber());
        assertEquals("entrant", newUser.getRole());
        assertTrue(newUser.getRoleList().contains("entrant"));
    }

    @Test
    public void testAddRole() {
        user.getRoleList().add("organizer");
        assertTrue(user.getRoleList().contains("organizer"));
    }
}