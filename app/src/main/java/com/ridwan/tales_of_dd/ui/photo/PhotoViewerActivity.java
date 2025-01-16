package com.ridwan.tales_of_dd.ui.photo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;
import java.io.File;

public class PhotoViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        // Get data from intent
        String photoPath = getIntent().getStringExtra("photo_path");
        String landmarkName = getIntent().getStringExtra("landmark_name");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(landmarkName);
        }

        // Setup image view
        ImageView photoView = findViewById(R.id.photo_view);
        if (photoPath != null) {
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                Glide.with(this)
                        .load(photoFile)
                        .into(photoView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}