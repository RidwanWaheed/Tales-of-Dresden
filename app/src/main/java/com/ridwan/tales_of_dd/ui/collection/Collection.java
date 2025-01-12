package com.ridwan.tales_of_dd.ui.collection;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private String name;
    private String imageUrl;
    private List<String> photos;

    public Collection(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.photos = new ArrayList<>();  // Initialize the photos list
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void addPhoto(String photoPath) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(photoPath);
    }
}