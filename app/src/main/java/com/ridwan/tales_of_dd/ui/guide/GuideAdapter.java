package com.ridwan.tales_of_dd.ui.guide;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ridwan.tales_of_dd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying guide items in a RecyclerView.
 */
public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideViewHolder> {

    private List<GuideItem> allGuideItems;
    private List<GuideItem> filteredGuideItems;
    private final OnGuideItemClickListener listener;

    /**
     * Constructor for GuideAdapter.
     *
     * @param guideItems List of all guide items.
     * @param listener   Listener for handling item click events.
     */
    public GuideAdapter(List<GuideItem> guideItems, OnGuideItemClickListener listener) {
        this.allGuideItems = guideItems;
        this.filteredGuideItems = new ArrayList<>(guideItems);
        this.listener = listener;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guide_item, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        holder.bind(filteredGuideItems.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return filteredGuideItems.size();
    }

    /**
     * Updates the list of guide items and refreshes the RecyclerView.
     *
     * @param newItems New list of guide items.
     */
    public void updateItems(List<GuideItem> newItems) {
        this.allGuideItems = newItems;
        this.filteredGuideItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    /**
     * Filters the guide items based on a search query.
     *
     * @param query The search query.
     */
    public void filter(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();
        filteredGuideItems.clear();

        if (lowerCaseQuery.isEmpty()) {
            filteredGuideItems.addAll(allGuideItems);
        } else {
            for (GuideItem item : allGuideItems) {
                if (item.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        item.getIntroduction().toLowerCase().contains(lowerCaseQuery)) {
                    filteredGuideItems.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Listener interface for handling guide item click events.
     */
    public interface OnGuideItemClickListener {
        void onGuideItemClicked(GuideItem guideItem);
    }

    /**
     * ViewHolder class for guide items.
     */
    static class GuideViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView introView;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.guide_image);
            titleView = itemView.findViewById(R.id.guide_title);
            introView = itemView.findViewById(R.id.guide_introduction);
        }

        /**
         * Binds a GuideItem to the ViewHolder.
         *
         * @param item     The guide item to display.
         * @param listener Listener for handling item click events.
         */
        public void bind(GuideItem item, OnGuideItemClickListener listener) {
            titleView.setText(item.getTitle());
            introView.setText(item.getIntroduction());

            // Load image using Glide
            loadImage(item.getImageUrl());

            // Handle item click events
            itemView.setOnClickListener(v -> listener.onGuideItemClicked(item));
        }

        /**
         * Loads an image into the ImageView using Glide.
         *
         * @param imageUrl The URL of the image.
         */
        private void loadImage(String imageUrl) {
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(imageView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_guide)  // Placeholder image
                        .error(R.drawable.ic_guide)       // Fallback image on error
                        .centerCrop()
                        .listener(new GlideLogListener(imageUrl))
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_guide);
            }
        }
    }

    /**
     * Glide listener for logging image loading events.
     */
    static class GlideLogListener implements RequestListener<Drawable> {

        private final String imageUrl;

        public GlideLogListener(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Log.e("GuideAdapter", "Error loading image: " + imageUrl, e);
            return false; // Allow Glide to handle fallback logic
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            Log.d("GuideAdapter", "Successfully loaded image: " + imageUrl);
            return false; // Allow Glide to continue processing
        }
    }
}