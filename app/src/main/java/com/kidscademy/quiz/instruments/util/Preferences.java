package com.kidscademy.quiz.instruments.util;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.util.PreferencesBase;

public final class Preferences extends PreferencesBase {
    public boolean isSoundsEffects() {
        return getBoolean(App.context(), R.string.pref_sound_fx_key, true);
    }

    public void setSoundsEffects(boolean soundsEffects) {
        String key = string(App.context(), R.string.pref_sound_fx_key);
        editor(App.context()).putBoolean(key, soundsEffects).apply();
    }

    public boolean isKeyVibrator() {
        return getBoolean(App.context(), R.string.pref_key_vibrator_key, false);
    }
}
