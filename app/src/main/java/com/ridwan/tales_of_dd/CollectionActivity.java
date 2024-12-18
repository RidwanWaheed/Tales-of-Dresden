package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView collectionsGrid;
    private CollectionAdapter adapter;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;

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
        List<Collection> collections = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            collections.add(new Collection("collection", ""));
        }

        adapter = new CollectionAdapter(collections);
        collectionsGrid.setLayoutManager(new GridLayoutManager(this, 2));
        collectionsGrid.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_collection);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_guide) {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_map) {
                // Handle map navigation
                return true;
            } else if (itemId == R.id.navigation_collection) {
                return true;
            } else if (itemId == R.id.navigation_about) {
                startActivity(new Intent(this, AboutActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Implement search functionality
                return false;
            }
        });
    }
}