package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private RecyclerView categoriesRecycler;
    private BottomNavigationView bottomNavigationView;
    private GuideItem currentGuideItem;
    private CircleImageView guideImage;
    private TextView guideText;
    private FloatingActionButton locationButton;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the GuideItem from intent
        currentGuideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        initializeViews();
        setupMap();
        setupCategoriesRecycler();
        setupBottomNavigation();
        setupGuideInfo();
        setupLocationButton();
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
                Log.e("MapActivity", "Security Exception: " + e.getMessage());
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
        // TODO: Set up categories adapter
    }

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

        // Enable location layer if permission is granted
        if (checkLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
            // Disable the default location button since we're using our own FAB
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Set Dresden as center
        LatLng dresden = new LatLng(51.0504, 13.7373);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dresden, 13));

        // Add any map markers or additional setup specific to the current guide
        if (currentGuideItem != null) {
            setupMapForGuide();
        }
    }

    private void setupMapForGuide() {
        // TODO: Add specific locations/markers for the current guide
        // This will be implemented when you have the specific locations for each guide
    }
}