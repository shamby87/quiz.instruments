package com.kidscademy.quiz.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.kidscademy.quiz.app.App;

import java.util.List;

import js.lang.BugError;

public class QuizOptionsView extends GridLayout implements View.OnClickListener {
    private Button[] buttons;
    private int buttonTextColor;
    private Listener listener;
    /**
     * Filter out multiple clicks. After options initialization via {@link #init(List)}} only first click is accepted.
     */
    private int clicksCount;

    public QuizOptionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getOptionsCount() {
        return buttons.length;
    }

    public void clear() {
        for (Button button : buttons) {
            button.setTextColor(buttonTextColor);
        }
    }

    public void init(List<String> options) {
        if (options.size() != buttons.length) {
            throw new BugError("Options count doe snot match buttons count.");
        }
        clicksCount = 0;
        for (int i = 0; i < buttons.length; ++i) {
            buttons[i].setText(options.get(i));
        }
    }

    public void highlightOption(String option, final Runnable callback) {
        for (final Button button : buttons) {
            if (button.getText().toString().equals(option)) {
                ObjectAnimator anim = ObjectAnimator.ofInt(button, "textColor", Color.TRANSPARENT, Color.WHITE);
                anim.setDuration(500);
                anim.setEvaluator(new ArgbEvaluator());
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.setRepeatCount(4);
                anim.start();

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        callback.run();
                    }
                });
                break;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        buttons = new Button[this.getChildCount()];
        for (int i = 0; i < buttons.length; ++i) {
            buttons[i] = (Button) this.getChildAt(i);
            buttons[i].setOnClickListener(this);
        }

        ColorStateList buttonsColorStateList = buttons[0].getTextColors();
        buttonTextColor = buttonsColorStateList.getDefaultColor();
    }

    @Override
    public void onClick(View view) {
        if (clicksCount > 0) {
            return;
        }
        clicksCount++;

        if (App.instance().preferences().isKeyVibrator()) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
        }

        // selected option text is generated and also checked by quiz engine
        // since display language is not an issue we can use user interface text
        listener.onQuizOptionSelected(((Button) view).getText().toString());
    }

    public interface Listener {
        void onQuizOptionSelected(String option);
    }
}
