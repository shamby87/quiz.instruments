package com.kidscademy.quiz.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.util.Preferences;
import com.kidscademy.quiz.view.HexaIcon;

/**
 * Display main actions and wait for user input.
 *
 * @author Iulian Rotaru
 */
public class MainActivity extends AppActivity implements View.OnClickListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    private final Preferences preferences;
    private HexaIcon volumeIcon;

    public MainActivity() {
        this.preferences = App.instance().preferences();
    }

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.main_play).setOnClickListener(this);
        findViewById(R.id.main_quiz).setOnClickListener(this);
        findViewById(R.id.main_balance).setOnClickListener(this);

        volumeIcon = findViewById(R.id.main_volume);
        volumeIcon.setOnClickListener(this);

        findViewById(R.id.main_close).setOnClickListener(this);
        findViewById(R.id.main_about).setOnClickListener(this);
        findViewById(R.id.main_no_ads).setOnClickListener(this);
        findViewById(R.id.main_recommended).setOnClickListener(this);
        findViewById(R.id.main_rate).setOnClickListener(this);
        findViewById(R.id.main_share).setOnClickListener(this);
        findViewById(R.id.main_settings).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateVolumeIcon();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_play:
                LevelsActivity.start(this);
                break;
            case R.id.main_quiz:
                QuizStartActivity.start(this);
                break;
            case R.id.main_balance:
                BalanceActivity.start(this);
                break;
            case R.id.main_volume:
                App.instance().preferences().toggleSoundsEffects();
                updateVolumeIcon();
                break;

            case R.id.main_about:
                AboutActivity.start(this);
                break;
            case R.id.main_share:
                ShareActivity.start(this);
                break;
            case R.id.main_rate:
                RateActivity.start(this);
                break;
            case R.id.main_close:
                onBackPressed();
                break;
        }
    }

    private void updateVolumeIcon() {
        int volumeDrawableId = preferences.isSoundsEffects() ? R.drawable.action_sounds_enable : R.drawable.action_sounds_mute;
        volumeIcon.setIconDrawable(ContextCompat.getDrawable(this, volumeDrawableId));
    }
}
