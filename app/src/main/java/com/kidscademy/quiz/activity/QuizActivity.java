package com.kidscademy.quiz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Storage;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.model.Balance;
import com.kidscademy.quiz.model.QuizChallenge;
import com.kidscademy.quiz.model.QuizEngine;
import com.kidscademy.quiz.model.QuizEngineImpl;
import com.kidscademy.quiz.util.Preferences;
import com.kidscademy.quiz.util.Strings;
import com.kidscademy.quiz.view.QuizOptionsView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;
import js.util.Player;
import js.view.DialogOverlay;

/**
 * Run a set of challenges with options to select the right answer.
 *
 * @author Iulian Rotaru
 */
public class QuizActivity extends AppActivity implements View.OnClickListener, QuizOptionsView.Listener, QuizEngine.Listener {
    private static final Log log = LogFactory.getLog(QuizActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)"); // NON-NLS
        Intent intent = new Intent(activity, QuizActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private static final Random random = new Random();

    private final Storage storage;
    private final Preferences preferences;
    private final QuizEngine engine;
    /**
     * Mutex for UI updates. When a new challenge is displayed it can end in two ways: user select and option - correct or bad,
     * or quiz engine timeout. Both events trigger UI update but they comes from different threads. At limit is possible to
     * have both event triggered so we need a mechanism to ensure UI update happens only once per quiz session.
     * <p>
     * To ensure UI is updated only once per quiz session uses this mutex. Mutex become active when first update trigger
     * comes - be it user selected option or timeout. Mutex state is reset to inactive after new challenge is displayed.
     */
    private final AtomicBoolean uiMutex;
    private final Player player;
    private final Handler handler;

    private ImageView instrumentPictureView;
    private TextView instrumentNameView;
    private QuizChallenge challenge;

    private ProgressBar progressBar;
    private QuizOptionsView optionsView;
    private DialogOverlay dialogOverlay;

    private RatingBar leftTriesStars;
    private TextView totalCreditsView;
    private TextView quizSessionCreditsView;
    private TextView totalChallengesCountView;
    private TextView solvedChallengesCountView;

    private Runnable updateUI_Runnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };

    public QuizActivity() {
        log.trace("QuizActivity()"); // NON-NLS

        this.storage = App.instance().storage();
        this.preferences = App.instance().preferences();

        this.engine = new QuizEngineImpl(storage, this);
        this.uiMutex = new AtomicBoolean();
        this.player = new Player(this);
        this.handler = new Handler();
    }

    @Override
    protected int layout() {
        return R.layout.activity_quiz;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.quiz_timeout);
        instrumentPictureView = findViewById(R.id.quiz_picture);
        instrumentNameView = findViewById(R.id.quiz_name);

        optionsView = findViewById(R.id.quiz_options);
        optionsView.setListener(this);

        leftTriesStars = findViewById(R.id.quiz_left_tries);
        dialogOverlay = findViewById(R.id.quiz_dialog_overlay);

        totalCreditsView = findViewById(R.id.quiz_credits);
        quizSessionCreditsView = findViewById(R.id.quiz_level_credits);
        solvedChallengesCountView = findViewById(R.id.quiz_solved);
        totalChallengesCountView = findViewById(R.id.quiz_count);

