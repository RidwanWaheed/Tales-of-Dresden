package com.ridwan.tales_of_dd.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.ridwan.tales_of_dd.R;

public class MapUtils {
    private static final String TAG = "MapUtils";

    /**
     * Applies a custom style to the provided GoogleMap instance.
     *
     * @param context the application context
     * @param map     the GoogleMap instance to style
     */
    public static void applyMapStyle(Context context, GoogleMap map) {
        try {
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_darkyellow)
            );

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying map style.", e);
        }
    }

    /**
     * Centers the map on a specified location with a given zoom level.
     *
     * @param map     the GoogleMap instance
     * @param latLng  the target location (latitude and longitude)
     * @param zoom    the desired zoom level
     */
    public static void centerMapOnLocation(GoogleMap map, LatLng latLng, float zoom) {
        if (map != null && latLng != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    /**
     * Configures GoogleMap settings, such as enabling/disabling UI elements.
     *
     * @param map the GoogleMap instance
     */
    public static void configureMapSettings(GoogleMap map) {
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}