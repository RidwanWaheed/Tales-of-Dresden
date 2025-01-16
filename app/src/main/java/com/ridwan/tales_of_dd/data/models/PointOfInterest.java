package com.ridwan.tales_of_dd.data.models;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    private final String name;
    private final String description;
    private final String imageUrl;
    private final double distance;
    private final String fullDescription;
    private final int id;

    // Main constructor used by POIDetailActivity
    public PointOfInterest(int id, String name, String description,
                           double latitude, double longitude, String imageUrl,
                           double distance, String fullDescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.distance = distance;
        this.fullDescription = fullDescription;
    }

    // getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getDistance() { return distance; }
    public String getFullDescription() { return fullDescription; }

    public int getId() { return id; }
}