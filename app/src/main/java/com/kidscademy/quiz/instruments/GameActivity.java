package com.kidscademy.quiz.instruments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.quiz.instruments.engine.GameEngine;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.Level;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.quiz.instruments.util.Assets;
import com.kidscademy.quiz.instruments.view.KeyboardView;
import com.kidscademy.quiz.instruments.view.NameView;
import com.kidscademy.quiz.instruments.view.RandomColorFAB;

import java.util.Random;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;
import js.util.Player;
import js.util.Strings;
import js.view.DialogOverlay;

public class GameActivity extends FullScreenActivity implements OnClickListener, KeyboardView.Listener, NameView.Listener {
    private static final Log log = LogFactory.getLog(GameActivity.class);

    private static final String ARG_LEVEL_INDEX = "levelIndex";
    private static final String ARG_BRAND_NAME = "responseView";

    public static void start(Activity activity, int levelIndex) {
        log.trace("start(Activity, int)");
        Intent intent = new Intent(activity, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    public static void start(Activity activity, int levelIndex, String brandName) {
        log.trace("start(Context, int, String)");
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
        intent.putExtra(ARG_BRAND_NAME, brandName);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private static final Random random = new Random();

    private GameEngine engine;
    private NameView nameView;
    private KeyboardView keyboardView;
    private ImageView imageView;
    private TextView scoreView;
    private TextView creditView;
    private TextView scorePlusView;
    private TextView brandsCountView;
    private TextView solvedBrandsCountView;
    private Player player;
    private Handler handler;

    private boolean isFabOpen = false;
    private RandomColorFAB fabMenu;
    private RandomColorFAB fabHint;
    private RandomColorFAB fabViewGrid;
    private RandomColorFAB fabSkipNext;
    private RandomColorFAB fabBack;
    private Animation[] animFabOpen = new Animation[4];
    private Animation[] animFabClose = new Animation[4];
    private Animation animFabRotateForward;
    private Animation animFabRotateBackward;

    /**
     * Value of level index loaded from intent.
     */
    private int levelIndex;

    /**
     * The level currently played.
     */
    private Level level;
    private LevelState levelState;

    /**
     * Overlay revealed to notify about next level being unlocked.
     */
    private DialogOverlay dialogOverlay;

    /**
     * Instrument currently displayed as challenge.
     */
    private Instrument challengedInstrument;

    private Balance balance;

    /**
     * Run {@link #nextChallenge()}.
     */
    private Runnable nextChallenge = new Runnable() {
        @Override
        public void run() {
            nextChallenge();
        }
    };

    /**
     * Reveal {@link #dialogOverlay} and wait for user to close it. On overlay close load next challenge.
     */
    private Runnable nextLevelUnlocked = new Runnable() {
        @Override
        public void run() {
            // last argument is true for level complete, so that in case of level unlocked is false
            dialogOverlay.open(R.layout.dialog_level_state, GameActivity.this, false);
        }
    };

    private ImageView backgroundView;

    public GameActivity() {
        log.trace("Game()");
        player = new Player(this);
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        levelIndex = getIntent().getIntExtra(ARG_LEVEL_INDEX, 0);
        balance = App.storage().getBalance();

        nameView = findViewById(R.id.game_answer);
        nameView.setPlayer(player);
        nameView.setListener(this);

        keyboardView = findViewById(R.id.game_keyboard);
        keyboardView.setPlayer(player);
        keyboardView.setListener(this);

        scoreView = findViewById(R.id.game_score);
        creditView = findViewById(R.id.game_credit);
        scorePlusView = findViewById(R.id.game_score_plus);
        brandsCountView = findViewById(R.id.game_brands_count);
        solvedBrandsCountView = findViewById(R.id.game_solved_brands_count);
        imageView = findViewById(R.id.game_challenge);

        dialogOverlay = findViewById(R.id.game_unlock_level_overlay);
        backgroundView = findViewById(R.id.page_background);

        for (int i = 0; i < animFabOpen.length; ++i) {
            animFabOpen[i] = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            animFabOpen[i].setStartOffset(i * 100);
        }
        for (int i = 0; i < animFabClose.length; ++i) {
            animFabClose[i] = AnimationUtils.loadAnimation(this, R.anim.fab_close);
            animFabClose[i].setStartOffset(animFabClose.length - i * 100);
        }
        animFabRotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        animFabRotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);

        fabMenu = findViewById(R.id.game_fab_menu);
        fabHint = findViewById(R.id.game_fab_hint);
        fabViewGrid = findViewById(R.id.game_fab_view_grid);
        fabSkipNext = findViewById(R.id.game_fab_skip_next);
        fabBack = findViewById(R.id.game_fab_back);

        fabMenu.setOnClickListener(this);
        fabHint.setOnClickListener(this);
        fabViewGrid.setOnClickListener(this);
        fabSkipNext.setOnClickListener(this);
        fabBack.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log.trace("onNewIntent(Intent)");
        super.onNewIntent(intent);
        setIntent(intent);
        levelIndex = getIntent().getIntExtra(ARG_LEVEL_INDEX, 0);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();
        player.create();

        level = App.storage().getLevel(levelIndex);
        levelState = App.storage().getLevelState(levelIndex);
        engine = new GameEngine(App.storage(), levelState);
        challengedInstrument = engine.initChallenge(getIntent().getStringExtra(ARG_BRAND_NAME));

        setTitle(Strings.concat("LEVEL ", levelIndex + 1));

        imageView.setImageDrawable(null);
        updateUI();

        App.audit().playGame(levelIndex);
        backgroundView.setImageResource(App.getBackgroundResId());
    }

    @Override
    public void onStop() {
        log.trace("onStop()");
        player.destroy();
        handler.removeCallbacks(nextChallenge);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.game_fab_menu:
                toggleFabMenu();
                break;

            case R.id.game_fab_hint:
                if (!balance.hasCredit()) {
                    dialogOverlay.open(R.layout.dialog_no_credit, this);
                } else {
                    dialogOverlay.open(R.layout.dialog_trade_hints, this);
                }
                break;

            case R.id.game_fab_view_grid:
                LevelInstrumentsActivity.start(this, level.getIndex());
                break;

            case R.id.game_fab_skip_next:
                player.stop();
                nextChallenge();
                break;

            case R.id.game_fab_back:
                toggleFabMenu();
                onBackPressed();
                break;

            default:
                view.setVisibility(View.INVISIBLE);
                player.play("fx/click.mp3");
        }
    }

    @Override
    public boolean onKeyboardChar(char c) {
        return handleKeyboardChar(c);
    }

    private boolean handleKeyboardChar(char c) {
        if (nameView.hasAllCharsFilled()) {
            player.play("fx/negative.mp3");
            return false;
        }
        nameView.putChar(c);
        if (!nameView.hasAllCharsFilled()) {
            return true;
        }

        if (!engine.checkAnswer(nameView.getValue())) {
            player.play("fx/negative.mp3");
            return true;
        }

        scorePlusView.setText(String.format("+%d", Balance.getScoreIncrement(level.getIndex())));
        scorePlusView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);

        player.play(String.format("fx/positive-%d.mp3", random.nextInt(5)));
        BitmapLoader loader = new BitmapLoader(this, challengedInstrument.getPicturePath(), imageView);
        loader.start();

        // engine check answer takes care to unlock next level if current level threshold was reached
        if (engine.wasNextLevelUnlocked()) {
            handler.postDelayed(nextLevelUnlocked, 2000);
        } else {
            handler.postDelayed(nextChallenge, 2000);
        }

        return true;
    }

