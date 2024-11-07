package com.example.hive;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.content.SharedPreferences;
import android.widget.EditText;

import com.example.hive.ProfileEditActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfileEditActivityTest {

    private ProfileEditActivity profileEditActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText personNameInput;
    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneInput;

    @BeforeEach
    public void setUp() {
        profileEditActivity = new ProfileEditActivity();

        // Mock SharedPreferences and its editor
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        // Inject mocks into the activity
        profileEditActivity.setSharedPreferencesForTesting(sharedPreferences);

        // Mock EditText fields
        personNameInput = mock(EditText.class);
        userNameInput = mock(EditText.class);
        emailInput = mock(EditText.class);
        phoneInput = mock(EditText.class);

        // Assign mocked EditTexts to profileEditActivity fields
        profileEditActivity.personNameInput = personNameInput;
        profileEditActivity.userNameInput = userNameInput;
        profileEditActivity.emailInput = emailInput;
        profileEditActivity.phoneInput = phoneInput;
    }

    // Test cases for US 01.02.01 - Providing Personal Information for the First Time

    @Test
    public void testFirstTimeUploadWithNameAndEmailOnly() {
        when(personNameInput.getText().toString()).thenReturn("John Doe");
        when(userNameInput.getText().toString()).thenReturn("johndoe");
        when(emailInput.getText().toString()).thenReturn("john.doe@example.com");
        when(phoneInput.getText().toString()).thenReturn(""); // Optional phone number is not provided

        profileEditActivity.saveProfileData();

        verify(editor).putString("personName", "John Doe");
        verify(editor).putString("userName", "johndoe");
        verify(editor).putString("email", "john.doe@example.com");
        verify(editor).putString("phone", "");
        verify(editor).apply();
    }

    @Test
    public void testFirstTimeUploadWithAllInformation() {
        when(personNameInput.getText().toString()).thenReturn("Alice Johnson");
        when(userNameInput.getText().toString()).thenReturn("alicejohnson");
        when(emailInput.getText().toString()).thenReturn("alice.johnson@example.com");
        when(phoneInput.getText().toString()).thenReturn("1234567890");

        profileEditActivity.saveProfileData();

        verify(editor).putString("personName", "Alice Johnson");
        verify(editor).putString("userName", "alicejohnson");
        verify(editor).putString("email", "alice.johnson@example.com");
        verify(editor).putString("phone", "1234567890");
        verify(editor).apply();
    }

    @Test
    public void testFirstTimeUploadWithInvalidEmail() {
        when(personNameInput.getText().toString()).thenReturn("Mark Brown");
        when(userNameInput.getText().toString()).thenReturn("markbrown");
        when(emailInput.getText().toString()).thenReturn("invalid-email"); // Invalid email format
        when(phoneInput.getText().toString()).thenReturn("1234567890");

        profileEditActivity.saveProfileData();

        verify(editor).putString("email", "invalid-email");
    }

    @Test
    public void testFirstTimeUploadWithPhoneNumberDigitsOnly() {
        when(personNameInput.getText().toString()).thenReturn("Tom White");
        when(userNameInput.getText().toString()).thenReturn("tomwhite");
        when(emailInput.getText().toString()).thenReturn("tom.white@example.com");
        when(phoneInput.getText().toString()).thenReturn("9876543210"); // Valid phone number

        profileEditActivity.saveProfileData();

        verify(editor).putString("phone", "9876543210");
    }

    // Test cases for US 01.02.02 - Updating Personal Information

    @Test
    public void testUpdateName() {
        when(personNameInput.getText().toString()).thenReturn("Updated Name");

        profileEditActivity.saveProfileData();

        verify(editor).putString("personName", "Updated Name");
    }

    @Test
    public void testUpdateEmail() {
        when(emailInput.getText().toString()).thenReturn("updated.email@example.com");

        profileEditActivity.saveProfileData();

        verify(editor).putString("email", "updated.email@example.com");
    }

    @Test
    public void testUpdatePhoneWithValidNumber() {
        when(phoneInput.getText().toString()).thenReturn("1234567890");

        profileEditActivity.saveProfileData();

        verify(editor).putString("phone", "1234567890");
    }

    @Test
    public void testUpdateUsername() {
        when(userNameInput.getText().toString()).thenReturn("newusername");

        profileEditActivity.saveProfileData();

        verify(editor).putString("userName", "newusername");
    }
}


