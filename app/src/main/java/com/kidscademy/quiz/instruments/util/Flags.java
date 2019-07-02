package com.kidscademy.quiz.instruments.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kidscademy.quiz.instruments.App;

/**
 * Application global state and flags.
 *
 * @author Iulian Rotaru
 */
public final class Flags {
    private static final String FLAGS_SCORE_ORDR_BY = "flags.order.by";
    private static final String FLAGS_SPEAKER_NOTES = "flags.speaker.notes";
    private static final String FLAGS_CURRENT_LEVEL = "flags.current.level";
    private static final String FLAGS_SYNC_TIMESTAMP = "flags.sync.timestamp";

    public static void setSyncTimestamp() {
        long timestamp = System.currentTimeMillis() + 0;
        putLong(FLAGS_SYNC_TIMESTAMP, timestamp);
    }

    public static long getSyncTimestamp() {
        // if sync timestamp is not set used maximum future value in order to avoid syncing before storage load
        return getLong(FLAGS_SYNC_TIMESTAMP, Long.MAX_VALUE);
    }

    public static void setScoreOrderBy(int position) {
        putInt(FLAGS_SCORE_ORDR_BY, position);
    }

    public static int getScoreOrderBy() {
        return getInt(FLAGS_SCORE_ORDR_BY);
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


    protected static boolean getBoolean(String key) {
        return store().getBoolean(key, true);
    }

    protected static boolean getBoolean(String key, boolean defaultValue) {
        return store().getBoolean(key, defaultValue);
    }

    protected static void putBoolean(String key, boolean value) {
        editor().putBoolean(key, value).commit();
    }

    protected static void putInt(String key, int value) {
        editor().putInt(key, value).commit();
    }

    protected static int getInt(String key) {
        return store().getInt(key, 0);
    }


    protected static void putLong(String key, long value) {
        editor().putLong(key, value).commit();
    }

    protected static long getLong(String key) {
        return store().getLong(key, 0);
    }

    protected static long getLong(String key, long defaultValue) {
        return store().getLong(key, defaultValue);
    }

    private static SharedPreferences store() {
        return PreferenceManager.getDefaultSharedPreferences(App.context());
    }

    private static SharedPreferences.Editor editor() {
        return store().edit();
    }
}
