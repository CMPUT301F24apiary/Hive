package com.example.hive;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EditFacilityActivityTest {

    private EditFacilityProfileActivity activity;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor editor;

    @Mock
    private EditText facilityNameEditText, emailEditText, phoneEditText;

    @Mock
    private ImageView facilityImageView;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        activity = new EditFacilityProfileActivity();
        activity.facilityNameEditText = facilityNameEditText;
        activity.emailEditText = emailEditText;
        activity.phoneEditText = phoneEditText;
        activity.facilityImageView = facilityImageView;

        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
    }

    @Test
    public void testValidateInputs_ValidData() {
        // Mock valid data input
        when(facilityNameEditText.getText().toString()).thenReturn("Community Center");
        when(emailEditText.getText().toString()).thenReturn("facility@example.com");
        when(phoneEditText.getText().toString()).thenReturn("1234567890");

        boolean isValid = activity.validateInputs();

        assertTrue(isValid, "Inputs should be valid.");
    }

    @Test
    public void testValidateInputs_InvalidEmail() {
        // Mock invalid email input
        when(facilityNameEditText.getText().toString()).thenReturn("Community Center");
        when(emailEditText.getText().toString()).thenReturn("invalid-email");
        when(phoneEditText.getText().toString()).thenReturn("1234567890");

        boolean isValid = activity.validateInputs();

        assertFalse(isValid, "Email should be valid.");
    }

    @Test
    public void testValidateInputs_InvalidPhone() {
        // Mock invalid phone input
        when(facilityNameEditText.getText().toString()).thenReturn("Community Center");
        when(emailEditText.getText().toString()).thenReturn("facility@example.com");
        when(phoneEditText.getText().toString()).thenReturn("12345");

        boolean isValid = activity.validateInputs();

        assertFalse(isValid, "Phone number should be valid.");
    }

    @Test
    public void testSaveFacilityData() {
        // Mock valid data input
        when(facilityNameEditText.getText().toString()).thenReturn("Community Center");
        when(emailEditText.getText().toString()).thenReturn("facility@example.com");
        when(phoneEditText.getText().toString()).thenReturn("1234567890");

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.image1);
        when(facilityImageView.getDrawable()).thenReturn(new BitmapDrawable(activity.getResources(), bitmap));

        when(sharedPreferences.getString("facilityName", "")).thenReturn("Community Center");
        when(sharedPreferences.getString("facilityEmail", "")).thenReturn("facility@example.com");
        when(sharedPreferences.getString("facilityPhone", "")).thenReturn("1234567890");

        activity.saveFacilityData();

        verify(editor).putString("facilityName", "Community Center");
        verify(editor).putString("facilityEmail", "facility@example.com");
        verify(editor).putString("facilityPhone", "1234567890");
        verify(editor).putString(anyString(), anyString());
        verify(editor).apply();
    }

    @Test
    public void testOpenImagePicker() {
        Intent resultIntent = new Intent();
        resultIntent.setData(Uri.parse("content://path/to/image"));

        activity.onActivityResult(EditFacilityProfileActivity.PICK_IMAGE, AppCompatActivity.RESULT_OK, resultIntent);
        verify(facilityImageView).setImageURI(eq(Uri.parse("content://path/to/image")));
    }

    @Test
    public void testRemovePicture() {
        activity.removePicture();
        verify(facilityImageView).setImageResource(R.drawable.image1);
    }
}

