package com.kidscademy.quiz.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.util.Assets;

import java.util.Random;

import js.util.Utils;

/**
 * Page background.
 *
 * @author Iulian Rotaru
 */
public class PageBackground extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final Random random = new Random();

    private Bitmap backgroundBitmap;
    private Rect backgroundRect;
    private RectF viewRect;
    private Paint backgroundPaint;
    private int maskColor;

    private Paint linePaint;
    private Paint lineGlowPaint;

    private ValueAnimator animator;
    private float slowOffset = random.nextFloat();
    private float quickOffset;

    private Paint circlePaint;
    private int xColor;
    private int yColor;

    public PageBackground(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageBackground(final Context context, AttributeSet attrs, int defAttrs) {
        super(context, attrs, defAttrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        backgroundRect = new Rect();
        viewRect = new RectF();

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        maskColor = ContextCompat.getColor(context, R.color.black_T20);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(2);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        //linePaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER));

        lineGlowPaint = new Paint();
        lineGlowPaint.set(linePaint);
        lineGlowPaint.setColor(Color.argb(235, 74, 138, 255));
        //lineGlowPaint.setColor(Color.argb(235, 255, 255, 255));
        lineGlowPaint.setStrokeWidth(6);
        lineGlowPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.OUTER));

        circlePaint = new Paint();
        //circlePaint.setColor(Color.argb(0, 0, 0, 0));
        circlePaint.setColor(Color.argb(255, 0, 255, 0));
        circlePaint.setDither(true);

        animator = new ValueAnimator();
        animator.setFloatValues(-0.2F, 1.2F);
        animator.setDuration(3000);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setInterpolator(new BounceInterpolator());
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(this);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                animator.setStartDelay(random.nextInt(2000));
                slowOffset = random.nextFloat();
                xColor = ContextCompat.getColor(context, Assets.getRandomColor());
                yColor = ContextCompat.getColor(context, Assets.getRandomColor());
            }
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int drawableResId = Assets.getPageBackgroundResId();

        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), drawableResId, boundsOptions);

        float scale = (float)Math.max(boundsOptions.outWidth, boundsOptions.outHeight) / (float)Math.max(w, h);

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = Utils.roundToSampleSize(scale);
//        Bitmap loadedBitmap = BitmapFactory.decodeResource(getResources(), drawableResId, decodeOptions);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), drawableResId, decodeOptions);

//        float scaledWidth = boundsOptions.outWidth / scale;
//        float scaledHeight = boundsOptions.outHeight / scale;
//        Bitmap scaledBitmap = Bitmap.createBitmap((int)scaledWidth, (int)scaledHeight, Bitmap.Config.ARGB_8888);
//
//        float ratioX = scaledWidth / loadedBitmap.getWidth();
//        float ratioY = scaledHeight / loadedBitmap.getHeight();
//        float middleX = scaledWidth / 2.0f;
//        float middleY = scaledHeight / 2.0f;
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
//        canvas.drawBitmap(loadedBitmap, middleX - loadedBitmap.getWidth() / 2, middleY - loadedBitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_bg1);
//        backgroundBitmap = scaledBitmap;

        int H = backgroundBitmap.getWidth() * h / w;
        backgroundRect.set(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        backgroundRect.set(0, 0, backgroundBitmap.getWidth(), H);

        viewRect.set(0, 0, w, h);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        quickOffset = (float) animator.getAnimatedValue();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        ScaleDrawable scale = new ScaleDrawable(backgroundDrawable, Gravity.CENTER | Gravity.CLIP_VERTICAL, 1, 1);
//        scale.setLevel(10000);
//        scale.setBounds(0, 0, getWidth(), getHeight());
//        scale.draw(canvas);

        //backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        //backgroundDrawable.draw(canvas);

        canvas.drawBitmap(backgroundBitmap, null, viewRect, backgroundPaint);

        canvas.drawColor(maskColor);

        //canvas.drawLine(0, 0, 400, 1200, lineGlowPaint);
        // canvas.drawLine(0, offset * canvas.getHeight(), canvas.getWidth(), offset * canvas.getHeight(), linePaint);

        int x = (int) (quickOffset * getWidth());
        int y = (int) (slowOffset * getHeight());
//        Rect rect = new Rect(x, y, x + 10, y + 10);
//        canvas.drawRect(rect, linePaint);
//
//        x = (int) (quickOffset * getWidth());
//        y = (int) (slowOffset * getHeight());
//        rect = new Rect(x, y, x + 10, y + 10);
//        canvas.drawRect(rect, linePaint);

        int radius = 16;

        Rect r = new Rect(x - radius, y - 4, x - 200, y + 4);
        LinearGradient linearGradient = new LinearGradient(r.left, r.top, r.right, r.bottom, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        circlePaint.setShader(linearGradient);
        canvas.drawRect(r, circlePaint);

        RadialGradient gradient = new RadialGradient(x, y, 20, 0xFF000000 | xColor, 0x00FFFFFF & xColor, Shader.TileMode.CLAMP);
        circlePaint.setShader(gradient);
        canvas.drawCircle(x, y, radius, circlePaint);


        x = (int) (slowOffset * getWidth());
        y = (int) (quickOffset * getHeight());

        r = new Rect(x - 4, y - radius, x + 4, y - 200);
        linearGradient = new LinearGradient(r.left, r.top, r.right, r.bottom, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        circlePaint.setShader(linearGradient);
        canvas.drawRect(r, circlePaint);

        gradient = new RadialGradient(x, y, radius, 0xFF000000 | yColor, 0x00FFFFFF & yColor, Shader.TileMode.CLAMP);
        circlePaint.setShader(gradient);
        canvas.drawCircle(x, y, radius, circlePaint);

    }
}
