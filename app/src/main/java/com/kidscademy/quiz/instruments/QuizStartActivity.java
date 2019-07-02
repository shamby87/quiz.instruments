package com.kidscademy.quiz.instruments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.view.HexaIcon;

import java.util.Random;

import js.log.Log;
import js.log.LogFactory;

/**
 * Display quiz start message and wait for user start.
 *
 * @author Iulian Rotaru
 */
public class QuizStartActivity extends AppActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(QuizStartActivity.class);

    private static final int DELAY = 2500;
    private static final int PERIOD = 1000;

    private static final Random random = new Random();

    private View responseTimeView;
    private TextView responseTimeValueView;

    private TextView messsageText;
    private TextView warningText;

    private Handler handler;
    private HexaIcon[] icons;
    private int currentIconIndex;

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, QuizStartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    @Override
    protected int layout() {
        return R.layout.activity_quiz_start;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        handler = new Handler();
        responseTimeView = findViewById(R.id.compo_response_time);
        responseTimeValueView = findViewById(R.id.compo_response_time_value);

        messsageText = findViewById(R.id.quiz_start_message);
        warningText = findViewById(R.id.quiz_start_warning);

        ViewGroup iconsGroup = findViewById(R.id.quiz_start_icons);
        icons = new HexaIcon[iconsGroup.getChildCount()];
        for (int i = 0; i < icons.length; ++i) {
            icons[i] = (HexaIcon) iconsGroup.getChildAt(i);
            icons[i].setOnClickListener(this);
        }
        currentIconIndex = random.nextInt(icons.length);
        icons[currentIconIndex].setIconDrawable(ContextCompat.getDrawable(this, R.drawable.action_play));
        // icon tag is used by UI testing
        icons[currentIconIndex].setTag("start-quiz");

        findViewById(R.id.fab_back).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_text_out);
        animation.setStartOffset(2000);
        messsageText.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.slide_text_out);
        animation.setStartOffset(2500);
        warningText.startAnimation(animation);

        Balance balance = App.storage().getBalance();
        if (balance.hasResponseTime()) {
            responseTimeView.setVisibility(View.VISIBLE);
            responseTimeValueView.setText(Integer.toString(balance.getMinResponseTime()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;

            default:
                QuizActivity.start(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
