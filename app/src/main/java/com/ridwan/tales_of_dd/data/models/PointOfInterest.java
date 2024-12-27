package com.ridwan.tales_of_dd.data.models;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    private String name;
    private String description;
    private String imageUrl;
    private double distance;
    private String fullDescription;
    private double latitude;    // Added field
    private double longitude;   // Added field
    private int id;            // Added field

    // Main constructor used by POIDetailActivity
    public PointOfInterest(String name, String description, String imageUrl,
                           double distance, String fullDescription) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.distance = distance;
        this.fullDescription = fullDescription;
    }

    // New constructor for creating from Landmark
    public PointOfInterest(int id, String name, String description,
                           double latitude, double longitude, String imageUrl,
                           double distance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.distance = distance;
        this.fullDescription = description; // Using description as fullDescription
    }

    // Original getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getDistance() { return distance; }
    public String getFullDescription() { return fullDescription; }

    // New getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getId() { return id; }
}