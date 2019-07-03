package com.kidscademy.quiz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Storage;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.model.Level;
import com.kidscademy.quiz.model.LevelState;
import com.kidscademy.quiz.model.LevelsCardAdapter;
import com.kidscademy.quiz.app.Flags;

import js.log.Log;
import js.log.LogFactory;
import js.util.Player;

/**
 * Display all levels as cards in a vertical list.
 *
 * @author Iulian Rotaru
 */
public class LevelsActivity extends AppActivity implements LevelsCardAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(LevelsActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, LevelsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private Storage storage;
    private FloatingActionButton backFAB;
    private LevelsCardAdapter adapter;
    private Player player;

    @Override
    protected int layout() {
        return R.layout.activity_levels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        storage = App.instance().storage();
        player = new Player(this);

        RecyclerView listView = findViewById(R.id.levels);
        listView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        adapter = new LevelsCardAdapter(this, storage.getLevels());
        listView.setAdapter(adapter);

        FloatingActionButton backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        player.create();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        log.trace("stop()");
        super.onStop();
        player.destroy();
    }

    @Override
    public void onLevelSelected(Level level) {
        final int levelIndex = level.getIndex();

        Flags.setCurrentLevel(levelIndex);
        LevelState levelState = storage.getLevelState(levelIndex);

        if (!levelState.isUnlocked()) {
            player.play("fx/negative.mp3");
            return;
        }

        if (levelState.isComplete()) {
            LevelInstrumentsActivity.start(this, levelIndex);
        } else {
            GameActivity.start(this, levelIndex);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
