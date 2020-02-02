package com.kidscademy.quiz.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.util.Strings;

import org.jetbrains.annotations.NonNls;

public class RateActivity extends AppActivity implements View.OnClickListener {
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, RateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    @Override
    protected int layout() {
        return R.layout.activity_rate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.rate_button).setOnClickListener(this);
        FloatingActionButton backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rate_button:
                try {
                    startActivity(rate("market://details"));
                } catch (ActivityNotFoundException e) {
                    startActivity(rate("http://play.google.com/store/apps/details"));
                }
                break;

            case R.id.fab_back:
                onBackPressed();
                break;
        }
    }

    private Intent rate(@NonNls String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Strings.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }


    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
