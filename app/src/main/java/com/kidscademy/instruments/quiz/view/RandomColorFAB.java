package com.kidscademy.instruments.quiz.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.kidscademy.instruments.quiz.util.Assets;

public class RandomColorFAB extends android.support.design.widget.FloatingActionButton {
    public RandomColorFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Assets.getRandomColor())));
        // setRippleColor(Color.GREEN);
    }
}