package com.ridwan.tales_of_dd.ui.map;

import android.location.Location;
import android.util.Log;

import com.ridwan.tales_of_dd.data.entities.Landmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProximityManager {
    private static final String TAG = "ProximityManager";
    private static final float DEFAULT_TRIGGER_RADIUS = 30.0f; // meters
    private static final long COOLDOWN_PERIOD = 300000; // 5 minutes in milliseconds
    private final Map<Integer, LandmarkState> landmarkStates = new HashMap<>();
    private NarrativeListener listener;
    private Location lastLocation;

    public interface NarrativeListener {
        void onNarrativeTriggered(Landmark landmark);
    }

    private static class LandmarkState {
        boolean isTriggered;
        long lastTriggerTime;
        Long enteredProximityTime;
        float customRadius;
        boolean wasOutsideRadius;  // New flag to track if we were previously outside

        LandmarkState(float radius) {
            this.isTriggered = false;
            this.lastTriggerTime = 0;
            this.enteredProximityTime = null;
            this.customRadius = radius;
            this.wasOutsideRadius = true;  // Start as true to allow immediate first trigger
        }
    }

    public void setNarrativeListener(NarrativeListener listener) {
        this.listener = listener;
        Log.d(TAG, "Narrative listener set");
    }

    public void setCustomTriggerRadius(int landmarkId, float radius) {
        LandmarkState state = landmarkStates.get(landmarkId);
        if (state == null) {
            state = new LandmarkState(radius);
            landmarkStates.put(landmarkId, state);
        } else {
            state.customRadius = radius;
        }
        Log.d(TAG, String.format("Custom radius set for landmark %d: %.2f meters", landmarkId, radius));
    }

    public void checkProximity(Location currentLocation, List<Landmark> landmarks) {
        if (currentLocation == null || landmarks == null || listener == null) {
            Log.w(TAG, "Invalid parameters for proximity check");
            return;
        }

        // Update last location
        lastLocation = currentLocation;
        Log.d(TAG, String.format("Checking proximity at location: %.6f, %.6f",
                currentLocation.getLatitude(), currentLocation.getLongitude()));

        for (Landmark landmark : landmarks) {
            processSingleLandmark(currentLocation, landmark);
        }
    }

    private void processSingleLandmark(Location currentLocation, Landmark landmark) {
        // Get or create state for this landmark
        LandmarkState state = landmarkStates.get(landmark.getId());
        if (state == null) {
            state = new LandmarkState(DEFAULT_TRIGGER_RADIUS);
            landmarkStates.put(landmark.getId(), state);
        }

        float[] distance = new float[1];
        Location.distanceBetween(
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                landmark.getLatitude(),
                landmark.getLongitude(),
                distance
        );

        float triggerRadius = state.customRadius > 0 ? state.customRadius : DEFAULT_TRIGGER_RADIUS;

        Log.d(TAG, String.format(
                "=== PROXIMITY CHECK ===\n" +
                        "Landmark: %s\n" +
                        "Distance: %.2f meters\n" +
                        "Trigger Radius: %.2f meters\n" +
                        "Was Outside: %b\n" +
                        "Is Triggered: %b\n" +
                        "Last Trigger Time: %d",
                landmark.getName(),
                distance[0],
                triggerRadius,
                state.wasOutsideRadius,
                state.isTriggered,
                state.lastTriggerTime
        ));

        if (distance[0] <= triggerRadius) {
            handleInProximity(state, landmark, distance[0]);
        } else {
            handleOutOfProximity(state, landmark);
        }
    }

    private void handleInProximity(LandmarkState state, Landmark landmark, float distance) {
        long currentTime = System.currentTimeMillis();

        // Trigger if we were previously outside and enough time has passed since last trigger
        if (state.wasOutsideRadius &&
                (currentTime - state.lastTriggerTime) >= COOLDOWN_PERIOD) {

            Log.d(TAG, String.format("Triggering narrative for %s at distance %.2fm",
                    landmark.getName(), distance));
            triggerNarrative(state, landmark);
            state.wasOutsideRadius = false;  // Reset the outside radius flag
        }
    }

    private void handleOutOfProximity(LandmarkState state, Landmark landmark) {
        // Mark that we've been outside the radius
        if (!state.wasOutsideRadius) {
            Log.d(TAG, "Left proximity of " + landmark.getName());
            state.wasOutsideRadius = true;
            state.isTriggered = false;
        }
    }

    private void triggerNarrative(LandmarkState state, Landmark landmark) {
        state.isTriggered = true;
        state.lastTriggerTime = System.currentTimeMillis();
        Log.d(TAG, "Triggering narrative for: " + landmark.getName());
        listener.onNarrativeTriggered(landmark);
    }

    public void reset() {
        Log.d(TAG, "Resetting ProximityManager state");
        landmarkStates.clear();
        lastLocation = null;
    }

    public void resetLandmark(int landmarkId) {
        LandmarkState state = landmarkStates.get(landmarkId);
        if (state != null) {
            state.isTriggered = false;
            state.lastTriggerTime = 0;
            state.enteredProximityTime = null;
            Log.d(TAG, "Reset state for landmark: " + landmarkId);
        }
    }
}