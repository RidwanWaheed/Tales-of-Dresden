package com.ridwan.tales_of_dd.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LocationManager {
    private static final String TAG = "LocationManager";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Checks and requests location permissions.
     *
     * @param activity the activity context
     * @param callback the callback to handle permission results
     */
    public static void checkAndRequestLocationPermission(Activity activity, PermissionResultCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            callback.onPermissionGranted();
        }
    }

    /**
     * Handles the result of a location permission request.
     *
     * @param requestCode  the request code
     * @param permissions  the requested permissions
     * @param grantResults the grant results
     * @param callback     the callback to handle permission results
     */
    public static void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionResultCallback callback) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    /**
     * Enables location services on the map if permissions are granted.
     *
     * @param context             the application context
     * @param googleMap           the GoogleMap instance
     * @param fusedLocationClient the FusedLocationProviderClient
     */
    public static void enableMyLocation(Context context, GoogleMap googleMap, FusedLocationProviderClient fusedLocationClient) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Log.d(TAG, "User location: " + location.getLatitude() + ", " + location.getLongitude());
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to get user location: " + e.getMessage()));
        } else {
            Log.e(TAG, "Location permission not granted.");
        }
    }

    /**
     * Centers the map on the device's last known location.
     *
     * @param map      the GoogleMap instance
     * @param location the last known location
     */
    public static void centerMapOnLocation(GoogleMap map, Location location) {
        if (map != null && location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
        }
    }

    /**
     * Starts location updates with a specified interval and callback.
     *
     * @param context             the application context
     * @param fusedLocationClient the FusedLocationProviderClient
     * @param locationCallback    the callback for receiving location updates
     */
    public static void startLocationUpdates(Context context, FusedLocationProviderClient fusedLocationClient, LocationCallback locationCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                    .setIntervalMillis(3000)
                    .setMinUpdateIntervalMillis(2000)
                    .build();

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Log.e(TAG, "Location permission not granted.");
        }
    }

    /**
     * Stops location updates for the given callback.
     *
     * @param fusedLocationClient the FusedLocationProviderClient
     * @param locationCallback    the callback to remove
     */
    public static void stopLocationUpdates(FusedLocationProviderClient fusedLocationClient, LocationCallback locationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Callback interface for handling location permission results.
     */
    public interface PermissionResultCallback {
        void onPermissionGranted();

        void onPermissionDenied();
    }
}