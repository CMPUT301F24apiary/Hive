package com.example.hive;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.os.Bundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RegistrationTests {

    private RegistrationActivity registrationActivity;
    private Bundle mockBundle;

    @BeforeEach
    public void setUp() {
        // Create a mock of RegistrationActivity
        registrationActivity = mock(RegistrationActivity.class);

        // Mock Bundle to pass in the onCreate method
        mockBundle = mock(Bundle.class);
    }

    @Test
    public void testOnCreate() {
        // Call the onCreate method with a mocked Bundle
        registrationActivity.onCreate(mockBundle);

        // Capture the layout being set in setContentView
        ArgumentCaptor<Integer> layoutCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(registrationActivity).setContentView(layoutCaptor.capture());

        // Verify that the correct layout resource was set
        int capturedLayoutId = layoutCaptor.getValue();
        assertEquals(R.layout.activity_registration, capturedLayoutId);
    }
}
