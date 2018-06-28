package com.kidscademy.quiz.instruments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Counters;
import com.kidscademy.quiz.instruments.model.Level;
import com.kidscademy.quiz.instruments.view.PercentDonutView;

import js.log.Log;
import js.log.LogFactory;

public class BalanceActivity extends AppActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(BalanceActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Context)");
        Intent intent = new Intent(activity, BalanceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private Balance balance;
    private Counters counters;

    @Override
    protected int layout() {
        return R.layout.activity_balance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        balance = App.storage().getBalance();
        counters = App.storage().getCounters();

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
        totalLevelsView.setText(Integer.toString(Level.getTotalLevels()));

        TextView unlockedLevelsView = findViewById(R.id.balance_unlocked_levels);
        unlockedLevelsView.setText(Integer.toString(Level.getUnlockedLevels()));

        TextView completedLevelsView = findViewById(R.id.balance_completed_levels);
        completedLevelsView.setText(Integer.toString(Level.getCompletedLevels()));

        PercentDonutView completPercentView = findViewById(R.id.balance_complete_percent);
        completPercentView.setPercent(((float)Level.getSolvedInstruments()) / ((float)Level.getTotalInstruments()));

        TextView pointsView = findViewById(R.id.balance_points);
        pointsView.setText(String.format("+%04d", balance.getScore()));

        TextView creditsView = findViewById(R.id.balance_credits);
        creditsView.setText(String.format("+%04d", balance.getCredit()));

        if (balance.hasResponseTime()) {
            findViewById(R.id.balance_response_time_layout).setVisibility(View.VISIBLE);
            TextView responseTimeView = findViewById(R.id.balance_response_time);
            responseTimeView.setText(Integer.toString(balance.getMinResponseTime()));
        }
    }
}
