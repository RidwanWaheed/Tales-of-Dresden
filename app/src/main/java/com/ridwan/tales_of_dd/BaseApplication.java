package com.ridwan.tales_of_dd;

import android.app.Application;

import com.ridwan.tales_of_dd.utils.GuidePreferences;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Clear guide selection when app starts
        GuidePreferences.clearGuideSelection(this);
    }
}
