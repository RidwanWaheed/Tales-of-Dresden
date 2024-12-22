package com.ridwan.tales_of_dd.data.models;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    private String name;
    private String description;
    private String imageUrl;
    private double distance;
    private String fullDescription;

    public PointOfInterest(String name, String description, String imageUrl, double distance, String fullDescription) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.distance = distance;
        this.fullDescription = fullDescription;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getDistance() { return distance; }
    public String getFullDescription() { return fullDescription; }
}
