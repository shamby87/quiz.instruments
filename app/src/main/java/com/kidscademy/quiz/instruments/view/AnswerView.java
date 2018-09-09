package com.kidscademy.quiz.instruments.view;

import android.content.Context;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.instruments.model.GameAnswerBuilder;

import java.util.Locale;

import js.lang.BugError;
import js.util.Player;

/**
 * View specialized in displaying game answer. This view is initialized with expected answer value, see {@link #init(String)}.
 * If a letter view is unset invoke {@link OnAnswerLetterUnsetListener} with letter value.
 *
 * @author Iulian Rotaru
 */
public class AnswerView extends LinearLayout implements GameAnswerBuilder, OnClickListener {
    /**
     * Listener invoked when a letter is unset, that is, replaced with underscore.
     *
     * @author Iulian Rotaru
     */
    public static interface OnAnswerLetterUnsetListener {
        /**
         * Answer letter was unset, that is, replaced with underscore.
         *
         * @param letter letter value that was removed from answer display.
         */
        void onAnswerLetterUnset(char letter);
    }

    private LayoutInflater inflater;
    private Player player;
    private OnAnswerLetterUnsetListener listener;
    /**
     * Expected answer value prepared for internal use. Answer is stored upper case with underscore (_)
     * separator replaced by space.
     */
    private String expectedAnswer;
    private boolean disabled;

    public AnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // --------------------------------------------------------------------------------------------
    // GameAnswerBuilder Interface

    @Override
    public void addLetter(char letter) {
        // underscore is used for missing letters
        // scan for first underscore, i.e. missing letter, and put given letter there

        for (int i = 0; i < expectedAnswer.length(); ++i) {
            TextView view = (TextView) getChildAt(i);
            if (view.getText().equals("_")) {
                view.setText(Character.toString(letter));
                return;
            }
        }
        throw new BugError("Attempt to put letter after answer complete.");
    }

    @Override
    public boolean hasAllLetters() {
        for (int i = 0; i < expectedAnswer.length(); ++i) {
            if (((TextView) getChildAt(i)).getText().charAt(0) == '_') {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getFirstMissingLetterIndex() {
        int firstMissingCharIndex = 0;
        for (int i = 0; i < expectedAnswer.length(); ++i) {
            TextView textView = (TextView) getChildAt(i);
            if (textView.getText().charAt(0) == '_') {
                return firstMissingCharIndex;
            }
            if (textView.getText().charAt(0) != ' ') {
                ++firstMissingCharIndex;
            }
        }
        return -1;
    }

    @Override
    public String getValue() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < expectedAnswer.length(); ++i) {
            builder.append(((TextView) getChildAt(i)).getText());
        }
        return builder.toString();
    }

    // --------------------------------------------------------------------------------------------
    // OnClickListener Interface

    @Override
    public void onClick(View view) {
        if (disabled) {
            return;
        }

        // quick return if letter is not set
        char letter = ((TextView) view).getText().charAt(0);
        if (letter == ' ' || letter == '_') {
            return;
        }

        // unset answer letter and invoke letter unset listener
        ((TextView) view).setText("_");
        listener.onAnswerLetterUnset(letter);

        player.play("fx/click.mp3");
        if (App.prefs().isKeyVibrator()) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
        }
    }

    // --------------------------------------------------------------------------------------------
    // AnswerView Implementation

    /**
     * Register click listeners for all existing letter views. This hook is called by framework after
     * abswer view inflation is complete.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).setOnClickListener(this);
        }
    }

    /**
     * Initialize answer view with expected answer value. Takes care to init missing letter views if
     * given answer value is larger than current letter views number. Also, hide trailing letter views
     * if there are more that needed for expected answer.
     * <p>
     * Expected answer is not explicitly displayed but obfuscated, that is, replace answer characters with
     * underscores.
     *
     * @param expectedAnswer expected answer value.
     */
    public void init(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer.toUpperCase(Locale.getDefault());
        createMissingLeterViews(this.expectedAnswer.length());

        int i = 0;
        for (; i < this.expectedAnswer.length(); ++i) {
            final TextView view = (TextView) getChildAt(i);
            view.setText(expectedAnswer.charAt(i) == ' ' ? " " : "_");
            view.setVisibility(View.VISIBLE);
        }

        hideTrailingLetters(i);
    }

    public void setListener(OnAnswerLetterUnsetListener listener) {
        this.listener = listener;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }

    /**
     * Set value for this answer view. This setter replace existing answer value with the newly one, promoted
     * to upper case. Internal expected value is lost. Also takes care to reset letter views colors to default.
     *
     * @param value answer value.
     */
    public void setValue(String value) {
        createMissingLeterViews(value.length());
        int i = 0;
        for (; i < value.length(); ++i) {
            final TextView view = (TextView) getChildAt(i);
            view.setText(Character.toString(value.charAt(i)).toUpperCase());
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            view.setVisibility(View.VISIBLE);
        }
        hideTrailingLetters(i);
    }

    /**
     * Compare given answer value with internally stored expected answer and mark this view letters accordingly.
     *
     * @param answerValue answer value to verify.
     */
    public void verify(String answerValue) {
        for (int i = 0; i < answerValue.length(); ++i) {
            assert i < getChildCount();
            Character c = answerValue.charAt(i);
            if (c == '_') {
                continue;
            }
            TextView textView = (TextView) getChildAt(i);
            if (!c.equals(expectedAnswer.charAt(i))) {
                textView.setText(c.toString());
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red_900));
            } else {
                textView.setText("_");
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helpers

    /**
     * Ensure there are enough letter views to display answer of requested length.
     *
     * @param expectedLength answer expected length.
     */
    private void createMissingLeterViews(int expectedLength) {
        // ensure capacity
        for (int i = getChildCount(); i < expectedLength; ++i) {
            View view = inflater.inflate(R.layout.compo_letter, this, false);
            addView(view);
            view.setOnClickListener(this);
        }
    }

    /**
     * Hide letter views that are not used by current answer. Answer view does not destroy letter views not
     * used by current answer. It hides them in order to be reused if next answer will be longer.
     *
     * @param currentAnswerLength current answer length.
     */
    private void hideTrailingLetters(int currentAnswerLength) {
        for (int i = currentAnswerLength; i < getChildCount(); ++i) {
            getChildAt(i).setVisibility(View.GONE);
        }
    }
}
