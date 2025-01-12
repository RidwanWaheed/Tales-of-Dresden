package com.ridwan.tales_of_dd.ui.character.detail;

public class LandmarkItem {
    private final int id;
    private final String name;
    private final String imageUrl;
    private final double latitude;
    private final double longitude;

    public LandmarkItem(int id, String name, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
