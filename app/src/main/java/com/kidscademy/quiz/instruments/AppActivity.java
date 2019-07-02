package com.kidscademy.quiz.instruments;

import android.os.Bundle;
import android.widget.ImageView;

import js.log.Log;
import js.log.LogFactory;

/**
 * Application base activity.
 *
 * @author Iulian Rotaru
 */
public abstract class AppActivity extends FullScreenActivity {
    private static final Log log = LogFactory.getLog(AppActivity.class);

    protected abstract int layout();

    private ImageView backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(layout());
        backgroundView = findViewById(R.id.page_background);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        backgroundView.setImageResource(App.getBackgroundResId());
    }
}
