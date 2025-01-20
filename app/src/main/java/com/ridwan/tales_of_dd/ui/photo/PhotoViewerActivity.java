package com.ridwan.tales_of_dd.ui.photo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;
import java.io.File;

public class PhotoViewerActivity extends AppCompatActivity {
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        // Get data from intent
        photoPath = getIntent().getStringExtra("photo_path");
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
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_viewer_menu, menu);
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create custom title view
        TextView titleView = new TextView(this);
        titleView.setText("Delete Photo");
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setPadding(60, 30, 60, 10);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        builder.setCustomTitle(titleView)
                .setMessage("Are you sure you want to delete this photo?")
                .setPositiveButton("Delete", (dialog, which) -> deletePhoto())
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            if (messageView != null) {
                messageView.setTextColor(Color.BLACK);
                messageView.setPadding(60, 10, 60, 30);
                messageView.setGravity(Gravity.CENTER_VERTICAL);
            }
        });

        dialog.show();
    }

    private void deletePhoto() {
        if (photoPath != null) {
            File photoFile = new File(photoPath);
            if (photoFile.delete()) {
                // Send back result to refresh the collection
                setResult(RESULT_OK);
                Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                Toast.makeText(this, "Failed to delete photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
