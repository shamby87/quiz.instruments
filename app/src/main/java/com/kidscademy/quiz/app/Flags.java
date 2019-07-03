package com.kidscademy.quiz.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Application global state and flags.
 *
 * @author Iulian Rotaru
 */
public final class Flags {
    private static final String FLAGS_SCORE_ORDER_BY = "flags.order.by";
    private static final String FLAGS_SPEAKER_NOTES = "flags.speaker.notes";
    private static final String FLAGS_CURRENT_LEVEL = "flags.current.level";

    public static void setScoreOrderBy(int position) {
        putInt(FLAGS_SCORE_ORDER_BY, position);
    }

    public static int getScoreOrderBy() {
        return getInt(FLAGS_SCORE_ORDER_BY);
    }

    public static void toggleSpeakerNotes() {
        putBoolean(FLAGS_SPEAKER_NOTES, !isSpeakerNotes());
    }

    public static boolean isSpeakerNotes() {
        return getBoolean(FLAGS_SPEAKER_NOTES);
    }

    public static void setCurrentLevel(int levelIndex) {
        putInt(FLAGS_CURRENT_LEVEL, levelIndex);
    }

    public static int getCurrentLevel() {
        return getInt(FLAGS_CURRENT_LEVEL);
    }


    private static boolean getBoolean(String key) {
        return store().getBoolean(key, true);
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        return store().getBoolean(key, defaultValue);
    }

    private static void putBoolean(String key, boolean value) {
        editor().putBoolean(key, value).commit();
    }

    private static void putInt(String key, int value) {
        editor().putInt(key, value).commit();
    }

    private static int getInt(String key) {
        return store().getInt(key, 0);
    }

    private static SharedPreferences store() {
        return PreferenceManager.getDefaultSharedPreferences(App.instance().getApplicationContext());
    }

    private static SharedPreferences.Editor editor() {
        return store().edit();
    }
}
