package com.ridwan.tales_of_dd;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideViewHolder> {
    private List<GuideItem> allGuideItems;
    private List<GuideItem> filteredGuideItems;
    private final OnGuideItemClickListener listener;

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
        GuideItem item = filteredGuideItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return filteredGuideItems.size();
    }

    public void filter(String query) {
        query = query.toLowerCase().trim();
        filteredGuideItems.clear();

        if (query.isEmpty()) {
            filteredGuideItems.addAll(allGuideItems);
        } else {
            for (GuideItem item : allGuideItems) {
                if (item.getTitle().toLowerCase().contains(query) ||
                        item.getIntroduction().toLowerCase().contains(query)) {
                    filteredGuideItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnGuideItemClickListener {
        void onGuideItemClicked(GuideItem guideItem);
    }

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

        public void bind(GuideItem item, OnGuideItemClickListener listener) {
            titleView.setText(item.getTitle());
            introView.setText(item.getIntroduction());

            // Load image with Glide
            if (!TextUtils.isEmpty(item.getImageUrl())) {
                Glide.with(imageView.getContext())
                        .load(item.getImageUrl())
                        .centerCrop()
                        .into(imageView);
            }

            // Set click listener on the entire itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGuideItemClicked(item);
                }
            });
        }
    }
}