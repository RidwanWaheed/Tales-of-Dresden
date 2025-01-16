package com.ridwan.tales_of_dd.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.photo.PhotoViewerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment {
    private RecyclerView collectionsGrid;
    private CollectionAdapter adapter;
    private SearchView searchView;
    private List<Collection> collections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        initializeViews(view);
        setupCollectionsGrid();
        setupSearchView();

        return view;
    }

    private void initializeViews(View view) {
        collectionsGrid = view.findViewById(R.id.collections_grid);
        searchView = view.findViewById(R.id.search_view);
    }

    private void setupCollectionsGrid() {
        collections = new ArrayList<>();
        loadCollectionsFromStorage();

        adapter = new CollectionAdapter(collections, (photoPath, landmarkName) -> {
            showFullScreenPhoto(photoPath, landmarkName);
        });

        collectionsGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        collectionsGrid.setAdapter(adapter);
    }

    private void showFullScreenPhoto(String photoPath, String landmarkName) {
        Intent intent = new Intent(requireContext(), PhotoViewerActivity.class);
        // or if you're in an activity: new Intent(this, PhotoViewerActivity.class)

        intent.putExtra("photo_path", photoPath);
        intent.putExtra("landmark_name", landmarkName);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loadCollectionsFromStorage() {
        collections.clear();
        File picturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
    public void onResume() {
        super.onResume();
        // Refresh collections when returning to the fragment
        loadCollectionsFromStorage();
        adapter.notifyDataSetChanged();
    }
}