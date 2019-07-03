package com.kidscademy.quiz.instruments;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenActivitiesStressTest extends AppConformanceTest {
    @Test
    public void run() {
        for (int i = 0; i < 100; ++i) {
            Log.i("OpenActivitiesStressTest", "Test #" + i);
            openActivities();
        }
    }
}
