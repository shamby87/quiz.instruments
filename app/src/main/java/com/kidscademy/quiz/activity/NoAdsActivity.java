package com.kidscademy.quiz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.R;

import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;

/**
 * No Ads Manifesto.
 *
 * @author Iulian Rotaru
 */
public class NoAdsActivity extends AppActivity implements OnClickListener {
    private static final Log log = LogFactory.getLog(NoAdsActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, NoAdsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    @Override
    protected int layout() {
        return R.layout.activity_no_ads;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.no_ads_disagree_button).setOnClickListener(this);
        findViewById(R.id.no_ads_agree_button).setOnClickListener(this);

        FloatingActionButton backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
    }

    private void setText(int textViewId, String format, Object... args) {
        TextView textView = findViewById(textViewId);
        if (textView == null) {
            throw new BugError("Invalid layout. Missing text view with id |%d|.", textViewId);
        }
        textView.setText(format != null ? String.format(format, args) : null);
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        boolean agree = false;
        switch (view.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;

            case R.id.no_ads_disagree_button:
                agree = false;
                break;
            case R.id.no_ads_agree_button:
                agree = true;
                break;
        }
        //AppBase.instance.controller().agreeNoAdsManifest(AppBase.instance().device(), agree);
        finish();
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
