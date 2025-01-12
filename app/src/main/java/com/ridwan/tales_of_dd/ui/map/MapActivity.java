package com.ridwan.tales_of_dd.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.poi.detail.POIDetailActivity;
import com.ridwan.tales_of_dd.utils.DatabaseHelper;
import com.ridwan.tales_of_dd.utils.LocationManager;
import com.ridwan.tales_of_dd.utils.MapUtils;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ProximityManager.NarrativeListener {

    private static final String TAG = "MapActivity";

    // UI elements
    private GoogleMap googleMap;
    private RecyclerView categoriesRecycler;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton locationButton;
    private TextView guideText;

    // Location and proximity management
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ProximityManager proximityManager;

    // Landmarks and guide info
    private List<Landmark> landmarks;
    private GuideItem currentGuideItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize UI and dependencies
        initializeViews();
        initializeDependencies();

        // Setup and load the map
        initializeMap();
        setupGuideInfo();
        setupCategoriesRecycler();
        setupBottomNavigation();
        setupLocationButton();

        // Setup ProximityManager for narrative tracking
        proximityManager = new ProximityManager();
        proximityManager.setNarrativeListener(this);

        // Request location permissions
        LocationManager.checkAndRequestLocationPermission(this, new LocationManager.PermissionResultCallback() {
            @Override
            public void onPermissionGranted() {
                initializeMap();
            }

            @Override
            public void onPermissionDenied() {
                Log.e(TAG, "Location permission denied.");
            }
        });
    }

    // ------------------------ Initialization Methods ------------------------

    private void initializeViews() {
        categoriesRecycler = findViewById(R.id.categories_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        locationButton = findViewById(R.id.fab_location);
        guideText = findViewById(R.id.guide_text);
    }

    private void initializeDependencies() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentGuideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        if (currentGuideItem != null) {
            Log.d(TAG, "Guide item received: " + currentGuideItem.getTitle());
        } else {
            Log.e(TAG, "No guide item received.");
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // ------------------------ Map and Marker Methods ------------------------

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Apply map styling and center on Dresden
        MapUtils.applyMapStyle(this, googleMap);
        MapUtils.centerMapOnLocation(googleMap, new LatLng(51.0504, 13.7373), 13);

        // Enable user location and load landmarks
        LocationManager.enableMyLocation(this, googleMap, fusedLocationClient);
        loadLandmarksOnMap();
        setupCustomInfoWindow();
    }

    private void loadLandmarksOnMap() {
        new Thread(() -> {
            landmarks = DatabaseHelper.getAllLandmarks(this);
            runOnUiThread(() -> {
                if (landmarks != null) {
                    for (Landmark landmark : landmarks) {
                        addLandmarkMarker(landmark);
                    }
                    showInitialWelcomeMessage();
                }
            });
        }).start();
    }

    private void addLandmarkMarker(Landmark landmark) {
        if (googleMap == null || landmark == null) return;

        LatLng position = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        String snippet = landmark.getDescription().length() > 100
                ? landmark.getDescription().substring(0, 97) + "..."
                : landmark.getDescription();

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(landmark.getName())
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if (marker != null) {
            marker.setTag(landmark);
        }
    }

    private void setupCustomInfoWindow() {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = LayoutInflater.from(MapActivity.this).inflate(R.layout.map_info_window, null);
                TextView title = view.findViewById(R.id.info_title);
                TextView snippet = view.findViewById(R.id.info_snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return view;
            }
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Landmark landmark = (Landmark) marker.getTag();
            if (landmark != null) {
                showLandmarkDetails(landmark);
            }
        });
    }

    private void showLandmarkDetails(Landmark landmark) {
        if (landmark == null) {
            Log.e("MapActivity", "Landmark is null, cannot show details");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                float[] results = new float[1];
                float distanceInKm = 0.0f; // Default distance
                if (location != null) {
                    Location.distanceBetween(
                            location.getLatitude(),
                            location.getLongitude(),
                            landmark.getLatitude(),
                            landmark.getLongitude(),
                            results
                    );
                    distanceInKm = results[0] / 1000; // Convert distance to kilometers
                }

                // Pass Landmark, distance, and GuideItem to POIDetailActivity
                Intent intent = new Intent(this, POIDetailActivity.class);
                intent.putExtra("poi", new PointOfInterest(
                        landmark.getName(),
                        landmark.getDescription(),
                        landmark.getImageUrl(),
                        distanceInKm,
                        landmark.getDetailedDescription()
                ));
                intent.putExtra("guide_item", currentGuideItem); // Pass the currentGuideItem
                startActivity(intent);
            });
        } else {
            Log.e("MapActivity", "Location permission not granted, cannot get user location");
            // Pass Landmark and GuideItem with a default distance (0.0 km)
            Intent intent = new Intent(this, POIDetailActivity.class);
            intent.putExtra("poi", new PointOfInterest(
                    landmark.getName(),
                    landmark.getDescription(),
                    landmark.getImageUrl(),
                    0.0f,
                    landmark.getDetailedDescription()
            ));
            intent.putExtra("guide_item", currentGuideItem); // Pass the currentGuideItem
            startActivity(intent);
        }
    }

    // ------------------------ Guide Info and Welcome Message ------------------------

    private void setupGuideInfo() {
        if (currentGuideItem != null) {
            Glide.with(this)
                    .load(currentGuideItem.getImageUrl())
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.guide_image));
        }
    }

    private void showInitialWelcomeMessage() {
        if (currentGuideItem != null) {
            new Thread(() -> {
                List<LandmarkCharacter> characters = DatabaseHelper.getLandmarkCharactersByGuideItemId(this, currentGuideItem.getId());
                if (!characters.isEmpty()) {
                    Landmark landmark = DatabaseHelper.getLandmarkById(this, characters.get(0).landmarkId);
                    if (landmark != null) {
                        runOnUiThread(() -> guideText.setText(String.format("Welcome to Dresden! Your guide, %s, suggests visiting %s.",
                                currentGuideItem.getTitle(), landmark.getName())));
                    }
                }
            }).start();
        }
    }

    @Override
    public void onNarrativeTriggered(Landmark landmark) {
        runOnUiThread(() -> guideText.setText(String.format("%s tells you about %s: %s",
                currentGuideItem.getTitle(), landmark.getName(), landmark.getDescription())));
    }

    // ------------------------ Navigation and Recycler Setup ------------------------

    private void setupCategoriesRecycler() {
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    // Configure bottom navigation
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_guide) {
                intent = new Intent(this, GuideActivity.class);
            } else if (itemId == R.id.navigation_collection) {
                intent = new Intent(this, CollectionActivity.class);
            } else if (itemId == R.id.navigation_about) {
                intent = new Intent(this, AboutActivity.class);
            } else if (itemId == R.id.navigation_map) {
                // Already on the map screen
                return true;
            }

            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private void setupLocationButton() {
        locationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                            } else {
                                Log.e(TAG, "Failed to get current location.");
                            }
                        });
            } else {
                Log.e(TAG, "Location permission not granted.");
            }
        });
    }

    // ------------------------ Lifecycle Methods ------------------------

    @Override
    protected void onResume() {
        super.onResume();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null && landmarks != null) {
                    proximityManager.checkProximity(locationResult.getLastLocation(), landmarks);
                }
            }
        };
        LocationManager.startLocationUpdates(this, fusedLocationClient, locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.stopLocationUpdates(fusedLocationClient, locationCallback);
    }
}