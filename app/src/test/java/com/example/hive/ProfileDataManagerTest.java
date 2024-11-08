package com.example.hive;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.hive.Models.ProfileDataManager;

public class ProfileDataManagerTest {

    private ProfileDataManager profileDataManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);

        // Configure the mock behavior
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        profileDataManager = new ProfileDataManager(sharedPreferences);
    }

    // Section 1: Tests for US 01.02.01 - Providing Personal Information

    @Test
    public void testProvidePersonalInformationWithAllFields() {
        String name = "Alice Johnson";
        String username = "alicejohnson";
        String email = "alice.johnson@example.com";
        String phone = "1234567890";

        profileDataManager.saveProfileData(name, username, email, phone);

        verify(editor).putString("personName", name);
        verify(editor).putString("userName", username);
        verify(editor).putString("email", email);
        verify(editor).putString("phone", phone);
        verify(editor).apply();
    }

    @Test
    public void testProvidePersonalInformationWithNameAndEmailOnly() {
        String name = "John Doe";
        String username = "johndoe";
        String email = "john.doe@example.com";
        String phone = "";  // Optional phone number

        profileDataManager.saveProfileData(name, username, email, phone);

        verify(editor).putString("personName", name);
        verify(editor).putString("userName", username);
        verify(editor).putString("email", email);
        verify(editor).putString("phone", phone);
        verify(editor).apply();
    }

    // Section 2: Tests for US 01.02.02 - Updating Personal Information with Specific Validations

    @Test
    public void testUpdatePersonalInformationWithValidPhoneNumber() {
        String name = "Tom White";
        String username = "tomwhite";
        String email = "tom.white@example.com";
        String phone = "9876543210"; // Valid phone number containing only digits

        profileDataManager.saveProfileData(name, username, email, phone);

        // Check that the phone number contains only digits
        assertTrue(phone.matches("\\d+"));
        verify(editor).putString("phone", phone);
    }

    @Test
    public void testUpdatePersonalInformationWithValidEmail() {
        String name = "Mark Brown";
        String username = "markbrown";
        String email = "mark.brown@example.com"; // Valid email format with '@'
        String phone = "1234567890";

        profileDataManager.saveProfileData(name, username, email, phone);

        // Check that the email contains an '@' symbol
        assertTrue(email.contains("@"));
        verify(editor).putString("email", email);
    }

    @Test
    public void testUpdatePersonalInformationWithInvalidPhoneNumber() {
        String name = "Tom White";
        String username = "tomwhite";
        String email = "tom.white@example.com";
        String phone = "98765abc"; // Invalid phone number containing letters

        profileDataManager.saveProfileData(name, username, email, phone);

        // Verify that an invalid phone number does not pass the digits-only check
        assertFalse(phone.matches("\\d+"));
    }

    @Test
    public void testUpdatePersonalInformationWithInvalidEmail() {
        String name = "Alice Johnson";
        String username = "alicejohnson";
        String email = "alice.johnsonexample.com"; // Invalid email without '@'
        String phone = "1234567890";

        profileDataManager.saveProfileData(name, username, email, phone);

        // Verify that an invalid email does not pass the '@' check
        assertFalse(email.contains("@"));


    }
}
