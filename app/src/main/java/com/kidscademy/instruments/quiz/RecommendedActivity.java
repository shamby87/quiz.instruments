package com.kidscademy.instruments.quiz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kidscademy.app.AppBase;
import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.commons.Recommended;
import com.kidscademy.instruments.quiz.model.RecommendedAdapter;

import js.log.Log;
import js.log.LogFactory;

/**
 * List of application related to this application. This activity displays recommended application list supplied by
 * storage and subject to synchronization with server repository.
 *
 * @author Iulian Rotaru
 */
public class RecommendedActivity extends FullScreenActivity implements RecommendedAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(RecommendedActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, RecommendedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private LinearLayoutManager layoutManager;
    private RecyclerView listView;
    private RecommendedAdapter adapter;
    private FloatingActionButton backFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppBase.audit().openRecommended();
        setContentView(R.layout.activity_recommended);

        listView = findViewById(R.id.recommended_list);
        listView.setSoundEffectsEnabled(false);

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(listView.getContext(), layoutManager.getOrientation());
        listView.addItemDecoration(divider);

        adapter = new RecommendedAdapter(this, AppBase.storage().getRecommended());
        listView.setAdapter(adapter);

        backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
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
    public void onRecommendedAppSelected(Recommended app) {
        String packageName = app.getPackageName();
        AppBase.audit().recommendApp(packageName);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException unused) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
