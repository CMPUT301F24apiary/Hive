package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class CancelledListActivityTest {

    @Mock
    FirebaseFirestore mockFirestore;

    @Mock
    DocumentReference mockDocumentReference;

    @Mock
    DocumentSnapshot mockDocumentSnapshot;

    private CancelledListActivity cancelledListActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cancelledListActivity = new CancelledListActivity();
        cancelledListActivity.db = mockFirestore;
    }

    @Test
    public void testFetchCancelledList() {
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();
        Task<DocumentSnapshot> mockTask = tcs.getTask();

        when(mockFirestore.collection("events").document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);

        tcs.setResult(mockDocumentSnapshot);

        when(mockDocumentSnapshot.getString("cancelledlistID")).thenReturn("mockCancelledListId");

        cancelledListActivity.fetchCancelledList();

        verify(mockDocumentReference).get();
        assertEquals("mockCancelledListId", cancelledListActivity.cancelledListId);
    }

}
