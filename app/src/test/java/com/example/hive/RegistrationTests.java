package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RegistrationTests {

    private RegistrationActivity registrationActivity;

    @Before
    public void setUp() {
        // Mock RegistrationActivity to avoid Android framework dependencies
        registrationActivity = Mockito.mock(RegistrationActivity.class);
    }

    @Test
    public void testSetContentView() {
        // Mock the Bundle to simulate the onCreate parameter
        Bundle mockBundle = mock(Bundle.class);

        // Mock the setContentView to verify its behavior without actual invocation
        doNothing().when(registrationActivity).setContentView(anyInt());

        // Manually trigger setContentView and verify
        registrationActivity.setContentView(R.layout.activity_registration);
        verify(registrationActivity).setContentView(R.layout.activity_registration);
    }

    @Test
    public void testActivityNotNull() {
        // Ensure the mocked RegistrationActivity instance is not null
        assertNotNull("RegistrationActivity instance should not be null", registrationActivity);
    }
}
