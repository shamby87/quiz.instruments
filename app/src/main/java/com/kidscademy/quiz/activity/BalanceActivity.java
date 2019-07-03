package com.kidscademy.quiz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Storage;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.model.Balance;
import com.kidscademy.quiz.util.LevelsUtil;
import com.kidscademy.quiz.view.PercentDonutView;

import js.log.Log;
import js.log.LogFactory;

/**
 * Balance activity.
 *
 * @author Iulian Rotaru
 */
public class BalanceActivity extends AppActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(BalanceActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Context)");
        Intent intent = new Intent(activity, BalanceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private LevelsUtil levels;
    private Balance balance;

    @Override
    protected int layout() {
        return R.layout.activity_balance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        Storage storage = App.instance().storage();
        levels = new LevelsUtil(storage);
        balance = storage.getBalance();

        findViewById(R.id.fab_back).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }

    private void updateUI() {
        TextView totalLevelsView = findViewById(R.id.balance_total_levels);
        totalLevelsView.setText(Integer.toString(levels.getTotalLevels()));

        TextView unlockedLevelsView = findViewById(R.id.balance_unlocked_levels);
        unlockedLevelsView.setText(Integer.toString(levels.getUnlockedLevels()));

        TextView completedLevelsView = findViewById(R.id.balance_completed_levels);
        completedLevelsView.setText(Integer.toString(levels.getCompletedLevels()));

        PercentDonutView completPercentView = findViewById(R.id.balance_complete_percent);
        completPercentView.setPercent(((float) levels.getSolvedInstruments()) / ((float) levels.getTotalInstruments()));

        TextView pointsView = findViewById(R.id.balance_points);
        pointsView.setText(String.format("+%04d", balance.getScore()));

        TextView creditsView = findViewById(R.id.balance_credits);
        creditsView.setText(String.format("+%04d", balance.getCredit()));

        if (balance.hasResponseTime()) {
            findViewById(R.id.compo_response_time).setVisibility(View.VISIBLE);
            TextView responseTimeView = findViewById(R.id.compo_response_time_value);
            responseTimeView.setText(Integer.toString(balance.getMinResponseTime()));
        }
    }
}
