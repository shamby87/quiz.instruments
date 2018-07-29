package com.kidscademy.quiz.instruments.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.instruments.util.Assets;

import java.util.Random;

import js.util.Utils;

/**
 * Animated background with two crossing dots.
 *
 * @author Iulian Rotaru
 */
public class MovingDotsBackground extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final Random random = new Random();

    private int maskColor;
    private int dotsSize;
    private int tailColor;
    private int tailLength;
    private int tailWidth;

    private Paint dotsPaint;
    private int[] dotColors = new int[2];
    private Rect tailRect;

    private ValueAnimator animator;
    private float slowOffset = random.nextFloat();
    private float quickOffset;

    public MovingDotsBackground(Context context) {
        super(context);
        init(null, 0);
    }

    public MovingDotsBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MovingDotsBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (Build.VERSION.SDK_INT < 23) {
            // missing liniar gradient support for hardware accelerator on api levels less than 23
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MovingDotsBackground, defStyle, 0);
        try {
            maskColor = a.getColor(R.styleable.MovingDotsBackground_movingDotsMaskColor, ContextCompat.getColor(getContext(), R.color.black_T20));
            dotsSize = a.getDimensionPixelSize(R.styleable.MovingDotsBackground_movingDotsSize, dp2px(8));
            tailColor = a.getColor(R.styleable.MovingDotsBackground_movingDotsTailColor, ContextCompat.getColor(getContext(), R.color.grey_300));
            tailLength = a.getDimensionPixelSize(R.styleable.MovingDotsBackground_movingDotsTailLength, dp2px(80));
            tailWidth = a.getDimensionPixelSize(R.styleable.MovingDotsBackground_movingDotsTailWidth, dp2px(4));
        } finally {
            a.recycle();
        }

        dotsPaint = new Paint();
        dotsPaint.setColor(Color.argb(255, 0, 255, 0));
        dotsPaint.setDither(true);

        tailRect = new Rect();

        animator = new ValueAnimator();
        animator.setFloatValues(0, 1);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        //animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(this);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                animator.setStartDelay(random.nextInt(2000));
                slowOffset = random.nextFloat();
                for (int i = 0; i < dotColors.length; ++i) {
                    dotColors[i] = ContextCompat.getColor(getContext(), Assets.getRandomColor());
                }
            }
        });
        animator.start();
    }

    private int dp2px(int dp) {
        return (int) Utils.dp2px(getContext(), dp);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        quickOffset = (float) animator.getAnimatedValue();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(maskColor);

        int w = getWidth() - getPaddingLeft() - getPaddingRight();
        int h = getHeight() - getPaddingTop() - getPaddingBottom();

        int x = (int) (quickOffset * w);
        int y = (int) (slowOffset * h);
        if (tailLength > 0) {
            tailRect.set(x - dotsSize, y - tailWidth / 2, x - tailLength, y + tailWidth / 2);
            drawTail(canvas);
        }
        drawDot(canvas, x, y, dotColors[0]);

        x = (int) (slowOffset * w);
        y = (int) (quickOffset * h);
        if (tailLength > 0) {
            tailRect.set(x - tailWidth / 2, y - dotsSize, x + tailWidth / 2, y - tailLength);
            drawTail(canvas);
        }
        drawDot(canvas, x, y, dotColors[1]);
    }

    private void drawDot(Canvas canvas, int x, int y, int color) {
        RadialGradient gradient = new RadialGradient(x, y, dotsSize, 0xFF000000 | color, 0x00FFFFFF & color, Shader.TileMode.CLAMP);
        dotsPaint.setShader(gradient);
        canvas.drawCircle(x, y, dotsSize, dotsPaint);
    }

    private void drawTail(Canvas canvas) {
        LinearGradient linearGradient = new LinearGradient(tailRect.left, tailRect.top, tailRect.right, tailRect.bottom, 0xFF000000 | tailColor, 0x00FFFFFF & tailColor, Shader.TileMode.CLAMP);
        dotsPaint.setShader(linearGradient);
        canvas.drawRect(tailRect, dotsPaint);
    }

    public int getDotsSize() {
        return dotsSize;
    }

    public void setDotsSize(int dotsSize) {
        this.dotsSize = dotsSize;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }
}
