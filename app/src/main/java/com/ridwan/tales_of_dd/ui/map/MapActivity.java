package com.ridwan.tales_of_dd.ui.map;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.poi.detail.POIDetailActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Request code for location permission
    private GoogleMap googleMap; // Google Map instance
    private RecyclerView categoriesRecycler;
    private BottomNavigationView bottomNavigationView;
    private GuideItem currentGuideItem;
    private CircleImageView guideImage;
    private TextView guideText;
    private FloatingActionButton locationButton;
    private FusedLocationProviderClient fusedLocationClient; // Location provider for accessing the device's location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the GuideItem from intent
        currentGuideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        // Initialize UI components and setup map, navigation, and other features
        initializeViews();
        setupMap();
        setupCategoriesRecycler();
        setupBottomNavigation();
        setupGuideInfo();
        setupLocationButton();
    }

    /**
     * Initializes UI components.
     */
    private void initializeViews() {
        categoriesRecycler = findViewById(R.id.categories_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        guideImage = findViewById(R.id.guide_image);
        guideText = findViewById(R.id.guide_text);
        locationButton = findViewById(R.id.fab_location);
    }

    /**
     * Sets up the floating action button (FAB) for showing the device's current location.
     */
    private void setupLocationButton() {
        locationButton.setOnClickListener(v -> getDeviceLocation());
    }

    /**
     * Fetches and moves the map camera to the user's current location.
     */
    private void getDeviceLocation() {
        if (checkLocationPermission()) {
            try {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );
                                googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                currentLocation, 15f
                                        )
                                );
                            }
                        });
            } catch (SecurityException e) {
                Log.e("MapActivity", "Security Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if location permission is granted and requests it if not.
     *
     * @return true if permission is granted, false otherwise.
     */
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            }
        }
    }

    /**
     * Configures and displays guide-specific information in the UI.
     *
     * - Loads the guide's image using Glide and displays it in the `guideImage` view.
     * - Sets the guide's descriptive text (e.g., "15 places about <Guide Title>") in the `guideText` view.
     * - Ensures the guide container (`R.id.guide_container`) is visible if it exists in the layout.
     *
     * This method ensures the guide-related UI elements are properly displayed based on the provided guide data.
     */
    private void setupGuideInfo() {
        if (currentGuideItem != null) {
            // Load guide image using Glide
            Glide.with(this)
                    .load(currentGuideItem.getImageUrl())
                    .centerCrop()
                    .into(guideImage);

            // Set the guide text
            String guideInfo = String.format("15 places about %s", currentGuideItem.getTitle());
            guideText.setText(guideInfo);

            // Show the guide container
            View guideContainer = findViewById(R.id.guide_container);
            if (guideContainer != null) {
                guideContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Sets up the Google Map fragment and callback.
     */
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Configures the categories recycler view.
     */
    private void setupCategoriesRecycler() {
        categoriesRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // TODO: Set up categories adapter
    }

    /**
     * Sets up the bottom navigation bar and handles navigation actions.
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_guide) {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_map) {
                return true;
            } else if (itemId == R.id.navigation_collection) {
                startActivity(new Intent(this, CollectionActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_about) {
                startActivity(new Intent(this, AboutActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Set map style to dark mode
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_dark));
        } catch (Resources.NotFoundException e) {
            Log.e("MapActivity", "Can't find style.", e);
        }

        // Set up custom info window
        setupCustomInfoWindow();

        // Enable location layer if permission is granted
        if (checkLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
            // Disable the default location button since we're using our own FAB
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Set Dresden as center
        LatLng dresden = new LatLng(51.0504, 13.7373);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dresden, 13));


        // Load landmarks
        loadLandmarks();

        // Add any map markers or additional setup specific to the current guide
        if (currentGuideItem != null) {
            setupMapForGuide();
        }
    }

    private void setupMapForGuide() {
        // TODO: Add specific locations/markers for the current guide
        // This will be implemented when you have the specific locations for each guide
    }

    /**
     * Fetches landmarks from the database and displays them on the map.
     */
    private void loadLandmarks() {
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            List<Landmark> landmarks = db.landmarkDao().getAllLandmarks();
            runOnUiThread(() -> addLandmarksToMap(landmarks));
        }).start();
    }

    /**
     * Adds markers for landmarks on the map.
     *
     * @param landmarks List of landmarks to display on the map.
     */
    private void addLandmarksToMap(List<Landmark> landmarks) {
        if (googleMap == null || landmarks == null) return;

        for (Landmark landmark : landmarks) {
            LatLng position = new LatLng(landmark.getLatitude(), landmark.getLongitude());

            // Create a brief snippet from the description
            String snippet = landmark.getDescription();
            if (snippet.length() > 100) {
                snippet = snippet.substring(0, 97) + "...";
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(landmark.getName())
                    .snippet(landmark.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            // Add marker to map
            Marker marker = googleMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(landmark);
            }
        }
    }

    /**
     * Displays detailed information about a landmark in the POIDetailActivity.
     *
     * If location permission is granted and the user's current location is available,
     * calculates the distance between the user's location and the landmark, and includes this
     * distance in the displayed details. If location data is unavailable, defaults to a distance
     * of 0.0 km.
     *
     * @param landmark The landmark object containing details such as name, description, coordinates,
     *                 and image URL.
     */
    private void showLandmarkDetails(Landmark landmark) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(
                            location.getLatitude(), location.getLongitude(),
                            landmark.getLatitude(), landmark.getLongitude(),
                            results
                    );

                    PointOfInterest poi = new PointOfInterest(
                            landmark.getName(),
                            landmark.getDescription(), // Short description
                            landmark.getImageUrl(),
                            results[0] / 1000,  // Convert meters to kilometers
                            landmark.getDetailedDescription()  // Full description
                    );

                    Intent intent = new Intent(this, POIDetailActivity.class);
                    intent.putExtra("poi", poi);
                    startActivity(intent);
                }
            });
        } else {
            PointOfInterest poi = new PointOfInterest(
                    landmark.getName(),
                    landmark.getDescription(), // Short description
                    landmark.getImageUrl(),
                    0.0,  // Default distance
                    landmark.getDetailedDescription()  // Full description
            );

            Intent intent = new Intent(this, POIDetailActivity.class);
            intent.putExtra("poi", poi);
            startActivity(intent);
        }
    }

    /**
     * Sets up a custom info window for markers on the map and defines a click listener for the info windows.
     *
     * - Customizes the content of the info window by inflating a layout and setting the title and snippet.
     * - When an info window is clicked, retrieves the associated landmark (stored in the marker's tag)
     *   and opens the `POIDetailActivity` to display detailed information about the landmark.
     */
    private void setupCustomInfoWindow() {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Use default window frame
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate custom layout
                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);

                // Get views
                TextView titleView = view.findViewById(R.id.info_title);
                TextView snippetView = view.findViewById(R.id.info_snippet);

                // Set content
                titleView.setText(marker.getTitle());
                snippetView.setText(marker.getSnippet());

                return view;
            }
        });

        // Handle info window clicks
        googleMap.setOnInfoWindowClickListener(marker -> {
            Landmark landmark = (Landmark) marker.getTag();
            if (landmark != null) {
                showLandmarkDetails(landmark);
            }
        });
    }

}