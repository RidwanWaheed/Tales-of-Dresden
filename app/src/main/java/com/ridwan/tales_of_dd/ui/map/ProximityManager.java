package com.ridwan.tales_of_dd.ui.map;

import android.location.Location;

import com.ridwan.tales_of_dd.data.entities.Landmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages proximity-based narrative triggers for landmarks.
 */
public class ProximityManager {

    private static final float DEFAULT_TRIGGER_RADIUS = 30.0f; // meters
    private static final long COOLDOWN_PERIOD = 300000; // 5 minutes in milliseconds

    private final Map<Integer, LandmarkState> landmarkStates = new HashMap<>();
    private NarrativeListener listener;
    private Location lastLocation;

    /**
     * Interface for handling narrative triggers.
     */
    public interface NarrativeListener {
        void onNarrativeTriggered(Landmark landmark);
    }

    /**
     * State management for individual landmarks.
     */
    private static class LandmarkState {
        boolean isTriggered;
        long lastTriggerTime;
        Long enteredProximityTime;
        float customRadius;
        boolean wasOutsideRadius;

        LandmarkState(float radius) {
            this.isTriggered = false;
            this.lastTriggerTime = 0;
            this.enteredProximityTime = null;
            this.customRadius = radius;
            this.wasOutsideRadius = true; // Start as true to allow the first trigger
        }
    }

    /**
     * Sets the narrative listener to handle proximity events.
     *
     * @param listener the narrative listener
     */
    public void setNarrativeListener(NarrativeListener listener) {
        this.listener = listener;
    }

    /**
     * Sets a custom trigger radius for a specific landmark.
     *
     * @param landmarkId the landmark ID
     * @param radius     the custom trigger radius in meters
     */
    public void setCustomTriggerRadius(int landmarkId, float radius) {
        LandmarkState state = landmarkStates.get(landmarkId);
        if (state == null) {
            state = new LandmarkState(radius);
            landmarkStates.put(landmarkId, state);
        } else {
            state.customRadius = radius;
        }
    }

    /**
     * Checks the proximity of the current location to a list of landmarks.
     *
     * @param currentLocation the user's current location
     * @param landmarks       the list of landmarks to check
     */
    public void checkProximity(Location currentLocation, List<Landmark> landmarks) {
        if (currentLocation == null || landmarks == null || listener == null) {
            return; // Do nothing if inputs are invalid
        }

        lastLocation = currentLocation;

        for (Landmark landmark : landmarks) {
            processSingleLandmark(currentLocation, landmark);
        }
    }

    /**
     * Processes proximity checks for a single landmark.
     *
     * @param currentLocation the user's current location
     * @param landmark        the landmark to check
     */
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

        if (distance[0] <= triggerRadius) {
            handleInProximity(state, landmark);
        } else {
            handleOutOfProximity(state);
        }
    }

    /**
     * Handles the scenario where the user is within proximity of a landmark.
     *
     * @param state    the landmark's state
     * @param landmark the landmark
     */
    private void handleInProximity(LandmarkState state, Landmark landmark) {
        long currentTime = System.currentTimeMillis();

        if (state.wasOutsideRadius && (currentTime - state.lastTriggerTime) >= COOLDOWN_PERIOD) {
            triggerNarrative(state, landmark);
            state.wasOutsideRadius = false;
        }
    }

    /**
     * Handles the scenario where the user is outside the proximity of a landmark.
     *
     * @param state the landmark's state
     */
    private void handleOutOfProximity(LandmarkState state) {
        if (!state.wasOutsideRadius) {
            state.wasOutsideRadius = true;
            state.isTriggered = false;
        }
    }

    /**
     * Triggers the narrative for the landmark.
     *
     * @param state    the landmark's state
     * @param landmark the landmark
     */
    private void triggerNarrative(LandmarkState state, Landmark landmark) {
        state.isTriggered = true;
        state.lastTriggerTime = System.currentTimeMillis();
        if (listener != null) {
            listener.onNarrativeTriggered(landmark);
        }
    }

    /**
     * Resets the state of all landmarks.
     */
    public void reset() {
        landmarkStates.clear();
        lastLocation = null;
    }

    /**
     * Resets the state of a specific landmark.
     *
     * @param landmarkId the landmark ID
     */
    public void resetLandmark(int landmarkId) {
        LandmarkState state = landmarkStates.get(landmarkId);
        if (state != null) {
            state.isTriggered = false;
            state.lastTriggerTime = 0;
            state.enteredProximityTime = null;
        }
    }
}