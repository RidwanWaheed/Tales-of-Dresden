package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements GuideAdapter.OnGuideItemClickListener {

    private RecyclerView guideRecycler;
    private GuideAdapter guideAdapter;
    private List<GuideItem> guideItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Initialize the guide items
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

        // Set up the RecyclerView
        guideRecycler = findViewById(R.id.guide_recycler);
        guideRecycler.setLayoutManager(new LinearLayoutManager(this));
        guideAdapter = new GuideAdapter(guideItems, this);
        guideRecycler.setAdapter(guideAdapter);

        // Set up the search functionality
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

    @Override
    public void onGuideItemClicked(GuideItem guideItem) {
        Intent intent = new Intent(this, AugustusDetailActivity.class);
        intent.putExtra("guide_item", guideItem);
        startActivity(intent);
    }
}