package com.ridwan.tales_of_dd.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.poi.detail.POIDetailActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ProximityManager.NarrativeListener {
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private RecyclerView categoriesRecycler;
    private BottomNavigationView bottomNavigationView;
    private GuideItem currentGuideItem;
    private CircleImageView guideImage;
    private TextView guideText;
    private FloatingActionButton locationButton;
    private FusedLocationProviderClient fusedLocationClient;
    private ProximityManager proximityManager;
    private LocationCallback locationCallback;
    private List<Landmark> landmarks;

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Let the system handle the back press naturally
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Clear any existing back stack
        if (getIntent().getFlags() != Intent.FLAG_ACTIVITY_NEW_TASK) {
            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        proximityManager = new ProximityManager();
        proximityManager.setNarrativeListener(this);

        currentGuideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");
        if (currentGuideItem != null) {
            Log.d(TAG, "Received guide item: " + currentGuideItem.getTitle());
        } else {
            Log.e(TAG, "No guide item received in intent");
        }

        initializeViews();
        setupMap();
        setupCategoriesRecycler();
        setupBottomNavigation();
        setupGuideInfo();
        setupLocationButton();

        // Make sure guide container starts invisible until we have the welcome message
        View guideContainer = findViewById(R.id.guide_container);
        if (guideContainer != null) {
            guideContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeViews() {
        categoriesRecycler = findViewById(R.id.categories_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        guideImage = findViewById(R.id.guide_image);
        guideText = findViewById(R.id.guide_text);
        locationButton = findViewById(R.id.fab_location);
    }

    private void setupLocationButton() {
        locationButton.setOnClickListener(v -> getDeviceLocation());
    }

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
                Log.e(TAG, "Security Exception: " + e.getMessage());
            }
        }
    }

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

    private void setupGuideInfo() {
        if (currentGuideItem != null) {
            Glide.with(this)
                    .load(currentGuideItem.getImageUrl())
                    .centerCrop()
                    .into(guideImage);
        }
    }

    private void showInitialWelcomeMessage() {
        if (currentGuideItem != null) {
            AppDatabase db = AppDatabase.getInstance(this);
            new Thread(() -> {
                List<LandmarkCharacter> landmarkCharacters = db.landmarkCharacterDao()
                        .getLandmarkCharactersByCharacterId(currentGuideItem.getId());

                if (!landmarkCharacters.isEmpty()) {
                    Landmark landmark = db.landmarkDao().getLandmarkById(landmarkCharacters.get(0).landmarkId);
                    if (landmark != null) {
                        runOnUiThread(() -> {
                            String message = String.format(
                                    "Welcome to Dresden! Your guide, %s, suggests starting with a visit to %s. It's one of the city's most iconic landmarks.",
                                    currentGuideItem.getTitle(),
                                    landmark.getName()
                            );

                            View guideContainer = findViewById(R.id.guide_container);
                            if (guideContainer != null) {
                                guideContainer.setVisibility(View.VISIBLE);
                            }

                            guideText.setText(message);
                            Log.d(TAG, "Welcome message set: " + message);
                        });
                    }
                }
            }).start();
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupCategoriesRecycler() {
        categoriesRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Check if this is a new guide item
        if (intent.hasExtra("guide_item")) {
            GuideItem newGuideItem = (GuideItem) intent.getSerializableExtra("guide_item");
            if (newGuideItem != null &&
                    (currentGuideItem == null || currentGuideItem.getId() != newGuideItem.getId())) {
                currentGuideItem = newGuideItem;
                setupGuideInfo();
                if (googleMap != null) {
                    loadLandmarks();
                }
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_map) {
                return true; // Already on map
            } else if (itemId == R.id.navigation_guide) {
                intent = new Intent(this, GuideActivity.class);
            } else if (itemId == R.id.navigation_collection) {
                intent = new Intent(this, CollectionActivity.class);
            } else if (itemId == R.id.navigation_about) {
                intent = new Intent(this, AboutActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_dark));
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }

        setupCustomInfoWindow();

        if (checkLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        LatLng dresden = new LatLng(51.0504, 13.7373);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dresden, 13));

        loadLandmarks();
    }

    private void loadLandmarks() {
        Log.d(TAG, "Loading landmarks from database");
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            landmarks = db.landmarkDao().getAllLandmarks();
            if (landmarks != null) {
                runOnUiThread(() -> {
                    addLandmarksToMap(landmarks);
                    showInitialWelcomeMessage();
                });
            }
        }).start();
    }

    private void addLandmarksToMap(List<Landmark> landmarks) {
        if (googleMap == null || landmarks == null) return;

        for (Landmark landmark : landmarks) {
            LatLng position = new LatLng(landmark.getLatitude(), landmark.getLongitude());
            String snippet = landmark.getDescription();
            if (snippet.length() > 100) {
                snippet = snippet.substring(0, 97) + "...";
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(landmark.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            Marker marker = googleMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(landmark);
            }
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
                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);
                TextView titleView = view.findViewById(R.id.info_title);
                TextView snippetView = view.findViewById(R.id.info_snippet);
                titleView.setText(marker.getTitle());
                snippetView.setText(marker.getSnippet());
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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                float[] results = new float[1];
                float distance = 0.0f;

                if (location != null) {
                    Location.distanceBetween(
                            location.getLatitude(), location.getLongitude(),
                            landmark.getLatitude(), landmark.getLongitude(),
                            results
                    );
                    distance = results[0];
                }

                PointOfInterest poi = new PointOfInterest(
                        landmark.getName(),
                        landmark.getDescription(),
                        landmark.getImageUrl(),
                        distance / 1000,
                        landmark.getDetailedDescription()
                );

                Intent intent = new Intent(this, POIDetailActivity.class);
                intent.putExtra("poi", poi);
                startActivity(intent);
            });
        }
    }

    private void setupLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(3000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null && landmarks != null) {
                    proximityManager.checkProximity(location, landmarks);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    @Override
    public void onNarrativeTriggered(Landmark landmark) {
        runOnUiThread(() -> {
            String narrative = String.format("%s tells you about %s: %s",
                    currentGuideItem.getTitle(),
                    landmark.getName(),
                    landmark.getDescription());

            guideText.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        guideText.setText(narrative);
                        guideText.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .start();
                    })
                    .start();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        setupLocationUpdates();
        if (proximityManager != null) {
            proximityManager.reset();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        removeLocationUpdates();
    }

    private void removeLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}