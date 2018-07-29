package com.kidscademy.quiz.instruments.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;

/**
 * Hexagonal icon.
 *
 * @author Iulian Rotaru
 */
public class HexaIcon extends View implements ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    private static final float DEF_ICON_RELATIVE_SIZE = 0.6F;
    private static final int ANIMATION_DURATION = 300;

    private int borderSize;
    private int iconColor;
    private Drawable iconDrawable;

    private Paint backgroundPaint;
    private Paint borderPaint;
    private float iconRelativeSize = DEF_ICON_RELATIVE_SIZE;
    private ValueAnimator iconCollapseAnimator;
    private OnClickListener clickListener;

    public HexaIcon(Context context, AttributeSet attrs) {
        super(context, attrs);

        int backgroundColor = 0;
        int backgroundAlpha = 0;
        int borderColor = 0;
        int iconDrawableId = 0;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HexaIcon, 0, 0);
        try {
            backgroundColor = a.getColor(R.styleable.HexaIcon_backgroundColor, backgroundColor);
            backgroundAlpha = a.getInt(R.styleable.HexaIcon_backgroundAlpha, 128);
            borderSize = a.getInt(R.styleable.HexaIcon_borderSize, 0);
            borderColor = a.getColor(R.styleable.HexaIcon_borderColor, Color.WHITE);
            iconColor = a.getColor(R.styleable.HexaIcon_iconColor, Color.WHITE);
            iconDrawableId = a.getResourceId(R.styleable.HexaIcon_iconDrawable, 0);
        } finally {
            a.recycle();
        }

        if (iconDrawableId != 0) {
            iconDrawable = VectorDrawableCompat.create(getResources(), iconDrawableId, null);
        }

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAlpha(backgroundAlpha);

        if (borderSize != 0) {
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderSize);
            borderPaint.setColor(borderColor);
        }

        iconCollapseAnimator = ValueAnimator.ofFloat(DEF_ICON_RELATIVE_SIZE, 0);
        iconCollapseAnimator.setDuration(ANIMATION_DURATION);
        iconCollapseAnimator.addUpdateListener(this);
        iconCollapseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                HexaIcon.this.iconRelativeSize = DEF_ICON_RELATIVE_SIZE;
                if (clickListener != null) {
                    clickListener.onClick(HexaIcon.this);
                }
            }
        });

        super.setOnClickListener(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        iconRelativeSize = (float) animator.getAnimatedValue();
        invalidate();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        if (iconDrawable == null) {
            return;
        }
        if (App.prefs().isKeyVibrator()) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
        }
        iconCollapseAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int contentDimension = Math.min(contentWidth, contentHeight);

        float[][] hexaVertices = getHexaVertices(contentDimension);
        canvas.drawPath(path(hexaVertices), backgroundPaint);
        if (borderPaint != null) {
            float scaleFactor = (float) (contentDimension - borderSize) / contentDimension;
            canvas.drawPath(path(scale(hexaVertices, contentDimension, scaleFactor)), borderPaint);
        }

        int iconDimension = (int) (iconRelativeSize * contentDimension);
        int x1 = (contentWidth - iconDimension) / 2;
        int y1 = (contentHeight - iconDimension) / 2;
        int x2 = x1 + iconDimension;
        int y2 = y1 + iconDimension;

        if (iconDrawable != null) {
            iconDrawable.setBounds(x1, y1, x2, y2);
            DrawableCompat.setTint(iconDrawable, iconColor);
            iconDrawable.draw(canvas);
        }
    }

    private float[][] getHexaVertices(float dimension) {
        float[][] vertices = new float[6][2];

        float dx = 0.25F * dimension;
        float dy = 0.433F * dimension;

        vertices[0][0] = dx;
        vertices[0][1] = dimension / 2 - dy;
        vertices[1][0] = 0;
        vertices[1][1] = dimension / 2;
        vertices[2][0] = vertices[0][0];
        vertices[2][1] = dimension / 2 + dy;
        vertices[3][0] = dimension - dx;
        vertices[3][1] = vertices[2][1];
        vertices[4][0] = dimension;
        vertices[4][1] = dimension / 2;
        vertices[5][0] = vertices[3][0];
        vertices[5][1] = vertices[0][1];

        return vertices;
    }

    private static float[][] scale(float[][] vertices, float dimension, float scaleFactor) {
        float cx = dimension / 2;
        float cy = dimension / 2;
        for (int i = 0; i < vertices.length; ++i) {
            vertices[i][0] = cx + scaleFactor * (vertices[i][0] - cx);
            vertices[i][1] = cy + scaleFactor * (vertices[i][1] - cy);
        }
        return vertices;
    }

    private static Path path(float[][] vertices) {
        Path path = new Path();
        path.moveTo(vertices[0][0], vertices[0][1]);
        for (int i = 1; i < vertices.length; ++i) {
            path.lineTo(vertices[i][0], vertices[i][1]);
        }
        path.close();
        return path;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        // it seems alpha is not preserved when set color
        int alpha = backgroundPaint.getAlpha();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAlpha(alpha);
        invalidate();
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
        invalidate();
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        invalidate();
    }
}
