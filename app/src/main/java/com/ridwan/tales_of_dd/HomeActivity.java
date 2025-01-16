package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ridwan.tales_of_dd.ui.about.AboutFragment;
import com.ridwan.tales_of_dd.ui.collection.CollectionFragment;
import com.ridwan.tales_of_dd.ui.guide.GuideFragment;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;
import com.ridwan.tales_of_dd.ui.map.MapFragment;
import com.ridwan.tales_of_dd.utils.GuidePreferences;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // If this is a fresh launch (not a configuration change)
        if (savedInstanceState == null) {
            // Handle incoming intent first
            handleIncomingIntent(getIntent());
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_map) {
                if (!GuidePreferences.isGuideSelected(this)) {
                    Toast.makeText(this, "Please select a guide to use the map", Toast.LENGTH_SHORT).show();
                    fragment = new GuideFragment();
                } else {
                    GuideItem currentGuide = GuidePreferences.getCurrentGuide(this);
                    fragment = MapFragment.newInstance(currentGuide);
                }
            } else if (itemId == R.id.navigation_guide) {
                fragment = new GuideFragment();
            } else if (itemId == R.id.navigation_collection) {
                fragment = new CollectionFragment();
            } else if (itemId == R.id.navigation_about) {
                fragment = new AboutFragment();
            }

            return loadFragment(fragment);
        });
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null) {
            String selectedTab = intent.getStringExtra("selected_tab");
            if ("map".equals(selectedTab)) {
                GuideItem guideItem = (GuideItem) intent.getSerializableExtra("guide_item");
                if (guideItem != null) {
                    // Slight delay to ensure proper initialization
                    new Handler().postDelayed(() -> {
                        MapFragment mapFragment = MapFragment.newInstance(guideItem);
                        loadFragment(mapFragment);
                        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
                    }, 100);
                }
            } else {
                loadFragment(new GuideFragment());
            }
        } else {
            loadFragment(new GuideFragment());
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                    )
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }
}