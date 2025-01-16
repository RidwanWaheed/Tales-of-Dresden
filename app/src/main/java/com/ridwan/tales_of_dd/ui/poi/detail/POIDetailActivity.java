package com.ridwan.tales_of_dd.ui.poi.detail;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.map.ProximityManager;
import com.ridwan.tales_of_dd.utils.LocationManager;
import com.ridwan.tales_of_dd.utils.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class POIDetailActivity extends AppCompatActivity implements ProximityManager.NarrativeListener {

    // Constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final float ALLOWED_DISTANCE_METERS = 30.0f;

    // UI Elements
    private ImageView poiImage;
    private TextView poiTitle;
    private TextView poiDetailedDescription;
    private TextView poiDistance;
    private TextView guideComment;
    private ImageView guideImage;
    private ImageButton backButton;
    private ImageButton cameraButton;

    // Location and Proximity
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Landmark currentLandmark;
    private float currentDistance = Float.MAX_VALUE;  // Track current distance

    private GuideItem guideItem;

    // Photo capture
    private String currentPhotoPath;
    private String landmarkName;

    private static final String TAG = "POIDetailActivity"; // Replace YourClassName with the actual class name.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        initializeViews();
        setupLocationServices();
        setupClickListeners();
        handleIntentData();
    }

    // ------------------------ Initialization Methods ------------------------

    private void initializeViews() {
        poiImage = findViewById(R.id.poi_image);
        poiTitle = findViewById(R.id.poi_title);
        poiDetailedDescription = findViewById(R.id.poi_detailed_description);
        poiDistance = findViewById(R.id.poi_distance);
        guideComment = findViewById(R.id.guide_comment);
        guideImage = findViewById(R.id.guide_image);
        backButton = findViewById(R.id.back_button);
        cameraButton = findViewById(R.id.camera_button);

        // Initially disable camera button
        cameraButton.setEnabled(false);
        cameraButton.setAlpha(0.5f);
    }

    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ProximityManager proximityManager = new ProximityManager();
        proximityManager.setNarrativeListener(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null && currentLandmark != null) {
                    checkProximityAndUpdateUI(locationResult.getLastLocation(), currentLandmark);
                }
            }
        };
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        cameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void handleIntentData() {
        PointOfInterest poi = (PointOfInterest) getIntent().getSerializableExtra("poi");
        guideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        if (poi != null) {
            landmarkName = poi.getName();
            currentLandmark = DatabaseHelper.getLandmarkByName(this, landmarkName);
            if (currentLandmark != null) {
                populatePOIViews(poi);
            } else {
                poiDetailedDescription.setText("Landmark details not found in database.");
            }
        } else {
            poiDetailedDescription.setText("Details not available.");
        }

        if (guideItem != null) {
            populateGuideViews(guideItem);
        }
    }

    // ------------------------ Location and Proximity Methods ------------------------

    private void checkProximityAndUpdateUI(Location userLocation, Landmark landmark) {
        float[] results = new float[1];
        Location.distanceBetween(
                userLocation.getLatitude(), userLocation.getLongitude(),
                landmark.getLatitude(), landmark.getLongitude(),
                results
        );

        currentDistance = results[0];  // Store current distance
        boolean isNearLandmark = currentDistance <= ALLOWED_DISTANCE_METERS;
        updateCameraButtonState(isNearLandmark);
    }

    private void updateCameraButtonState(boolean enabled) {
        runOnUiThread(() -> {
            cameraButton.setEnabled(enabled);
            cameraButton.setAlpha(enabled ? 1.0f : 0.5f);
        });
    }

    // ------------------------ UI Population Methods ------------------------

    private void populatePOIViews(PointOfInterest poi) {
        poiTitle.setText(poi.getName() != null ? poi.getName() : "Unknown");
        poiDetailedDescription.setText(poi.getFullDescription() != null ?
                poi.getFullDescription() : "No detailed description available.");
        poiDistance.setText(String.format(Locale.getDefault(), "%.1f km", poi.getDistance()));

        if (poi.getImageUrl() != null && !poi.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(poi.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_landmark_placeholder)
                    .error(R.drawable.ic_landmark_placeholder)
                    .into(poiImage);
        } else {
            poiImage.setImageResource(R.drawable.ic_landmark_placeholder);
        }

// Fetch narrativeText dynamically
        new Thread(() -> {
            Log.d(TAG, "Fetching narrative text for guideId: " + guideItem.getId() + " and poiId: " + poi.getId());

            String narrativeText = DatabaseHelper.getNarrativeForGuideAndLandmark(
                    this,
                    guideItem.getId(),
                    poi.getId()
            );

            if (narrativeText != null) {
                Log.d(TAG, "Narrative text fetched: " + narrativeText);
            } else {
                Log.d(TAG, "Narrative text not found or is null.");
            }

            // Update the UI on the main thread
            runOnUiThread(() -> {
                if (narrativeText != null && !narrativeText.isEmpty()) {
                    Log.d(TAG, "Updating guideComment with narrative text.");
                    guideComment.setText(narrativeText); // Set the narrative text
                } else {
                    Log.d(TAG, "Setting guideComment to fallback message: 'No guide narrative available.'");
                    guideComment.setText("No guide narrative available.");
                }
            });
        }).start();
    }

    private void populateGuideViews(GuideItem guideItem) {
        if (guideItem.getImageUrl() != null && !guideItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(guideItem.getImageUrl())
                    .centerCrop()
                    .into(guideImage);
        }
    }

    // ------------------------ Camera and Photo Methods ------------------------

    private void handleCameraClick() {
        if (!cameraButton.isEnabled()) {
            // Show distance message only when user tries to take photo while too far
            String message = String.format(Locale.getDefault(),
                    "You need to be within %d meters of %s to take photos. Current distance: %.0f meters",
                    (int) ALLOWED_DISTANCE_METERS,
                    landmarkName,
                    currentDistance);
            Snackbar.make(cameraButton, message, Snackbar.LENGTH_SHORT).show();
            return;
        }

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ridwan.tales_of_dd.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = landmarkName + "_" + timeStamp;

        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), landmarkName);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName + ".jpg");
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // ------------------------ Lifecycle Methods ------------------------

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager.startLocationUpdates(this, fusedLocationClient, locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.stopLocationUpdates(fusedLocationClient, locationCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo saved to collection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNarrativeTriggered(Landmark landmark) {
        // Implement if needed
    }
}