    @Override
    public void onNameChar(char c) {
        keyboardView.ungetChar(c);
    }

    private void nextChallenge() {
        challengedInstrument = engine.nextChallenge();
        updateUI();
    }

    private void updateUI() {
        if (challengedInstrument == null) {
            // last argument is true to signal level complete
            dialogOverlay.open(R.layout.dialog_level_state, GameActivity.this, true);
            return;
        }

        scoreView.setText(Integer.toString(balance.getScore()));
        creditView.setText(Integer.toString(balance.getCredit()));

        brandsCountView.setText(Integer.toString(level.getInstrumentsCount()));
        solvedBrandsCountView.setText(Integer.toString(levelState.getSolvedInstrumentsCount()));
        nameView.init(challengedInstrument.getName());
        keyboardView.init(challengedInstrument.getName());

        BitmapLoader loader = new BitmapLoader(this, challengedInstrument.getPicturePath(), imageView);
        loader.setRunnable(new Runnable() {
            @Override
            public void run() {
                scorePlusView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        });
        loader.start();
    }

    @Override
    public void onBackPressed() {
        dialogOverlay.close();
        final Intent intent = new Intent(this, LevelsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            fabMenu.startAnimation(animFabRotateBackward);

            fabHint.startAnimation(animFabClose[0]);
            fabViewGrid.startAnimation(animFabClose[1]);
            fabSkipNext.startAnimation(animFabClose[2]);
            fabBack.startAnimation(animFabClose[3]);

            fabHint.setClickable(false);
            fabViewGrid.setClickable(false);
            fabSkipNext.setClickable(false);
            fabBack.setClickable(false);

            isFabOpen = false;
        } else {
            fabMenu.startAnimation(animFabRotateForward);

            fabHint.startAnimation(animFabOpen[0]);
            fabViewGrid.startAnimation(animFabOpen[1]);
            fabSkipNext.startAnimation(animFabOpen[2]);
            fabBack.startAnimation(animFabOpen[3]);

            fabHint.setClickable(true);
            fabViewGrid.setClickable(true);
            fabSkipNext.setClickable(true);
            fabBack.setClickable(true);

            isFabOpen = true;
        }
    }

    // ------------------------------------------------------
    // DIALOG OVERLAY

    @SuppressWarnings("unused")
    private static class LevelStateDialog extends GameDialog {
        private boolean levelComplete;

        public LevelStateDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            super.onOpen(dialog, args);
            levelComplete = (Boolean) args[1];
            activity.player.play("fx/hooray.mp3");

            if (levelComplete) {
                dialog.setText(R.id.level_state_message, "%s COMPLETE", Assets.getLevelName(activity, activity.level.getIndex()));
                dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelCompleteBonus(activity.level.getIndex()));
            } else {
                dialog.setText(R.id.level_state_message, "%s UNLOCKED", Assets.getLevelName(activity, activity.engine.getUnlockedLevelIndex()));
                dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelUnlockBonus(activity.level.getIndex()));
            }
        }

        @Override
        public void onClose() {
            super.onClose();
            if (levelComplete) {
                if (Level.areAllLevelsComplete()) {
                    GameOverActivity.start(activity);
                } else {
                    LevelsActivity.start(activity);
                    activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            } else {
                activity.handler.postDelayed(activity.nextChallenge, 1000);
            }
        }
    }

    @SuppressWarnings("unused")
    private static class NoCreditDialog extends GameDialog implements OnClickListener {
        private boolean openQuizSelector;

        public NoCreditDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            super.onOpen(dialog, args);
            dialog.findViewById(R.id.no_credit_action).setOnClickListener(this);
        }

        @Override
        public void onClose() {
            super.onClose();
            if (openQuizSelector) {
                QuizActivity.start(activity);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.no_credit_action:
                    openQuizSelector = true;
                    dialog.close();
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    private static class TradeHintsDialog extends GameDialog implements OnClickListener {
        public TradeHintsDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            super.onOpen(dialog, args);

            dialog.setText(R.id.trade_hints_title, activity.getString(R.string.trade_hints_title), activity.balance.getCredit());
            dialog.setText(R.id.reveal_letter_deduction, "%d", Balance.getRevealLetterDeduction());
            dialog.setText(R.id.verify_deduction, "%d", Balance.getVerifyInputDeduction());
            dialog.setText(R.id.hide_letters_deduction, "%d", Balance.getHideLettersDeduction());
            dialog.setText(R.id.play_sample_deduction, "%d", Balance.getSayNameDeduction());

            dialog.findViewById(R.id.reveal_letter_action).setOnClickListener(this);
            dialog.findViewById(R.id.verify_action).setOnClickListener(this);
            dialog.findViewById(R.id.hide_letters_action).setOnClickListener(this);
            dialog.findViewById(R.id.play_sample_action).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            boolean deductionPerformed = false;

            switch (view.getId()) {
                case R.id.reveal_letter_action:
                    if (activity.balance.deductRevealLetter()) {
                        deductionPerformed = true;
                        int firstMissingCharIndex = activity.nameView.getFirstMissingCharIndex();
                        assert firstMissingCharIndex != -1;
                        activity.handleKeyboardChar(activity.keyboardView.getExpectedChar(firstMissingCharIndex));
                    }
                    break;

                case R.id.verify_action:
                    if (activity.balance.deductVerifyInput()) {
                        deductionPerformed = true;
                        activity.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.dialogOverlay.open(R.layout.dialog_input_verify, activity);
                            }
                        }, 1000);
                    }
                    break;

                case R.id.hide_letters_action:
                    if (activity.balance.deductHideLettersInput()) {
                        deductionPerformed = true;
                        activity.keyboardView.hideUnusedLetters();
                    }
                    break;

                case R.id.play_sample_action:
                    if (activity.balance.deductSayName()) {
                        deductionPerformed = true;
                        activity.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // activity.player.play(activity.challengedInstrument.getVoiceAssetPath());
                            }
                        }, 1000);
                    }
                    break;
            }

            if (deductionPerformed) {
                activity.creditView.setText(Integer.toString(activity.balance.getCredit()));
            } else {
                activity.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.dialogOverlay.open(R.layout.dialog_no_credit, activity);
                    }
                }, 1000);
            }
            dialog.close();
        }
    }

    @SuppressWarnings("unused")
    private static class InputVerifyDialog extends GameDialog {
        private NameView responseView;
        private NameView suggestionView;

        public InputVerifyDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            super.onOpen(dialog, args);

            responseView = findViewById(R.id.input_verify_response);
            responseView.open(activity.nameView.getInput());

            suggestionView = findViewById(R.id.input_verify_suggestion);
            suggestionView.init(activity.challengedInstrument.getName());
            suggestionView.verify(activity.nameView.getInput());
        }
    }

    private static class GameDialog extends FrameLayout implements DialogOverlay.Content {
        protected DialogOverlay dialog;
        protected GameActivity activity;

        public GameDialog(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onOpen(DialogOverlay dialog, Object... args) {
            this.dialog = dialog;
            activity = (GameActivity) args[0];
            activity.keyboardView.disable();
            activity.nameView.disable();
        }

        @Override
        public void onClose() {
            activity.keyboardView.enable();
            activity.nameView.enable();
        }
    }
}
