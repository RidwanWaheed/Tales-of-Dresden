package com.ridwan.tales_of_dd;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AugustusDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView introductionTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augustus_detail);

        // Initialize views
        titleTextView = findViewById(R.id.detail_title);
        introductionTextView = findViewById(R.id.detail_introduction);
        imageView = findViewById(R.id.detail_image);

        // Get the GuideItem from the intent
        GuideItem guideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        if (guideItem != null) {
            // Set the data to views
            titleTextView.setText(guideItem.getTitle());
            introductionTextView.setText(guideItem.getIntroduction());

            // Load image using Glide
            if (!TextUtils.isEmpty(guideItem.getImageUrl())) {
                Glide.with(this)
                        .load(guideItem.getImageUrl())
                        .centerCrop()
                        .into(imageView);
            }
        }

    }
}