package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RegistrationTests {

    private RegistrationActivity registrationActivity;

    @Before
    public void setUp() {
        // Create a spy of RegistrationActivity to retain its real behavior but allow control of Android components
        registrationActivity = Mockito.spy(RegistrationActivity.class);
    }

    @Test
    public void testOnCreate() {
        // Create a mock Bundle to pass into the onCreate method
        Bundle mockBundle = mock(Bundle.class);

        // Override the behavior of setContentView using doNothing()
        doNothing().when(registrationActivity).setContentView(anyInt());

        // Invoke the onCreate method
        registrationActivity.onCreate(mockBundle);

        // Capture the layout being set in setContentView
        ArgumentCaptor<Integer> layoutCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(registrationActivity).setContentView(layoutCaptor.capture());

        // Verify that the correct layout resource was set
        int capturedLayoutId = layoutCaptor.getValue();
        assertEquals(R.layout.activity_registration, capturedLayoutId);
    }
}
