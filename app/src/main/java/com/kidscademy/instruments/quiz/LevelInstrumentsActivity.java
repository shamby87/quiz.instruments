package com.kidscademy.instruments.quiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.instruments.quiz.model.Instrument;
import com.kidscademy.instruments.quiz.model.Level;
import com.kidscademy.instruments.quiz.model.LevelInstrumentsAdapter;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public class LevelInstrumentsActivity extends FullScreenActivity implements LevelInstrumentsAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(LevelInstrumentsActivity.class);

    private static final String ARG_LEVEL_INDEX = "levelIndex";

    public static void start(Activity activity, int levelIndex) {
        log.trace("start(Context, Level)");
        Intent intent = new Intent(activity, LevelInstrumentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private ImageView backgroundView;
    private GridLayoutManager layoutManager;
    private RecyclerView gridView;
    private LevelInstrumentsAdapter adapter;
    private Level level;
    private boolean unsolved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_instruments);

        backgroundView = findViewById(R.id.page_background);

        gridView = findViewById(R.id.level_preview_grid);

        layoutManager = new GridLayoutManager(this, 2);
        gridView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        int levelIndex = intent.getIntExtra(ARG_LEVEL_INDEX, 0);
        level = App.storage().getLevel(levelIndex);

        adapter = new LevelInstrumentsAdapter(this, level);
        gridView.setAdapter(adapter);

        setTitle(Strings.concat("LEVEL ", levelIndex + 1));
        findViewById(R.id.fab_back).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        backgroundView.setImageResource(App.getBackgroundResId());
    }

    @Override
    public void onInstrumentSelected(int position, Instrument instrument) {
        if (!level.getState().isSolvedInstrument(position)) {
            GameActivity.start(this, level.getIndex(), instrument.getName());
        }
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
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
        if (unsolved) {
            GameActivity.start(this, level.getIndex());
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
    }
}
