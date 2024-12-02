package com.example.hive.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * EntrantMapActivity displays an equirectangular map and plots the locations
 * of entrants based on latitude and longitude retrieved from Firebase Firestore.
 *
 * This activity fetches the waiting list ID associated with an event,
 * retrieves user locations, and plots them as markers on a map.
 *
 * @author Dina
 */
public class EntrantMapActivity extends AppCompatActivity {

    private static final String TAG = "EntrantMapActivity";
    public FirebaseFirestore db;
    private String eventId;
    public ImageView mapView;
    public Bitmap mutableBitmap;
    public Canvas canvas;
    public Paint paint;

    /**
     * Initializes the EntrantMapActivity, setting up the map and fetching geolocation data.
     *
     * @param savedInstanceState The previously saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_map);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get eventId from the Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Received eventId: " + eventId);

        // Set up back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantMapActivity.this, OptionsPageActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            finish();
        });

        // Set up map view
        mapView = findViewById(R.id.equirectangular_map);
        mapView.setImageResource(R.drawable.equirectangular_projection);

        // Prepare the canvas for drawing
        BitmapDrawable drawable = (BitmapDrawable) mapView.getDrawable();
        mutableBitmap = drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Prepare paint for markers
        paint = new Paint();
        paint.setColor(Color.RED); // Marker color
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true); // Smooth edges

        // Fetch geolocation data and plot on map
        fetchAndDisplayGeolocations();
    }

    /**
     * Fetches geolocation data from Firebase Firestore and plots the locations on the map.
     */
    public void fetchAndDisplayGeolocations() {
        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String waitingListId = task.getResult().getString("waiting-list-id");
                if (waitingListId != null && !waitingListId.isEmpty()) {
                    Log.d(TAG, "Waiting list ID: " + waitingListId);

                    // Query the specific waiting-list document
                    db.collection("waiting-list").document(waitingListId).get().addOnCompleteListener(waitingListTask -> {
                        if (waitingListTask.isSuccessful() && waitingListTask.getResult() != null) {
                            Map<String, Object> userLocations = (Map<String, Object>) waitingListTask.getResult().get("user-locations");
                            if (userLocations != null) {
                                for (Map.Entry<String, Object> entry : userLocations.entrySet()) {
                                    Map<String, Double> locationData = (Map<String, Double>) entry.getValue();
                                    if (locationData != null) {
                                        Double latitude = locationData.get("latitude");
                                        Double longitude = locationData.get("longitude");

                                        if (latitude != null && longitude != null) {
                                            Log.d(TAG, "Plotting user location: Lat " + latitude + ", Long " + longitude);
                                            plotLocationOnMap(latitude, longitude);
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "No user-locations found in waiting-list document.");
                            }
                        } else {
                            Log.e(TAG, "Failed to fetch waiting-list document: ", waitingListTask.getException());
                        }
                    });
                } else {
                    Log.e(TAG, "No waiting-list ID found for event.");
                }
            } else {
                Log.e(TAG, "Failed to fetch event document: ", task.getException());
            }
        });
    }

    /**
     * Plots a single location on the map.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     */
    public void plotLocationOnMap(double latitude, double longitude) {
        double bitmapWidth = mutableBitmap.getWidth();
        double bitmapHeight = mutableBitmap.getHeight();

        // Convert latitude and longitude to pixel coordinates
        double x = ((longitude + 180) / 360) * bitmapWidth;
        double y = ((90 - latitude) / 180) * bitmapHeight;

        Log.d(TAG, "Bitmap dimensions -> Width: " + bitmapWidth + ", Height: " + bitmapHeight);
        Log.d(TAG, "Plotted marker at Lat: " + latitude + ", Long: " + longitude + " -> X: " + x + ", Y: " + y);

        // Ensure the coordinates are within bounds
        if (x < 0 || x > bitmapWidth || y < 0 || y > bitmapHeight) {
            Log.e(TAG, "Marker coordinates are out of bounds!");
            return;
        }

        // Draw marker on the canvas
        canvas.drawCircle((float) x, (float) y, 20, paint); // Marker radius

        // Update the ImageView
        mapView.setImageBitmap(mutableBitmap);
    }
}

