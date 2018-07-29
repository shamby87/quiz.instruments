package com.kidscademy.quiz.instruments.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.kidscademy.quiz.instruments.util.Assets;

/**
 * Floating button with random background color.
 *
 * @author Iulian Rotaru
 */
public class RandomColorFAB extends android.support.design.widget.FloatingActionButton {
    public RandomColorFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Assets.getRandomColor())));
        // setRippleColor(Color.GREEN);
    }
}