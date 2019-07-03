package com.kidscademy.quiz.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Audit;
import com.kidscademy.quiz.instruments.R;

/**
 * Application preferences.
 *
 * @author Iulian Rotaru
 */
public final class Preferences {
    private final Context context;
    private final Audit audit;

    public Preferences(Context context, Audit audit) {
        this.context = context;
        this.audit = audit;
    }

    public boolean isSoundsEffects() {
        return getBoolean(R.string.pref_sound_fx_key, true);
    }

    public void toggleSoundsEffects() {
        String key = string(R.string.pref_sound_fx_key);
        boolean soundsEffects = !isSoundsEffects();
        audit.preferenceChanged(key, soundsEffects);
        editor().putBoolean(key, soundsEffects).apply();
    }

    public boolean isKeyVibrator() {
        return getBoolean(R.string.pref_key_vibrator_key, false);
    }

    // ---------------------------------------------------------------------------------------------

    private boolean getBoolean(int keyStringId, boolean defaultValue) {
        return store().getBoolean(string(keyStringId), defaultValue);
    }

    private SharedPreferences store() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor editor() {
        return store().edit();
    }

    private String string(int id) {
        return context.getResources().getString(id);
    }
}
