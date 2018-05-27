package com.kidscademy.instruments.quiz.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.kidscademy.instruments.quiz.R;

import java.text.NumberFormat;
import java.util.Locale;

import js.util.Utils;

/**
 * TODO: document your custom view class.
 */
public class PercentDonutView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final int ANIM_START_DELAY = 2000;
    private static final int ANIM_DURATION = 2000;

    private float percent;
    private int backgroundColor;
    private int strokeColor;
    private float strokeWidth;
    private int textColor;
    private float textSize;

    private final Paint backgroundPaint = new Paint();
    private final Paint strokePaint = new Paint();
    private final TextPaint textPaint = new TextPaint();

    private final RectF donutDimension = new RectF();
    private final RectF textDimension = new RectF();

    private final ValueAnimator animator = new ValueAnimator();
    private final NumberFormat format = NumberFormat.getPercentInstance(Locale.getDefault());

    public PercentDonutView(Context context) {
        super(context);
        init(null, 0);
    }

    public PercentDonutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PercentDonutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PercentDonutView, defStyle, 0);

        try {
            percent = a.getFloat(R.styleable.PercentDonutView_donutPercent, 0);
            backgroundColor = a.getColor(R.styleable.PercentDonutView_donutBackgroundColor, backgroundColor);
            strokeColor = a.getColor(R.styleable.PercentDonutView_donutStrokeColor, strokeColor);
            strokeWidth = a.getDimension(R.styleable.PercentDonutView_donutStrokeWidth, strokeWidth);
            textColor = a.getColor(R.styleable.PercentDonutView_donutTextColor, Color.WHITE);
            textSize = a.getDimension(R.styleable.PercentDonutView_donutTextSize, Utils.dp2px(getContext(), 20));
        } finally {
            a.recycle();
        }

        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeWidth(strokeWidth / 3);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);

        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        animator.setStartDelay(ANIM_START_DELAY);
        animator.setDuration(ANIM_DURATION);
        //animator.setInterpolator(new LinearInterpolator());
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(this);
    }

    public void setPercent(float percent) {
        this.percent = 0;
        animator.setFloatValues(0, percent);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        percent = (float) animator.getAnimatedValue();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float offset = strokeWidth / 2;
        donutDimension.set(offset, offset, w - offset, h - offset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(donutDimension, 0, 360, false, backgroundPaint);
        canvas.drawArc(donutDimension, -90, percent * 360, false, strokePaint);

        final float textHeight = textPaint.descent() - textPaint.ascent();
        final float textOffset = (textHeight / 2) - textPaint.descent();
        textDimension.set(0, 0, getWidth(), getHeight());
        canvas.drawText(format.format(percent), textDimension.centerX(), textDimension.centerY() + textOffset, textPaint);
    }

    public float getDonutPercent() {
        return percent;
    }

    public void setDonutPercent(float percent) {
        this.percent = percent;
    }

    public int getDonutBackgroundColor() {
        return backgroundColor;
    }

    public void setDonutBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getDonutStrokeColor() {
        return strokeColor;
    }

    public void setDonutStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getDonutStrokeWidth() {
        return strokeWidth;
    }

    public void setDonutStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getDonutTextColor() {
        return textColor;
    }

    public void setDonutTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getDonutTextSize() {
        return textSize;
    }

    public void setDonutTextSize(float textSize) {
        this.textSize = textSize;
    }
}
