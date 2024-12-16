package com.ridwan.tales_of_dd;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class AugustusDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView briefIntroView;
    private TextView overviewText;
    private ImageView imageView;
    private RecyclerView landmarksRecycler;
    private ImageButton backButton;
    private MaterialButton guideMeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augustus_detail);

        // Initialize views
        initializeViews();
        setupClickListeners();
        setupLandmarksRecycler();

        // Get the GuideItem from the intent
        GuideItem guideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        if (guideItem != null) {
            populateViews(guideItem);
        }
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.detail_title);
        briefIntroView = findViewById(R.id.brief_intro);
        overviewText = findViewById(R.id.overview_text);
        imageView = findViewById(R.id.detail_image);
        landmarksRecycler = findViewById(R.id.landmarks_recycler);
        backButton = findViewById(R.id.back_button);
        guideMeButton = findViewById(R.id.guide_me_button);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        guideMeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Guide feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupLandmarksRecycler() {
        landmarksRecycler.setLayoutManager(
                new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        // TODO: Set up landmarks adapter once implemented
    }

    private void populateViews(GuideItem guideItem) {
        titleTextView.setText(guideItem.getTitle());
        briefIntroView.setText("brief introduction");
        overviewText.setText("Frederick Augustus I (German: Friedrich August I.; " +
                "Polish: Fryderyk August I; French: Frédéric-Auguste Ier; 23 December 1750 – .... ");

        // Load image using Glide
        if (!TextUtils.isEmpty(guideItem.getImageUrl())) {
            Glide.with(this)
                    .load(guideItem.getImageUrl())
                    .centerCrop()
                    .into(imageView);
        }
    }

}