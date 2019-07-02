package com.kidscademy.quiz.instruments.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;

/**
 * Application preferences.
 *
 * @author Iulian Rotaru
 */
public final class Preferences {
    public boolean isSoundsEffects() {
        return getBoolean(App.context(), R.string.pref_sound_fx_key, true);
    }

    public void setSoundsEffects(boolean soundsEffects) {
        String key = string(App.context(), R.string.pref_sound_fx_key);
        App.audit().preferenceChanged(key, soundsEffects);
        editor(App.context()).putBoolean(key, soundsEffects).apply();
    }

    public boolean isKeyVibrator() {
        return getBoolean(App.context(), R.string.pref_key_vibrator_key, false);
    }


    protected boolean getBoolean(Context context, int keyStringId, boolean defaultValue) {
        return store(context).getBoolean(string(context, keyStringId), defaultValue);
    }

    protected SharedPreferences store(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected SharedPreferences.Editor editor(Context context) {
        return store(context).edit();
    }

    protected String string(Context context, int id) {
        return context.getResources().getString(id);
    }
}
