package com.kidscademy.instruments.quiz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.kidscademy.app.FullScreenActivity;

import js.log.Log;
import js.log.LogFactory;

public class GameOverActivity extends FullScreenActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(GameOverActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, GameOverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        findViewById(R.id.game_over_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_over_action:
                App.audit().openMarket();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + getString(R.string.app_publisher))));
                } catch (android.content.ActivityNotFoundException unused) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:" + getString(R.string.app_publisher))));
                }
                break;
        }
    }
}
