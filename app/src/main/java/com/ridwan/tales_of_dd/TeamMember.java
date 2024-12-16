package com.ridwan.tales_of_dd;

public class TeamMember {
    private String name;
    private String imageUrl;

    public TeamMember(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
