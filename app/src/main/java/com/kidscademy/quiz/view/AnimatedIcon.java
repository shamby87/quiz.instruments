package com.kidscademy.quiz.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kidscademy.quiz.instruments.R;

import java.util.Random;

/**
 * Animated icon.
 *
 * @author Iulian Rotaru
 */
public class AnimatedIcon extends AppCompatImageView implements Runnable {
    private static final Random random = new Random();

    private int animationResId;
    private int startOffset;

    private Handler handler;
    private Animation animation;

    public AnimatedIcon(Context context) {
        super(context);
        init(null, 0);
    }

    public AnimatedIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AnimatedIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        int animationResId = 0;

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnimatedIcon, defStyle, 0);
        try {
            animationResId = a.getResourceId(R.styleable.AnimatedIcon_iconAnimation, 0);
        } finally {
            a.recycle();
        }

        if (animationResId != 0) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.vibrate_icon);

            handler = new Handler();
            handler.postDelayed(this, 2000);
        }
    }

    @Override
    public void run() {
        startAnimation(animation);
        handler.postDelayed(this, 2000 + random.nextInt(3000));
    }
}
