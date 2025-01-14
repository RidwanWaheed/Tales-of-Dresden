package com.ridwan.tales_of_dd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.ridwan.tales_of_dd.ui.guide.GuideItem;

public class GuidePreferences {
    private static final String PREF_NAME = "guide_prefs";
    private static final String KEY_GUIDE_SELECTED = "guide_selected";
    private static final String KEY_CURRENT_GUIDE = "current_guide";

    public static void setGuideSelected(Context context, boolean selected, GuideItem guide) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_GUIDE_SELECTED, selected)
                .putString(KEY_CURRENT_GUIDE, guide != null ? new Gson().toJson(guide) : null)
                .apply();
    }

    public static boolean isGuideSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_GUIDE_SELECTED, false);
    }

    public static GuideItem getCurrentGuide(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String guideJson = prefs.getString(KEY_CURRENT_GUIDE, null);
        return guideJson != null ? new Gson().fromJson(guideJson, GuideItem.class) : null;
    }

    public static void clearGuideSelection(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_GUIDE_SELECTED)
                .remove(KEY_CURRENT_GUIDE)
                .apply();
    }
}