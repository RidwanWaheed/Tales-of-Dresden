package com.ridwan.tales_of_dd.ui.character.detail;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Character;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.map.MapActivity;
import com.ridwan.tales_of_dd.utils.LandmarkManager;

import java.util.List;

public class GuideDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView briefIntroView;
    private TextView overviewText;
    private ImageView imageView;
    private RecyclerView landmarksRecycler;
    private ImageButton backButton;
    private MaterialButton guideMeButton;
    private GuideItem currentGuideItem;
    private LandmarksAdapter landmarksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augustus_detail);

        // Initialize views
        initializeViews();

        // Get the GuideItem from the intent
        currentGuideItem = (GuideItem) getIntent().getSerializableExtra("guide_item");

        if (currentGuideItem != null) {
            populateViews(currentGuideItem);
            fetchOverviewFromDatabase(currentGuideItem.getId()); // Fetch and display overview dynamically
            fetchLandmarks(currentGuideItem.getId()); // Fetch landmarks dynamically
            setupClickListeners();
        } else {
            Toast.makeText(this, "Error: Guide item not found.", Toast.LENGTH_SHORT).show();
            finish();
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
            if (currentGuideItem != null) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("guide_item", currentGuideItem);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error loading guide information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateViews(GuideItem guideItem) {
        titleTextView.setText(guideItem.getTitle());
        briefIntroView.setText(guideItem.getIntroduction());

        // Load image using Glide
        if (!TextUtils.isEmpty(guideItem.getImageUrl())) {
            Glide.with(this)
                    .load(guideItem.getImageUrl())
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void fetchOverviewFromDatabase(int characterId) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Character character = db.characterDao().getCharacterById(characterId);

            runOnUiThread(() -> {
                if (character != null && !TextUtils.isEmpty(character.overview)) {
                    overviewText.setText(character.overview);
                } else {
                    overviewText.setText("Overview not available.");
                }
            });
        }).start();
    }

    private void fetchLandmarks(int characterId) {
        LandmarkManager.fetchLandmarksByCharacterId(this, characterId, new LandmarkManager.LandmarkFetchCallback() {
            @Override
            public void onLandmarksFetched(List<Landmark> fetchedLandmarks) {
                if (fetchedLandmarks != null && !fetchedLandmarks.isEmpty()) {
                    setupLandmarksRecycler(fetchedLandmarks);
                } else {
                    Toast.makeText(GuideDetailActivity.this, "No landmarks associated with this character.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                Toast.makeText(GuideDetailActivity.this, "Failed to fetch landmarks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLandmarksRecycler(List<Landmark> landmarks) {
        landmarksRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // Convert Landmark objects to LandmarkItem objects
        List<LandmarkItem> landmarkItems = LandmarkManager.convertToLandmarkItems(landmarks);

        // Set the adapter
        LandmarksAdapter landmarksAdapter = new LandmarksAdapter(landmarkItems, null);
        landmarksRecycler.setAdapter(landmarksAdapter);

        // Add spacing between items
        int spacing = getResources().getDimensionPixelSize(R.dimen.landmark_item_spacing);
        landmarksRecycler.addItemDecoration(new SpacesItemDecoration(spacing));

        // Add SnapHelper for snapping behavior
        SnapHelper snapHelper = new LinearSnapHelper(); // or PagerSnapHelper for pager-like behavior
        snapHelper.attachToRecyclerView(landmarksRecycler);
    }

}