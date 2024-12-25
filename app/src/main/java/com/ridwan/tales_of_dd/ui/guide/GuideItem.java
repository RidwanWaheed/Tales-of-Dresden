package com.ridwan.tales_of_dd.ui.guide;

import java.io.Serializable;

public class GuideItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private String introduction;
    private String imageUrl;

    public GuideItem(int id, String title, String introduction, String imageUrl) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getIntroduction() { return introduction; }
    public String getImageUrl() { return imageUrl; }
}