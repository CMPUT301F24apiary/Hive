package com.example.hive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.hive.Models.User;

import org.junit.Before;
import org.junit.Test;

public class DeterministicProfilePicTests {
    private User user;

    @Before
    public void setUser() {
        user = new User();
        user.setDeviceId("23dsfa5fd45fd46s5e651");
    }

    @Test
    public void test1WordUserName() {
        user.setUserName("Rapunzel");
        assertEquals("R", user.getInitials());
    }

    @Test
    public void testMultiWordUserName() {
        user.setUserName("Rapunzel Herz der Sonne");
        assertEquals("RS", user.getInitials());
    }

    @Test
    public void testEmptyUserName() {
        user.setUserName("");
        assertEquals("PN", user.getInitials());
    }
}
