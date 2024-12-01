package com.example.hive;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.widget.ArrayAdapter;

public class WaitingListActivityTests {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private FirebaseController mockFirebaseController;

    private WaitingListActivity waitingListActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create a partial mock of WaitingListActivity using Mockito
        waitingListActivity = Mockito.spy(new WaitingListActivity());

        // Use reflection to set private fields
        setPrivateField(waitingListActivity, "db", mockFirestore);
        setPrivateField(waitingListActivity, "fbControl", mockFirebaseController);
        setPrivateField(waitingListActivity, "entrantsList", new ArrayList<>());
        setPrivateField(waitingListActivity, "adapter", Mockito.mock(ArrayAdapter.class));
        setPrivateField(waitingListActivity, "eventId", "testEventId");
    }

    @Test
    public void testFetchWaitingList_NoWaitingListId() {
        // Simulate document without waiting-list-id
        when(mockFirestore.collection("events").document("testEventId").get()).thenReturn(null);

        callPrivateMethod(waitingListActivity, "fetchWaitingList");

        // Verify no further actions were taken due to missing waiting-list-id
        verify(mockFirestore.collection("events").document("testEventId"), Mockito.times(1)).get();
    }

    @Test
    public void testFetchWaitingList_WithValidUserIds() {
        // Simulate a waiting-list document with user IDs
        List<String> userIds = Arrays.asList("user1", "user2", "user3");
        when(mockFirestore.collection("events").document("testEventId").get()).thenAnswer(invocation -> {
            DocumentSnapshot mockDocSnapshot = Mockito.mock(DocumentSnapshot.class);
            when(mockDocSnapshot.getString("waiting-list-id")).thenReturn("testWaitingListId");
            return mockDocSnapshot;
        });

        when(mockFirestore.collection("waiting-list").document("testWaitingListId")).thenAnswer(invocation -> {
            DocumentSnapshot mockSnapshot = Mockito.mock(DocumentSnapshot.class);
            when(mockSnapshot.get("user-ids")).thenReturn(userIds);
            return mockSnapshot;
        });

        callPrivateMethod(waitingListActivity, "fetchWaitingList");

        // Verify fetchUserByDeviceId was called for each user ID
        ArgumentCaptor<FirebaseController.OnUserFetchedListener> captor = ArgumentCaptor.forClass(FirebaseController.OnUserFetchedListener.class);
        for (String userId : userIds) {
            verify(mockFirebaseController).fetchUserByDeviceId(Mockito.eq(userId), captor.capture());
        }

        // Simulate successful user fetch
        for (FirebaseController.OnUserFetchedListener listener : captor.getAllValues()) {
            User mockUser = Mockito.mock(User.class);
            when(mockUser.getUserName()).thenReturn("TestUser");
            listener.onUserFetched(mockUser);
        }

        // Verify entrantsList was updated
        List<String> entrantsList = (List<String>) getPrivateField(waitingListActivity, "entrantsList");
        assertEquals(3, entrantsList.size());
    }

    // Reflection methods
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void callPrivateMethod(Object target, String methodName) {
        try {
            java.lang.reflect.Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
