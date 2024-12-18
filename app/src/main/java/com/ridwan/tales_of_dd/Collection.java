package com.ridwan.tales_of_dd;

public class Collection {
    private String name;
    private String imageUrl;

    public Collection(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
