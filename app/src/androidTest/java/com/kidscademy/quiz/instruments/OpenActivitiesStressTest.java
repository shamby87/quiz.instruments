package com.kidscademy.quiz.instruments;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.kidscademy.quiz.instruments.Util.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenActivitiesStressTest {
    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void run() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 100; ++i) {
            Log.i("OpenActivitiesStressTest", "Test #" + i);
            openActivities();
        }
    }

    void openActivities() {
        ViewInteraction hexaIcon = onView(
                allOf(withId(R.id.main_no_ads),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        hexaIcon.perform(click());

        ViewInteraction randomColorFAB = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        randomColorFAB.perform(click());

        ViewInteraction hexaIcon2 = onView(
                allOf(withId(R.id.main_share),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                3),
                        isDisplayed()));
        hexaIcon2.perform(click());

        ViewInteraction randomColorFAB2 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        randomColorFAB2.perform(click());

        ViewInteraction hexaIcon3 = onView(
                allOf(withId(R.id.main_about),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                0),
                        isDisplayed()));
        hexaIcon3.perform(click());

        ViewInteraction randomColorFAB3 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        randomColorFAB3.perform(click());

        ViewInteraction hexaIcon4 = onView(
                allOf(withId(R.id.main_recommended),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                2),
                        isDisplayed()));
        hexaIcon4.perform(click());

        ViewInteraction randomColorFAB4 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        randomColorFAB4.perform(click());

        ViewInteraction hexaIcon5 = onView(
                allOf(withId(R.id.main_play),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        hexaIcon5.perform(click());

        ViewInteraction randomColorFAB5 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.main_body),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        randomColorFAB5.perform(click());

        ViewInteraction hexaIcon6 = onView(
                allOf(withId(R.id.main_quiz),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                1),
                        isDisplayed()));
        hexaIcon6.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction randomColorFAB6 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.quiz_start),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        randomColorFAB6.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction hexaIcon7 = onView(
                allOf(withId(R.id.main_balance),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        hexaIcon7.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction randomColorFAB7 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.balance),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        randomColorFAB7.perform(click());

    }
}
