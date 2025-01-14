package com.ridwan.tales_of_dd.ui.character.detail;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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

import java.util.List;

public class LandmarksAdapter extends RecyclerView.Adapter<LandmarksAdapter.LandmarkViewHolder> {
    private final List<LandmarkItem> landmarks;
    private final OnLandmarkClickListener listener;

    public interface OnLandmarkClickListener {
        void onLandmarkClick(LandmarkItem landmarkItem);
    }

    public LandmarksAdapter(List<LandmarkItem> landmarks, OnLandmarkClickListener listener) {
        this.landmarks = landmarks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.landmark_item, parent, false);
        return new LandmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkViewHolder holder, int position) {
        LandmarkItem item = landmarks.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return landmarks.size();
    }

    static class LandmarkViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameView;

        public LandmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.landmark_image);
            nameView = itemView.findViewById(R.id.landmark_name);
        }

        public void bind(LandmarkItem item, OnLandmarkClickListener listener) {
            nameView.setText(item.getName());

            // Load image with Glide
            if (!TextUtils.isEmpty(item.getImageUrl())) {
                Glide.with(imageView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_landmark_placeholder)
                        .error(R.drawable.ic_landmark_placeholder)
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_landmark_placeholder);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLandmarkClick(item);
                }
            });
        }
    }
}