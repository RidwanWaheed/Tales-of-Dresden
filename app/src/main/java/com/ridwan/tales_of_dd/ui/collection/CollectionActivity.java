package com.ridwan.tales_of_dd.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.map.MapActivity;

import java.io.File;
import java.io.FilenameFilter;
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

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_collection);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_guide) {
                intent = new Intent(this, GuideActivity.class);
            } else if (itemId == R.id.navigation_map) {
                intent = new Intent(this, MapActivity.class);
            } else if (itemId == R.id.navigation_collection) {
                return true; // Already on collection screen
            } else if (itemId == R.id.navigation_about) {
                intent = new Intent(this, AboutActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });
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