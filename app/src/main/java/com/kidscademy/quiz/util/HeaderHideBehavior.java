package com.kidscademy.quiz.instruments.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Hide page header when outer scroller starts moving up.
 *
 * @author Iulian Rotaru
 */
public class HeaderHideBehavior extends CoordinatorLayout.Behavior<FrameLayout> {
    private boolean headerVisible = true;

    public HeaderHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FrameLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FrameLayout child, @NonNull View nestedScroll, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, nestedScroll, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        // child argument is the header that has behavior attached

        if (nestedScroll.getScrollY() < 100) {
            if (!headerVisible) {
                headerVisible = true;
                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                child.startAnimation(animation);
            }
        } else {
            if (headerVisible) {
                headerVisible = false;
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                animation.setDuration(600);
                animation.setFillAfter(true);
                child.startAnimation(animation);
            }
        }
    }
}
