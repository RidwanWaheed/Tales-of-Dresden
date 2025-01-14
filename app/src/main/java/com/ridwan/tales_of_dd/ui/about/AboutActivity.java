package com.ridwan.tales_of_dd.ui.about;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.map.MapActivity;
import com.ridwan.tales_of_dd.ui.team.TeamMember;
import com.ridwan.tales_of_dd.ui.team.TeamMemberAdapter;
import com.ridwan.tales_of_dd.utils.GuidePreferences;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    // UI Components
    private RecyclerView teamMembersRecycler;
    private TeamMemberAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        setupRecyclerView();
        setupBottomNavigation();
    }

    private void initializeViews() {
        teamMembersRecycler = findViewById(R.id.team_recycler_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(new TeamMember("Waheed Ridwan", "url_to_waheed_image"));
        teamMembers.add(new TeamMember("Marcel Siedlich", "url_to_marcel_image"));
        teamMembers.add(new TeamMember("Keying Fan", "url_to_keying_image"));

        adapter = new TeamMemberAdapter(teamMembers);
        teamMembersRecycler.setLayoutManager(new LinearLayoutManager(this));
        teamMembersRecycler.setAdapter(adapter);
    }

    // Navigation Methods
    private void setupBottomNavigation() {
        // Set current selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_about);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_about) {
                // Already on About page
                return true;
            }

            // Handle navigation based on selected item
            if (itemId == R.id.navigation_map) {
                handleMapNavigation();
            } else if (itemId == R.id.navigation_guide) {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
            } else if (itemId == R.id.navigation_collection) {
                startActivity(new Intent(this, CollectionActivity.class));
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
}