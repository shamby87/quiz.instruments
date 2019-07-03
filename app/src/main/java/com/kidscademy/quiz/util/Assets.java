package com.kidscademy.quiz.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.kidscademy.quiz.instruments.R;

import java.util.Random;

/**
 * Utility class for application media assets.
 *
 * @author Iulian Rotaru
 */
public class Assets {
    private static Random random = new Random();

    private final static int[] pageBackgroundIds = new int[]{
            R.drawable.page_bg1,
            R.drawable.page_bg2,
            R.drawable.page_bg3,
            R.drawable.page_bg4,
            R.drawable.page_bg5,
            R.drawable.page_bg6,
            R.drawable.page_bg7,
            R.drawable.page_bg8,
            R.drawable.page_bg9,
            R.drawable.page_bg10
    };

    public static int getPageBackgroundResId() {
        return pageBackgroundIds[random.nextInt(pageBackgroundIds.length)];
    }

    public static Drawable getPageBackground(Context context) {
        return ContextCompat.getDrawable(context, getPageBackgroundResId());
    }

    private static final int[] levelBackgroundIds = new int[]{
            R.drawable.level_bg1,
            R.drawable.level_bg2,
            R.drawable.level_bg3,
            R.drawable.level_bg4,
            R.drawable.level_bg5,
            R.drawable.level_bg6,
            R.drawable.level_bg7,
            R.drawable.level_bg8
    };

    public static int getLevelBackgroundId(int position) {
        return levelBackgroundIds[position % levelBackgroundIds.length];
    }

    private static final int[] colors = new int[]{
            R.color.red_300,
            R.color.pink_300,
            R.color.purple_300,
            R.color.indigo_300,
            R.color.blue_300,
            R.color.cyan_300,
            R.color.teal_300,
            R.color.green_300,
            R.color.lime_300,
            R.color.amber_300,
            R.color.orange_300,
            R.color.brown_300
    };

    public static int getColor(int position) {
        return colors[position % colors.length];
    }

    public static int getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

    private static final int[] progresses = new int[]{
            R.drawable.level_progress_red,
            R.drawable.level_progress_pink,
            R.drawable.level_progress_purple,
            R.drawable.level_progress_indigo,
            R.drawable.level_progress_blue,
            R.drawable.level_progress_cyan,
            R.drawable.level_progress_teal,
            R.drawable.level_progress_green,
            R.drawable.level_progress_lime,
            R.drawable.level_progress_amber,
            R.drawable.level_progress_orange,
            R.drawable.level_progress_brown
    };

    public static Drawable getProgressDrawable(Context context, int position) {
        return context.getResources().getDrawable(progresses[position % progresses.length]);
    }

    /**
     * Get name of the game level identified by zero based level index.
     *
     * @param context    android context,
     * @param levelIndex zero based level index.
     * @return level name.
     */
    public static String getLevelName(Context context, int levelIndex) {
        String[] levelNames = context.getResources().getStringArray(R.array.level_names);
        return levelNames[levelIndex % levelNames.length];
    }
}
