package com.ridwan.tales_of_dd.ui.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<String> photoPaths;

    public PhotoAdapter(List<String> photoPaths) {
        this.photoPaths = photoPaths;
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
        String photoPath = photoPaths.get(position);
        Glide.with(holder.itemView.getContext())
                .load(photoPath)
                .centerCrop()
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;  // Using the same variable name for consistency

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photo_image);
        }
    }
}
