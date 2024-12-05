package com.example.studoc_clone.utils;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

public class GlobalUtils {

    public static void saveRecents(String id, List<String> recentIds, String KEY, int limit, Context context) {
        // Update the list of recent document IDs
        if (recentIds.contains(id)) {
            recentIds.remove(id);
        }
        recentIds.add(0, id); // Add to the top
        if (recentIds.size() > limit) {
            recentIds.remove(recentIds.size() - 1); // Keep only the last 15
        }

        // Save to SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(KEY, gson.toJson(recentIds));
        editor.apply();
    }

}
