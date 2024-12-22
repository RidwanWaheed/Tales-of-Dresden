package com.ridwan.tales_of_dd.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.ui.character.detail.AugustusDetailActivity;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.about.AboutActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements GuideAdapter.OnGuideItemClickListener {

    private RecyclerView guideRecycler;
    private GuideAdapter guideAdapter;
    private List<GuideItem> guideItems;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Initialize views
        initializeViews();
        setupGuideItems();
        setupRecyclerView();
        setupSearchView();
        setupBottomNavigation();
    }

    private void initializeViews() {
        guideRecycler = findViewById(R.id.guide_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupGuideItems() {
        guideItems = new ArrayList<>();
        guideItems.add(new GuideItem(
                "Augustus I",
                "Introduction",
                "https://upload.wikimedia.org/wikipedia/commons/3/3a/Bust_of_augustus.jpg"
        ));
        guideItems.add(new GuideItem(
                "Augustus II",
                "Introduction",
                "https://upload.wikimedia.org/wikipedia/commons/3/3a/Bust_of_augustus.jpg"
        ));
        guideItems.add(new GuideItem(
                "Augustus III",
                "Introduction",
                "https://upload.wikimedia.org/wikipedia/commons/3/3a/Bust_of_augustus.jpg"
        ));
    }

    private void setupRecyclerView() {
        guideRecycler.setLayoutManager(new LinearLayoutManager(this));
        guideAdapter = new GuideAdapter(guideItems, this);
        guideRecycler.setAdapter(guideAdapter);
    }

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

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_guide);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_guide) {
                return true;
            } else if (itemId == R.id.navigation_map) {
                // Handle map navigation
                return true;
            } else if (itemId == R.id.navigation_collection) {
                Intent intent = new Intent(this, CollectionActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_about) {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onGuideItemClicked(GuideItem guideItem) {
        Intent intent = new Intent(this, AugustusDetailActivity.class);
        intent.putExtra("guide_item", guideItem);
        startActivity(intent);
    }
}