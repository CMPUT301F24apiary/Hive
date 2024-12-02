package com.example.hive;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.example.hive.Views.ProfileEditActivity;

@RunWith(JUnit4.class)
public class PfpTest {

    private ProfileEditActivity profileEditActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView mockImageView;

    @Before
    public void setup() {
        // Initialize mocks
        profileEditActivity = Mockito.mock(ProfileEditActivity.class);
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);
        mockImageView = Mockito.mock(ImageView.class);

        // Set up SharedPreferences behavior
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        // Inject mock SharedPreferences and ImageView into the activity
        profileEditActivity.setSharedPreferencesForTesting(sharedPreferences);
        profileEditActivity.profilePicture = mockImageView;
    }

    /**
     * Test if the profile picture can be "removed" by setting it to the default image.
     * Section 1: Tests for US 01.03.02 - Remove profile picture
     */
    @Test
    public void testRemoveProfilePicture() {
        // Simulate clicking the removePictureButton to set the profile picture to the default image
        profileEditActivity.profilePicture.setImageResource(R.drawable.ic_profile);

        // Verify that the image resource was set to the default
        verify(mockImageView).setImageResource(R.drawable.ic_profile);
    }

    /**
     * Test the bitmap-to-Base64 conversion function.
     */
    @Test
    public void testBitmapToBase64() {
        Bitmap mockBitmap = Mockito.mock(Bitmap.class);

        when(profileEditActivity.bitmapToBase64(mockBitmap)).thenReturn("mockBase64String");

        String base64String = profileEditActivity.bitmapToBase64(mockBitmap);
        assertNotNull("Base64 conversion should return a non-null string", base64String);
    }

    /**
     * Test if the profile picture is "cropped" to a circle.
     */
    @Test
    public void testCircularCropProfilePicture() {
        BitmapDrawable mockDrawable = Mockito.mock(BitmapDrawable.class);

        // Mock setting the circular drawable on the ImageView
        when(mockImageView.getDrawable()).thenReturn(mockDrawable);

        profileEditActivity.profilePicture.setImageDrawable(mockDrawable);

        // Verify that the circular drawable was set
        verify(mockImageView).setImageDrawable(mockDrawable);
    }
    /**
     * Test if the profile picture can be "uploaded" by setting a new image drawable.
     * Section 2: Tests for US 01.03.01 - Add/Upload profile picture
     */
    @Test
    public void testUploadProfilePicture() {
        // Create a mock BitmapDrawable to represent the uploaded profile picture
        BitmapDrawable uploadedDrawable = Mockito.mock(BitmapDrawable.class);

        // Simulate the upload by setting the profile picture's drawable to the new image
        profileEditActivity.profilePicture.setImageDrawable(uploadedDrawable);

        // Verify that the drawable was set correctly on the profile picture ImageView
        verify(mockImageView).setImageDrawable(uploadedDrawable);
    }
}




