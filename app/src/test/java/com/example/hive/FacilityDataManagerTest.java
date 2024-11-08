package com.example.hive;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.hive.Models.FacilityDataManager;

public class FacilityDataManagerTest {

    private FacilityDataManager facilityDataManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        facilityDataManager = new FacilityDataManager(sharedPreferences);
    }

    @Test
    public void testSaveFacilityDataWithAllFields() {
        String name = "Community Center";
        String email = "contact@community.com";
        String phone = "1234567890";

        facilityDataManager.saveFacilityData(name, email, phone);

        verify(editor).putString("facilityName", name);
        verify(editor).putString("facilityEmail", email);
        verify(editor).putString("facilityPhone", phone);
        verify(editor).apply();
    }

    @Test
    public void testSaveFacilityDataWithOnlyNameAndPhone() {
        String name = "Sports Complex";
        String email = "";
        String phone = "9876543210";

        facilityDataManager.saveFacilityData(name, email, phone);

        verify(editor).putString("facilityName", name);
        verify(editor).putString("facilityPhone", phone);
        verify(editor).apply();
    }

    @Test
    public void testSaveFacilityDataWithValidPhoneNumber() {
        String name = "Library";
        String email = "library@public.com";
        String phone = "0123456789";

        facilityDataManager.saveFacilityData(name, email, phone);

        assertTrue(phone.matches("\\d+"));
        verify(editor).putString("facilityPhone", phone);
    }


    @Test
    public void testSaveFacilityDataWithInvalidPhoneNumber() {
        String name = "Gym";
        String email = "gym@fitness.com";
        String phone = "phone123";

        facilityDataManager.saveFacilityData(name, email, phone);
        assertFalse(phone.matches("\\d+"));
    }
}
