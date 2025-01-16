package com.ridwan.tales_of_dd.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.character.detail.LandmarkItem;
import com.ridwan.tales_of_dd.ui.character.detail.LandmarksAdapter;
import com.ridwan.tales_of_dd.ui.character.detail.SpacesItemDecoration;
import com.ridwan.tales_of_dd.ui.poi.detail.POIDetailActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.utils.DatabaseHelper;
import com.ridwan.tales_of_dd.utils.LandmarkManager;
import com.ridwan.tales_of_dd.utils.LocationManager;
import com.ridwan.tales_of_dd.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, ProximityManager.NarrativeListener {

    private static final String TAG = "MapFragment";

    // UI elements
    private GoogleMap googleMap;
    private RecyclerView landmarksRecycler;
    private FloatingActionButton locationButton;
    private TextView guideText;
    private View rootView;

    // Location and proximity management
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ProximityManager proximityManager;

    // Landmarks and guide info
    private List<Landmark> landmarks;
    private GuideItem currentGuideItem;

    public static MapFragment newInstance(GuideItem guideItem) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable("guide_item", guideItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Get guide item from arguments first
        if (getArguments() != null) {
            currentGuideItem = (GuideItem) getArguments().getSerializable("guide_item");
            Log.d(TAG, "Guide item received: " + (currentGuideItem != null ? currentGuideItem.getTitle() : "null"));
        }

        initializeViews();
        initializeDependencies();

        // Setup ProximityManager for narrative tracking
        proximityManager = new ProximityManager();
        proximityManager.setNarrativeListener(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupLocationButton();

        // Request location permissions
        LocationManager.checkAndRequestLocationPermission(requireActivity(),
                new LocationManager.PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted() {
                        if (isAdded()) {
                            initializeLocation();
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        if (isAdded()) {
                            Log.e(TAG, "Location permission denied.");
                            Toast.makeText(requireContext(),
                                    "Location permission is required for full functionality",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return rootView;
    }

    private void initializeDependencies() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Get guide item from arguments
        if (getArguments() != null) {
            currentGuideItem = (GuideItem) getArguments().getSerializable("guide_item");
        }

        if (currentGuideItem != null) {
            Log.d(TAG, "Guide item received: " + currentGuideItem.getTitle());
        } else {
            Log.w(TAG, "No guide item received.");
            guideText.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        landmarksRecycler = rootView.findViewById(R.id.landmarks_recycler);
        locationButton = rootView.findViewById(R.id.fab_location);
        guideText = rootView.findViewById(R.id.guide_text);
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Apply map styling and center on Dresden
        MapUtils.applyMapStyle(requireContext(), googleMap);
        MapUtils.centerMapOnLocation(googleMap, new LatLng(51.0504, 13.7373), 13);

        // Enable user location if we have permission
        LocationManager.enableMyLocation(requireContext(), googleMap, fusedLocationClient);

        // Load landmarks and setup other map features
        loadLandmarksOnMap();
        setupCustomInfoWindow();

        // Setup guide info if we have a guide
        if (currentGuideItem != null) {
            setupGuideInfo();
            fetchLandmarksFromDatabase(currentGuideItem.getId());
        }
    }

    private void initializeLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void loadLandmarksOnMap() {
        if (!isAdded() || googleMap == null) return;

        new Thread(() -> {
            landmarks = DatabaseHelper.getAllLandmarks(requireContext());
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    if (landmarks != null) {
                        for (Landmark landmark : landmarks) {
                            addLandmarkMarker(landmark);
                        }
                        showInitialWelcomeMessage();
                    }
                });
            }
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
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.map_info_window, null);
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
            Log.e(TAG, "Landmark is null, cannot show details");
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                float[] results = new float[1];
                float distanceInKm = 0.0f;

                if (location != null) {
                    Location.distanceBetween(
                            location.getLatitude(),
                            location.getLongitude(),
                            landmark.getLatitude(),
                            landmark.getLongitude(),
                            results
                    );
                    distanceInKm = results[0] / 1000;
                }

                launchPOIDetailActivity(landmark, distanceInKm);
            });
        } else {
            launchPOIDetailActivity(landmark, 0.0f);
        }
    }

    private void launchPOIDetailActivity(Landmark landmark, float distance) {
        Intent intent = new Intent(requireContext(), POIDetailActivity.class);
        intent.putExtra("poi", new PointOfInterest(
                landmark.getName(),
                landmark.getDescription(),
                landmark.getImageUrl(),
                distance,
                landmark.getDetailedDescription()
        ));
        intent.putExtra("guide_item", currentGuideItem);
        startActivity(intent);
    }

    private void setupGuideInfo() {
        if (currentGuideItem != null && isAdded()) {
            ImageView guideImage = rootView.findViewById(R.id.guide_image);
            Glide.with(this)
                    .load(currentGuideItem.getImageUrl())
                    .centerCrop()
                    .into(guideImage);
        }
    }

    private void showInitialWelcomeMessage() {
        if (currentGuideItem != null) {
            new Thread(() -> {
                List<LandmarkCharacter> characters = DatabaseHelper.getLandmarkCharactersByGuideItemId(
                        requireContext(),
                        currentGuideItem.getId()
                );
                if (!characters.isEmpty()) {
                    Landmark landmark = DatabaseHelper.getLandmarkById(
                            requireContext(),
                            characters.get(0).landmarkId
                    );
                    if (landmark != null && isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                guideText.setText(String.format("Welcome to Dresden! Your guide, %s, suggests visiting %s.",
                                        currentGuideItem.getTitle(), landmark.getName()))
                        );
                    }
                }
            }).start();
        }
    }

    @Override
    public void onNarrativeTriggered(Landmark landmark) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() ->
                    guideText.setText(String.format("%s tells you about %s: %s",
                            currentGuideItem.getTitle(), landmark.getName(), landmark.getDescription()))
            );
        }
    }

    private void fetchLandmarksFromDatabase(int characterId) {
        if (!isAdded()) return; // Add this check

        LandmarkManager.fetchLandmarksByCharacterId(requireContext(), characterId,
                new LandmarkManager.LandmarkFetchCallback() {
                    @Override
                    public void onLandmarksFetched(List<Landmark> fetchedLandmarks) {
                        if (!isAdded()) return; // Add this check

                        if (fetchedLandmarks != null && !fetchedLandmarks.isEmpty()) {
                            requireActivity().runOnUiThread(() -> {
                                setupLandmarksRecycler(fetchedLandmarks);
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(),
                                        "No landmarks associated with this guide.",
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return; // Add this check

                        Log.e(TAG, message);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(),
                                    "Failed to fetch landmarks.",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void setupLandmarksRecycler(List<Landmark> landmarks) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            landmarksRecycler.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );

            List<LandmarkItem> landmarkItems = new ArrayList<>();
            for (Landmark landmark : landmarks) {
                landmarkItems.add(new LandmarkItem(
                        landmark.getId(),
                        landmark.getName(),
                        landmark.getImageUrl(),
                        landmark.getLatitude(),
                        landmark.getLongitude()
                ));
            }

            LandmarksAdapter landmarksAdapter = new LandmarksAdapter(landmarkItems,
                    landmark -> centerMapOnLandmark(landmark.getLatitude(), landmark.getLongitude()));

            landmarksRecycler.setAdapter(landmarksAdapter);

            int spacing = getResources().getDimensionPixelSize(R.dimen.landmark_item_spacing);
            landmarksRecycler.addItemDecoration(new SpacesItemDecoration(spacing));

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(landmarksRecycler);
        });
    }

    private void centerMapOnLandmark(double latitude, double longitude) {
        if (googleMap != null) {
            LatLng position = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f));
        }
    }

    private void setupLocationButton() {
        locationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null && googleMap != null) {
                        LatLng currentLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)
                        );
                    } else {
                        Log.e(TAG, "Failed to get current location.");
                    }
                });
            } else {
                Log.e(TAG, "Location permission not granted.");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null && landmarks != null) {
                    proximityManager.checkProximity(locationResult.getLastLocation(), landmarks);
                }
            }
        };
        LocationManager.startLocationUpdates(requireContext(), fusedLocationClient, locationCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationManager.stopLocationUpdates(fusedLocationClient, locationCallback);
    }
}