package com.kidscademy.quiz.instruments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.app.PreferencesActivity;
import com.kidscademy.quiz.instruments.view.HexaIcon;

import js.log.Log;
import js.log.LogFactory;

public class MainActivity extends FullScreenActivity implements View.OnClickListener {
    /**
     * Class logger.
     */
    private static final Log log = LogFactory.getLog(MainActivity.class);

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void start(Context context) {
        log.trace("start(Context)");
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    private ImageView backgroundView;
    private HexaIcon volumeIcon;

    public MainActivity() {
        log.trace("MainActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundView = findViewById(R.id.page_background);

        findViewById(R.id.main_play).setOnClickListener(this);
        findViewById(R.id.main_quiz).setOnClickListener(this);
        findViewById(R.id.main_score).setOnClickListener(this);

        volumeIcon = findViewById(R.id.main_volume);
        volumeIcon.setOnClickListener(this);

        findViewById(R.id.main_help).setOnClickListener(this);
        findViewById(R.id.main_about).setOnClickListener(this);
        findViewById(R.id.main_no_ads).setOnClickListener(this);
        findViewById(R.id.main_recommended).setOnClickListener(this);
        findViewById(R.id.main_rate).setOnClickListener(this);
        findViewById(R.id.main_share).setOnClickListener(this);
        findViewById(R.id.main_settings).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        backgroundView.setImageResource(App.getBackgroundResId());
        updateVolumeIcon();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.main_play:
                LevelsActivity.start(this);
                break;
            case R.id.main_quiz:
                QuizStartActivity.start(this);
                break;
            case R.id.main_score:
                BalanceActivity.start(this);
                break;
            case R.id.main_volume:
                App.prefs().setSoundsEffects(!App.prefs().isSoundsEffects());
                updateVolumeIcon();
                break;

            case R.id.main_about:
                AboutActivity.start(this);
                break;
            case R.id.main_no_ads:
                NoAdsActivity.start(this);
                break;
            case R.id.main_rate:
                App.audit().openRate();
                try {
                    startActivity(rate("market://details"));
                } catch (ActivityNotFoundException e) {
                    startActivity(rate("http://play.google.com/store/apps/details"));
                }
                break;
            case R.id.main_recommended:
                RecommendedActivity.start(this);
                break;
            case R.id.main_share:
                ShareActivity.start(this);
                break;
            case R.id.main_settings:
                intent = new Intent(this, PreferencesActivity.class);
                break;
        }
        if (intent != null) {
            finish();
            startActivity(intent);
        }
    }

    private Intent rate(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void updateVolumeIcon() {
        int volumeDrawableId = App.prefs().isSoundsEffects() ? R.drawable.ic_action_volume_up : R.drawable.ic_action_volume_mute;
        volumeIcon.setIconDrawable(ContextCompat.getDrawable(this, volumeDrawableId));
    }
}
