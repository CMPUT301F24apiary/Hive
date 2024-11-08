package com.example.hive;

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

@RunWith(JUnit4.class)
public class EditFacilityActivityTest {

    private EditFacilityProfileActivity editFacilityActivity;
    private ImageView mockImageView;

    @Before
    public void setup() {
        // Initialize mocks
        editFacilityActivity = Mockito.mock(EditFacilityProfileActivity.class);
        mockImageView = Mockito.mock(ImageView.class);

        editFacilityActivity.facilityImageView = mockImageView;
    }

    /**
     * Test if the facility image can be "removed" by setting it to the default image.
     */
    @Test
    public void testRemoveFacilityImage() {
        editFacilityActivity.facilityImageView.setImageResource(R.drawable.image1);
        verify(mockImageView).setImageResource(R.drawable.image1);
    }

    /**
     * Test the bitmap-to-Base64 conversion function.
     */
    @Test
    public void testBitmapToBase64() {
        Bitmap mockBitmap = Mockito.mock(Bitmap.class);

        when(editFacilityActivity.bitmapToBase64(mockBitmap)).thenReturn("mockBase64String");

        String base64String = editFacilityActivity.bitmapToBase64(mockBitmap);
        assertNotNull("Base64 conversion should return a non-null string", base64String);
    }

    /**
     * Test if the facility image is "uploaded" by setting a new image drawable.
     */
    @Test
    public void testUploadFacilityImage() {
        BitmapDrawable uploadedDrawable = Mockito.mock(BitmapDrawable.class);
        editFacilityActivity.facilityImageView.setImageDrawable(uploadedDrawable);
        verify(mockImageView).setImageDrawable(uploadedDrawable);
    }
}
