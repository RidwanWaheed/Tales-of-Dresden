package com.ridwan.tales_of_dd.ui.character.detail;

public class LandmarkItem {
    private final int id;
    private final String name;
    private final String imageUrl;

    public LandmarkItem(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
