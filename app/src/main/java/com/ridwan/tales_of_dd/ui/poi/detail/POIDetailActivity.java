package com.ridwan.tales_of_dd.ui.poi.detail;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.models.PointOfInterest;

import java.util.Locale;

public class POIDetailActivity extends AppCompatActivity {

    private ImageView poiImage;
    private TextView poiTitle;
    private TextView poiDescription;
    private TextView poiDistance;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        initializeViews();
        setupClickListeners();

        // Get POI data from intent
        PointOfInterest poi = (PointOfInterest) getIntent().getSerializableExtra("poi");
        if (poi != null) {
            populateViews(poi);
        }
    }

    private void initializeViews() {
        poiImage = findViewById(R.id.poi_image);
        poiTitle = findViewById(R.id.poi_title);
        poiDescription = findViewById(R.id.poi_description);
        poiDistance = findViewById(R.id.poi_distance);
        backButton = findViewById(R.id.back_button);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void populateViews(PointOfInterest poi) {
        poiTitle.setText(poi.getName());
        poiDescription.setText(poi.getDescription());
        poiDistance.setText(String.format(Locale.getDefault(), "%.1fkm", poi.getDistance()));

        // Load image using Glide
        Glide.with(this)
                .load(poi.getImageUrl())
                .centerCrop()
                .into(poiImage);
    }
}