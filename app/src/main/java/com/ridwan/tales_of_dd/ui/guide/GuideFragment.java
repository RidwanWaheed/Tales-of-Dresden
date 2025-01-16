package com.ridwan.tales_of_dd.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Character;
import com.ridwan.tales_of_dd.ui.character.detail.GuideDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment implements GuideAdapter.OnGuideItemClickListener {
    private RecyclerView guideRecycler;
    private GuideAdapter guideAdapter;
    private final List<GuideItem> guideItems = new ArrayList<>();
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        // Initialize database
        db = AppDatabase.getInstance(requireContext());

        // Initialize views and setup
        initializeViews(view);
        loadGuideItemsFromDatabase();
        setupRecyclerView();
        setupSearchView(view);

        return view;
    }

    private void initializeViews(View view) {
        guideRecycler = view.findViewById(R.id.guide_recycler);
    }

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
            requireActivity().runOnUiThread(() -> {
                if (guideAdapter != null) {
                    guideAdapter.updateItems(guideItems);
                } else {
                    setupRecyclerView();
                }
            });
        }).start();
    }

    private void setupRecyclerView() {
        guideRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        guideAdapter = new GuideAdapter(guideItems, this);
        guideRecycler.setAdapter(guideAdapter);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search_view);
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
        Intent intent = new Intent(requireContext(), GuideDetailActivity.class);
        intent.putExtra("guide_item", guideItem);
        startActivity(intent);
        // Add transition animation
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}