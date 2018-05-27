package com.kidscademy.instruments.quiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.kidscademy.app.AppBase;
import com.kidscademy.app.FullScreenActivity;

import js.log.Log;
import js.log.LogFactory;

public class AboutActivity extends FullScreenActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(AboutActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        AppBase.audit().openAbout();
        setContentView(R.layout.activity_about);

        FloatingActionButton backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);

        // developer pictures uses icon view from library that needs software layer
        // there is a bug on lollipop 5.0 that draw black background around circle icon if set software layer on icon view
        // solution is to set software layer on parent, that is, here
        if (Build.VERSION.SDK_INT < 22) {
            findViewById(R.id.about_scroll).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
