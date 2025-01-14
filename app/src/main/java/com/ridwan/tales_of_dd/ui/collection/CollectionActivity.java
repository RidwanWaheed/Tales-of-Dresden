package com.ridwan.tales_of_dd.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.map.MapActivity;
import com.ridwan.tales_of_dd.utils.GuidePreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView collectionsGrid;
    private CollectionAdapter adapter;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;
    private List<Collection> collections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        initializeViews();
        setupCollectionsGrid();
        setupBottomNavigation();
        setupSearchView();
    }

    private void initializeViews() {
        collectionsGrid = findViewById(R.id.collections_grid);
        searchView = findViewById(R.id.search_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupCollectionsGrid() {
        collections = new ArrayList<>();
        loadCollectionsFromStorage();

        adapter = new CollectionAdapter(collections, new CollectionAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(String photoPath, String landmarkName) {
                // Handle photo click here
                // For example, open a full-screen photo viewer
                showFullScreenPhoto(photoPath, landmarkName);
            }
        });

        collectionsGrid.setLayoutManager(new GridLayoutManager(this, 2));
        collectionsGrid.setAdapter(adapter);
    }

    // Optional method to handle photo viewing
    private void showFullScreenPhoto(String photoPath, String landmarkName) {
        // Implement photo viewer functionality
        // You could start a new activity here to show the photo in full screen
    }

    private void loadCollectionsFromStorage() {
        collections.clear();
        File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (picturesDir != null && picturesDir.exists()) {
            File[] landmarkFolders = picturesDir.listFiles();
            if (landmarkFolders != null) {
                for (File landmarkFolder : landmarkFolders) {
                    if (landmarkFolder.isDirectory()) {
                        String landmarkName = landmarkFolder.getName();
                        Collection collection = new Collection(landmarkName, "");

                        // Get all .jpg files from the landmark folder
                        File[] photos = landmarkFolder.listFiles((dir, name) ->
                                name.toLowerCase().endsWith(".jpg"));

                        if (photos != null) {
                            for (File photo : photos) {
                                collection.addPhoto(photo.getAbsolutePath());
                            }
                            if (photos.length > 0) {
                                collections.add(collection);
                            }
                        }
                    }
                }
            }
        }
    }

    // Navigation Methods
    private void setupBottomNavigation() {
        // Set current selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_collection);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_collection) {
                // Already on About page
                return true;
            }

            // Handle navigation based on selected item
            if (itemId == R.id.navigation_map) {
                handleMapNavigation();
            } else if (itemId == R.id.navigation_guide) {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
            } else if (itemId == R.id.navigation_about) {
                startActivity(new Intent(this, AboutActivity.class));
                finish();
            }

            return true;
        });
    }

    private void handleMapNavigation() {
        if (!GuidePreferences.isGuideSelected(this)) {
            // No guide selected, show message and redirect to guide selection
            Toast.makeText(this,
                    "Please select a guide to use the map",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {
            // Guide already selected, proceed to map
            GuideItem currentGuide = GuidePreferences.getCurrentGuide(this);
            Intent mapIntent = new Intent(this, MapActivity.class);
            mapIntent.putExtra("guide_item", currentGuide);
            startActivity(mapIntent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // Optional: Handle back press if needed
        super.onBackPressed();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCollections(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCollections(newText);
                return true;
            }
        });
    }

    private void filterCollections(String query) {
        if (collections == null) return;

        List<Collection> filteredList = new ArrayList<>();
        for (Collection collection : collections) {
            if (collection.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(collection);
            }
        }
        adapter.updateCollections(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh collections when returning to the activity
        loadCollectionsFromStorage();
        adapter.notifyDataSetChanged();
    }
}