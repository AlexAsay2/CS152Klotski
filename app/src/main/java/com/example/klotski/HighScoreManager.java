package com.example.klotski;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "highscores";
    private static final int DEFAULT_HIGH_SCORE = 0;

    private final SharedPreferences sharedPreferences;

    public HighScoreManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getHighScore(int levelNumber) {
        return sharedPreferences.getInt(getKey(levelNumber), DEFAULT_HIGH_SCORE);
    }

    public void setHighScore(int levelNumber, int score) {
        sharedPreferences.edit().putInt(getKey(levelNumber), score).apply();
    }

    private String getKey(int levelNumber) {
        return "highscore_level_" + levelNumber;
    }
}


