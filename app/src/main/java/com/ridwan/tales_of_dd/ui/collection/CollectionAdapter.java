package com.ridwan.tales_of_dd.ui.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    // Interface for handling photo clicks
    public interface OnPhotoClickListener {
        void onPhotoClick(String photoPath, String landmarkName);
    }

    private List<Collection> collections;
    private final OnPhotoClickListener photoClickListener;

    // Constructor
    public CollectionAdapter(List<Collection> collections, OnPhotoClickListener listener) {
        this.collections = collections;
        this.photoClickListener = listener;
    }

    // Collection ViewHolder Methods
    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collections.get(position);
        holder.bind(collection, photoClickListener);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public void updateCollections(List<Collection> newCollections) {
        this.collections = newCollections;
        notifyDataSetChanged();
    }

    // Collection ViewHolder Class
    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView collectionName;
        private final RecyclerView photosRecyclerView;
        private final TextView noPhotosText;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            collectionName = itemView.findViewById(R.id.collection_name);
            photosRecyclerView = itemView.findViewById(R.id.photos_recycler);
            noPhotosText = itemView.findViewById(R.id.no_photos_text);

            // Setup RecyclerView with GridLayoutManager
            photosRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
        }

        public void bind(Collection collection, OnPhotoClickListener listener) {
            collectionName.setText(collection.getName());

            List<String> photos = collection.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                photosRecyclerView.setVisibility(View.VISIBLE);
                noPhotosText.setVisibility(View.GONE);
                setupPhotosAdapter(photos, collection.getName(), listener);
            } else {
                photosRecyclerView.setVisibility(View.GONE);
                noPhotosText.setVisibility(View.VISIBLE);
            }
        }

        private void setupPhotosAdapter(List<String> photos, String landmarkName, OnPhotoClickListener listener) {
            PhotoAdapter photoAdapter = new PhotoAdapter(photos, landmarkName, listener);
            photosRecyclerView.setAdapter(photoAdapter);
        }
    }

    // Photo Adapter Class
    private static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private final List<String> photos;
        private final String landmarkName;
        private final OnPhotoClickListener listener;

        PhotoAdapter(List<String> photos, String landmarkName, OnPhotoClickListener listener) {
            this.photos = photos;
            this.landmarkName = landmarkName;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            String photoPath = photos.get(position);
            holder.bind(photoPath, landmarkName, listener);
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        // Photo ViewHolder Class
        static class PhotoViewHolder extends RecyclerView.ViewHolder {
            private final ImageView photoImage;

            PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                photoImage = itemView.findViewById(R.id.photo_image);
            }

            void bind(String photoPath, String landmarkName, OnPhotoClickListener listener) {
                // Load image using Glide
                Glide.with(itemView.getContext())
                        .load(photoPath)
                        .centerCrop()
                        .placeholder(R.drawable.ic_camera_placeholder)
                        .error(R.drawable.ic_camera_placeholder)
                        .into(photoImage);

                // Setup click listener
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPhotoClick(photoPath, landmarkName);
                    }
                });
            }
        }
    }
}