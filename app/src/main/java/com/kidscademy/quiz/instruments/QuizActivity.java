package com.kidscademy.quiz.instruments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.quiz.instruments.engine.QuizEngine;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.util.QuizTimeoutListener;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;
import js.util.Player;
import js.view.DialogOverlay;

public class QuizActivity extends AppActivity implements View.OnClickListener, QuizTimeoutListener {
    private static final Log log = LogFactory.getLog(QuizActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, QuizActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private static final float CLOCK_TICK_THRESHOLD = 0.5F;

    private static final Random random = new Random();

    private int levelIndex;
    private QuizEngine engine;
    private ImageView instrumentPictureView;
    private TextView instrumentNameView;
    private Instrument challengedInstrument;
    private Button[] optionButtons;
    private int buttonTextColor;

    private RatingBar leftTriesStars;
    private TextView creditsView;
    private TextView levelCreditsView;
    private TextView solvedView;
    private TextView quizCountView;

    private Player player;
    private Handler handler;
    private DialogOverlay dialogOverlay;

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };

    private QuizTimeout quizTimeout;

    private int lastProgressPercent;

    public QuizActivity() {
        log.trace("SpellSound()");
        player = new Player(this);
        handler = new Handler();
    }

    @Override
    protected int layout() {
        return R.layout.activity_quiz;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instrumentPictureView = findViewById(R.id.quiz_logo);
        instrumentNameView = findViewById(R.id.quiz_name);

        GridLayout optionsGrid = findViewById(R.id.quiz_options);
        optionButtons = new Button[optionsGrid.getChildCount()];
        for (int i = 0; i < optionButtons.length; ++i) {
            optionButtons[i] = (Button) optionsGrid.getChildAt(i);
            optionButtons[i].setOnClickListener(this);
        }

        ColorStateList buttonsColorStateList = optionButtons[0].getTextColors();
        buttonTextColor = buttonsColorStateList.getDefaultColor();

        leftTriesStars = findViewById(R.id.quiz_left_tries);
        dialogOverlay = findViewById(R.id.quiz_dialog_overlay);

        creditsView = findViewById(R.id.quiz_credits);
        levelCreditsView = findViewById(R.id.quiz_level_credits);
        solvedView = findViewById(R.id.quiz_solved);
        quizCountView = findViewById(R.id.quiz_count);

        findViewById(R.id.fab_close).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        player.create();

        engine = new QuizEngine();
        quizTimeout = new QuizTimeout(this, (ProgressBar) findViewById(R.id.quiz_timeout));

        updateUI();
        App.audit().playQuiz();
    }

