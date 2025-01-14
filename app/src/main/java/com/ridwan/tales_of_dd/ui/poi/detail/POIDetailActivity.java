package com.ridwan.tales_of_dd.ui.poi.detail;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;

import java.util.Locale;

public class POIDetailActivity extends AppCompatActivity {

    private ImageView poiImage;
    private TextView poiTitle;
    private TextView poiDetailedDescription;
    private TextView poiDistance;
    private TextView guideComment;
    private ImageView guideImage;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        initializeViews();
        setupClickListeners();

        // Get the PointOfInterest and GuideItem objects from the intent
        PointOfInterest poi = (PointOfInterest) getIntent().getSerializableExtra("poi");
        GuideItem guideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        // Populate views with POI details if the POI object is not null
        if (poi != null) {
            populatePOIViews(poi);
        } else {
            poiDetailedDescription.setText("Details not available.");
        }

        // Populate guide's details if the GuideItem object is not null
        if (guideItem != null) {
            populateGuideViews(guideItem);
        }
    }

    private void initializeViews() {
        poiImage = findViewById(R.id.poi_image);
        poiTitle = findViewById(R.id.poi_title);
        poiDetailedDescription = findViewById(R.id.poi_detailed_description);
        poiDistance = findViewById(R.id.poi_distance);
        guideComment = findViewById(R.id.guide_comment);
        guideImage = findViewById(R.id.guide_image);
        backButton = findViewById(R.id.back_button);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void populatePOIViews(PointOfInterest poi) {
        // Set the title
        poiTitle.setText(poi.getName() != null ? poi.getName() : "Unknown");

        // Set the detailed description
        poiDetailedDescription.setText(
                poi.getFullDescription() != null ? poi.getFullDescription() : "No detailed description available."
        );

        // Set the guide's narrative
        guideComment.setText(
                poi.getDescription() != null ? poi.getDescription() : "No guide narrative available."
        );

        // Set the distance
        poiDistance.setText(String.format(Locale.getDefault(), "%.1f km", poi.getDistance()));

        // Load the image using Glide
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
    }

    private void populateGuideViews(GuideItem guideItem) {
        // Load the guide's image using Glide
        if (guideItem.getImageUrl() != null && !guideItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(guideItem.getImageUrl())
                    .centerCrop()
                    .into(guideImage);
        }
    }
}