        findViewById(R.id.fab_close).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        log.trace("onStart()"); // NON-NLS
        super.onStart();
        player.create();
        updateUI();
    }

    @Override
    public void onStop() {
        log.trace("stop()"); // NON-NLS
        engine.cancelChallenge();
        dialogOverlay.close();
        handler.removeCallbacks(updateUI_Runnable);
        player.destroy();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_close:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onQuizOptionSelected(String option) {
        log.trace("onQuizOptionSelected(String)"); // NON-NLS
        player.setVolume(1);

        // abort processing if UI update mutex is signaled, since UI is already updated from timeout event
        if (uiMutex.getAndSet(true)) {
            log.debug("Timeout occurred. Ignore quiz option selected event."); // NON-NLS
            return;
        }

        if (dialogOverlay.isActive()) {
            return;
        }

        if (!engine.checkAnswer(option)) {
            if (preferences.isSoundsEffects()) {
                player.play("fx/negative.mp3"); // NON-NLS
            }
            optionsView.highlightOption(challenge.getLocaleName(), updateUI_Runnable);
            return;
        }

        log.debug("Correct answer for quiz on |%s|\n", challenge); // NON-NLS
        storage.getBalance().updateResponseTime(engine.getResponseTime());

        instrumentNameView.setText(challenge.getLocaleName());
        if (preferences.isSoundsEffects()) {
            player.play(Strings.format("fx/positive-%d.mp3", random.nextInt(5))); //NON-NLS
            updateUI(2000);
        } else {
            updateUI(500);
        }
    }

    @Override
    public void onQuizProgress(int progress) {
        // although quiz progress event is executed on non UI thread we can safely use progress.setProgress()
        progressBar.setProgress(progress);
        if (preferences.isSoundsEffects()) {
            float volume = (float) Math.min(0.3, Math.max(0, (double) progressBar.getProgress() / progressBar.getMax() - 0.5));
            player.setVolume(volume);
        }
    }

    @Override
    public void onQuizTimeout() {
        log.trace("onQuizTimeout()"); // NON-NLS
        // ignores quiz timeout event if UI was updated by option selected event
        if (uiMutex.getAndSet(true)) {
            log.debug("Option selected event occured. Ignore timeout event."); // NON-NLS
            return;
        }

        player.stop();
        // update UI via handler even if no delay because quiz timeout is invoked from non UI thread
        updateUI(0);
    }

    private void updateUI(int delay) {
        handler.postDelayed(updateUI_Runnable, delay);
    }

    private void updateUI() {
        log.trace("updateUI()"); // NON-NLS
        optionsView.clear();
        leftTriesStars.setRating(engine.getLeftTries());

        Balance balance = storage.getBalance();
        totalCreditsView.setText(Strings.toString(balance.getCredit()));
        quizSessionCreditsView.setText(Strings.toString(engine.getCollectedCredits()));
        solvedChallengesCountView.setText(Strings.toString(engine.getSolvedChallengesCount()));
        totalChallengesCountView.setText(Strings.toString(engine.getTotalChallengesCount()));

        if (engine.getLeftTries() == 0) {
            log.debug("No more tries. Open quiz fail dialog."); // NON-NLS
            dialogOverlay.open(R.layout.dialog_quiz_fail, this);
            return;
        }

        challenge = engine.nextChallenge();
        if (challenge == null) {
            log.debug("No more challenges. Open quiz complete dialog."); // NON-NLS
            dialogOverlay.open(R.layout.dialog_quiz_complete, this);
            return;
        }

        BitmapLoader loader = new BitmapLoader(this, challenge.getPicturePath(), instrumentPictureView);
        loader.start();
        instrumentPictureView.setTag(challenge.getPicturePath());
        instrumentNameView.setText(null);

        optionsView.init(challenge.getOptions());

        if (preferences.isSoundsEffects()) {
            player.setVolume(0);
            player.play("fx/clock-tick.mp3"); // NON-NLS
        }

        // reset UI updates mutex after new challenge was displayed
        uiMutex.set(false);
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }

    // ------------------------------------------------------
    // DIALOG OVERLAYS

    @SuppressWarnings("unused")
    private static class QuizFailDialog extends FrameLayout implements DialogOverlay.Content, View.OnClickListener {
        private static final String packageName = "com.kidscademy.instruments"; // NON-NLS

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
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName))); // NON-NLS
                            } catch (android.content.ActivityNotFoundException unused) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName))); // NON-NLS
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
            if (App.instance().preferences().isSoundsEffects()) {
                activity.player.play("fx/hooray.mp3"); // NON-NLS
            }
            dialog.setText(R.id.quiz_complete_credits, "+%d", activity.engine.getCollectedCredits()); // NON-NLS

            final View responseTimeView = findViewById(R.id.quiz_complete_response_time);
            final TextView responseTimeValueView = findViewById(R.id.quiz_complete_response_time_value);

            int responseTime = activity.engine.getAverageResponseTime();
            if (responseTime != 0) {
                responseTimeValueView.setText(Strings.toString(responseTime));
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
