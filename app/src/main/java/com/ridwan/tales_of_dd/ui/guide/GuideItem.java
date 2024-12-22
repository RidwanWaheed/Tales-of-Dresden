package com.ridwan.tales_of_dd.ui.guide;

import java.io.Serializable;

public class GuideItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String introduction;
    private String imageUrl;

    public GuideItem(String title, String introduction) {
        this.title = title;
        this.introduction = introduction;
        this.imageUrl = "";
    }

    public GuideItem(String title, String introduction, String imageUrl) {
        this.title = title;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}