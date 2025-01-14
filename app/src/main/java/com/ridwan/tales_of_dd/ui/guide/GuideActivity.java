package com.ridwan.tales_of_dd.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Character;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;
import com.ridwan.tales_of_dd.ui.character.detail.GuideDetailActivity;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of guides and handle user interactions.
 */
public class GuideActivity extends AppCompatActivity implements GuideAdapter.OnGuideItemClickListener {

    private RecyclerView guideRecycler;
    private GuideAdapter guideAdapter;
    private List<GuideItem> guideItems = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Initialize database and views
        db = AppDatabase.getInstance(this);
        initializeViews();

        // Load data and set up UI components
        loadGuideItemsFromDatabase();
        setupRecyclerView();
        setupSearchView();
        setupBottomNavigation();
    }

    /**
     * Initializes views in the layout.
     */
    private void initializeViews() {
        guideRecycler = findViewById(R.id.guide_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    /**
     * Loads guide items from the database and updates the RecyclerView.
     */
    private void loadGuideItemsFromDatabase() {
        new Thread(() -> {
            // Fetch characters from the database
            List<Character> characters = db.characterDao().getAllCharacters();

            // Convert characters to guide items
            guideItems.clear();
            for (Character character : characters) {
                guideItems.add(new GuideItem(
                        character.id,
                        character.name,
                        character.personality,
                        character.imageUrl
                ));
            }

            // Update RecyclerView on the main thread
            runOnUiThread(() -> {
                if (guideAdapter != null) {
                    guideAdapter.updateItems(guideItems);
                } else {
                    setupRecyclerView();
                }
            });
        }).start();
    }

    /**
     * Sets up the RecyclerView with a layout manager and adapter.
     */
    private void setupRecyclerView() {
        guideRecycler.setLayoutManager(new LinearLayoutManager(this));
        guideAdapter = new GuideAdapter(guideItems, this);
        guideRecycler.setAdapter(guideAdapter);
    }

    /**
     * Sets up the search functionality to filter guide items.
     */
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                guideAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                guideAdapter.filter(newText);
                return false;
            }
        });
    }

    /**
     * Sets up the bottom navigation bar with listeners for navigation events.
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_guide);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_guide) {
                // Already on the guide screen
                return true;
            } else if (itemId == R.id.navigation_map) {
                // Handle map navigation
                return true;
            } else if (itemId == R.id.navigation_collection) {
                navigateToActivity(CollectionActivity.class);
                return true;
            } else if (itemId == R.id.navigation_about) {
                navigateToActivity(AboutActivity.class);
                return true;
            }
            return false;
        });
    }

    /**
     * Handles navigation to a different activity.
     *
     * @param targetActivity The target activity class.
     */
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    /**
     * Handles click events on guide items.
     *
     * @param guideItem The clicked guide item.
     */
    @Override
    public void onGuideItemClicked(GuideItem guideItem) {
        Intent intent = new Intent(this, GuideDetailActivity.class);
        intent.putExtra("guide_item", guideItem);
        startActivity(intent);
    }
}