package com.ridwan.tales_of_dd.ui.about;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.ui.collection.CollectionActivity;
import com.ridwan.tales_of_dd.ui.guide.GuideActivity;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.team.TeamMember;
import com.ridwan.tales_of_dd.ui.team.TeamMemberAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    private RecyclerView teamMembersRecycler;
    private TeamMemberAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        teamMembersRecycler = findViewById(R.id.team_recycler_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupRecyclerView();
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(new TeamMember("Marcel Siedlich", "url_to_marcel_image"));
        teamMembers.add(new TeamMember("Waheed Ridwan", "url_to_waheed_image"));
        teamMembers.add(new TeamMember("Keying Fan", "url_to_keying_image"));

        adapter = new TeamMemberAdapter(teamMembers);
        teamMembersRecycler.setLayoutManager(new LinearLayoutManager(this));
        teamMembersRecycler.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_about);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_guide) {
                Intent intent = new Intent(this, GuideActivity.class);
                startActivity(intent);
                finish();
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
                return true;
            }
            return false;
        });
    }



}