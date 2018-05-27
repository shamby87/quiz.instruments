package com.kidscademy.instruments.quiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.instruments.quiz.model.Level;
import com.kidscademy.instruments.quiz.model.LevelState;
import com.kidscademy.instruments.quiz.model.LevelsCardAdapter;
import com.kidscademy.instruments.quiz.util.Flags;

import js.log.Log;
import js.log.LogFactory;
import js.util.Player;

public class LevelsActivity extends FullScreenActivity implements LevelsCardAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(LevelsActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, LevelsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private ImageView backgroundView;
    private LinearLayoutManager layoutManager;
    private RecyclerView listView;
    private FloatingActionButton backFAB;
    private LevelsCardAdapter adapter;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        backgroundView = findViewById(R.id.page_background);
        player = new Player(this);

        listView = findViewById(R.id.levels);
        listView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        adapter = new LevelsCardAdapter(this, App.storage().getLevels());
        listView.setAdapter(adapter);

        backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        backgroundView.setImageResource(App.getBackgroundResId());
        player.create();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        log.trace("onStop()");
        super.onStop();
        player.destroy();
    }

    @Override
    public void onLevelSelected(Level level) {
        final int levelIndex = level.getIndex();

        Flags.setCurrentLevel(levelIndex);
        LevelState levelState = App.storage().getLevelState(levelIndex);

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
