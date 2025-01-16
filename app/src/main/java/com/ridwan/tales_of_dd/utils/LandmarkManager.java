package com.ridwan.tales_of_dd.utils;

import android.content.Context;

import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.ui.character.detail.LandmarkItem;

import java.util.ArrayList;
import java.util.List;

public class   LandmarkManager {

    public interface LandmarkFetchCallback {
        void onLandmarksFetched(List<Landmark> landmarks);
        void onError(String message);
    }

    public static void fetchLandmarksByCharacterId(Context context, int characterId, LandmarkFetchCallback callback) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                List<LandmarkCharacter> landmarkCharacters = db.landmarkCharacterDao()
                        .getLandmarkCharactersByCharacterId(characterId);

                List<Integer> landmarkIds = new ArrayList<>();
                for (LandmarkCharacter relationship : landmarkCharacters) {
                    landmarkIds.add(relationship.landmarkId);
                }

                List<Landmark> landmarks = db.landmarkDao().getLandmarksByIds(landmarkIds);
                if (callback != null) {
                    callback.onLandmarksFetched(landmarks);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error fetching landmarks: " + e.getMessage());
                }
            }
        }).start();
    }

    public static List<LandmarkItem> convertToLandmarkItems(List<Landmark> landmarks) {
        List<LandmarkItem> landmarkItems = new ArrayList<>();
        for (Landmark landmark : landmarks) {
            landmarkItems.add(new LandmarkItem(
                    landmark.getId(),
                    landmark.getName(),
                    landmark.getImageUrl(),
                    landmark.getLatitude(),
                    landmark.getLongitude()
            ));
        }
        return landmarkItems;
    }
}