    @Override
    public void onStop() {
        log.trace("onStop()");
        player.destroy();
        quizTimeout.stop();
        dialogOverlay.close();
        handler.removeCallbacks(update);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (dialogOverlay.isActive()) {
            return;
        }

        if (view.getId() == R.id.fab_close) {
            App.audit().quizAbort(challengedInstrument);
            onBackPressed();
            return;
        }

        final Button selectedButton = (Button) view;
        int responseTime = quizTimeout.stop();

        if (App.prefs().isKeyVibrator()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
        }

        // selected option text is generated and also checked by quiz engine
        // since display language is not an issue we can use user interface text
        if (!engine.checkAnswer(selectedButton.getText().toString(), quizTimeout.getSpeedFactor())) {
            if (App.prefs().isSoundsEffects()) {
                player.play("fx/negative.mp3");
            }
            App.audit().quizWrongAnswer(challengedInstrument, selectedButton.getText().toString());
            for (final Button button : optionButtons) {
                if (button.getText().toString().equals(challengedInstrument.getDisplay())) {
                    ObjectAnimator anim = ObjectAnimator.ofInt(button, "textColor", Color.TRANSPARENT, Color.WHITE);
                    anim.setDuration(500);
                    anim.setEvaluator(new ArgbEvaluator());
                    anim.setRepeatMode(ValueAnimator.REVERSE);
                    anim.setRepeatCount(4);
                    anim.start();

                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            handler.postDelayed(update, 500);
                        }
                    });
                    break;
                }
            }
            return;
        }

        App.audit().quizCorrectAnswer(challengedInstrument);
        log.debug("Correct answer for quiz on |%s|\n", challengedInstrument);
        App.storage().getBalance().updateResponseTime(responseTime);

        BitmapLoader loader = new BitmapLoader(this, challengedInstrument.getPicturePath(), instrumentPictureView);
        loader.start();
        instrumentNameView.setText(challengedInstrument.getDisplay());

        if (App.prefs().isSoundsEffects()) {
            player.play(String.format("fx/positive-%d.mp3", random.nextInt(5)));
            handler.postDelayed(update, 2000);
        } else {
            handler.postDelayed(update, 500);
        }
    }

    @Override
    public void onQuizTimeout() {
        engine.onAswerTimeout();
        handler.postDelayed(update, 1000);
    }

    private void updateUI() {
        for (Button button : optionButtons) {
            button.setTextColor(buttonTextColor);
        }

        if (leftTriesStars != null) {
            leftTriesStars.setRating(engine.getLeftTries());
        }

        if (engine.noMoreTries()) {
            dialogOverlay.open(R.layout.dialog_quiz_fail, this);
            return;
        }
        challengedInstrument = engine.nextChallenge();
        if (challengedInstrument == null) {
            dialogOverlay.open(R.layout.dialog_quiz_complete, this);
            return;
        }

        Balance balance = App.storage().getBalance();
        creditsView.setText(Integer.toString(balance.getCredit()));
        levelCreditsView.setText(Integer.toString(engine.getCollectedCredits()));
        solvedView.setText(Integer.toString(engine.getSolvedCount()));
        quizCountView.setText(Integer.toString(engine.getQuizCount()));

        BitmapLoader loader = new BitmapLoader(this, challengedInstrument.getPicturePath(), instrumentPictureView);
        loader.start();
        instrumentNameView.setText(null);

        List<String> options = engine.getOptions(optionButtons.length);
        for (int i = 0; i < optionButtons.length; ++i) {
            final String option = options.get(i);
            optionButtons[i].setText(option);
        }

        quizTimeout.start();
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }

    // ------------------------------------------------------
    // QUIZ TIMER

    private class QuizTimeout {
        private final QuizTimeoutListener listener;
        private final ProgressBar progress;
        private final Timer timer;

        private TimerTask task;
        private long startTimestamp;

        public QuizTimeout(QuizTimeoutListener listener, ProgressBar progress) {
            this.listener = listener;
            this.progress = progress;
            this.timer = new Timer();
        }

        public void start() {
            task = new TimerTask() {
                @Override
                public void run() {
                    progress.setProgress((int) (System.currentTimeMillis() - startTimestamp));
                    if (App.prefs().isSoundsEffects()) {
                        float volume = (float) Math.min(0.3, Math.max(0, (double) progress.getProgress() / progress.getMax() - 0.5));
                        player.setVolume(volume);
                    }
                    if (progress.getProgress() >= progress.getMax()) {
                        stop();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onQuizTimeout();
                            }
                        });
                    }
                }
            };
            startTimestamp = System.currentTimeMillis();
            if (App.prefs().isSoundsEffects()) {
                player.setVolume(0);
                player.play("fx/clock-tick.mp3");
            }
            timer.schedule(task, 0, 40);
        }

        public int stop() {
            if (task != null) {
                task.cancel();
            }
            player.stop();
            return (int) (System.currentTimeMillis() - startTimestamp);
        }

        public double getSpeedFactor() {
            long elapsedTime = System.currentTimeMillis() - startTimestamp;
            return 3 - (2 * elapsedTime / progress.getMax());
        }
    }

    // ------------------------------------------------------
    // DIALOG OVERLAYS

    @SuppressWarnings("unused")
    private static class QuizFailDialog extends FrameLayout implements DialogOverlay.Content, View.OnClickListener {
        private static final String packageName = "com.kidscademy.instruments";

        private DialogOverlay dialog;
        private QuizActivity activity;

        public QuizFailDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            this.dialog = dialog;
            activity = (QuizActivity) args[0];
            findViewById(R.id.quiz_fail_action).setOnClickListener(this);

            ImageView backgroundView = findViewById(R.id.page_background);
            backgroundView.setImageResource(App.getBackgroundResId());
        }

        @Override
        public void onClose() {
            activity.onBackPressed();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.quiz_fail_action:
                    activity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            PackageManager packageManager = activity.getPackageManager();
                            Intent lauchIntent = packageManager.getLaunchIntentForPackage(packageName);
                            if (lauchIntent != null) {
                                activity.startActivity(lauchIntent);
                                return;
                            }

                            try {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                            } catch (android.content.ActivityNotFoundException unused) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                            }
                        }
                    });
                    dialog.hide();
                    activity.onBackPressed();
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    private static class QuizCompleteDialog extends FrameLayout implements DialogOverlay.Content {
        private QuizActivity activity;

        public QuizCompleteDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void onOpen(DialogOverlay dialog, Object... args) {
            activity = (QuizActivity) args[0];
            if (App.prefs().isSoundsEffects()) {
                activity.player.play("fx/hooray.mp3");
            }
            dialog.setText(R.id.quiz_complete_credits, "+%d", activity.engine.getCollectedCredits());

            final View responseTimeView = findViewById(R.id.quiz_complete_response_time);
            final TextView responseTimeValueView = findViewById(R.id.quiz_complete_response_time_value);

            int responseTime = activity.engine.getAverageResponseTime();
            if (responseTime != 0) {
                responseTimeValueView.setText(Integer.toString(responseTime));
                responseTimeView.setVisibility(View.VISIBLE);
            }

            ImageView backgroundView = findViewById(R.id.page_background);
            backgroundView.setImageResource(App.getBackgroundResId());
        }

        public void onClose() {
            activity.onBackPressed();
        }
    }
}
