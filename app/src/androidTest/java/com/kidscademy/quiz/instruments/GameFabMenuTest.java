package com.kidscademy.quiz.instruments;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GameFabMenuTest {

    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void launcherActivityTest3() {
        //sleep(4);

        info("Click on play game from main activity.");
        ViewInteraction playButton = onView(withId(R.id.main_play));
        playButton.check(matches(isDisplayed()));
        playButton.perform(click());

        info("Click on play from first card view from levels selector.");
        ViewInteraction levelButton = onView(
                allOf(withId(R.id.card_level_action),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        levelButton.perform(click());

        info("Open sub-items from FAB menu on game activity.");
        ViewInteraction fabMenu = onView(withId(R.id.game_fab_menu));
        fabMenu.perform(click());

        sleep(2);

        info("Press back from sub-items");
        ViewInteraction fabBack = onView(withId(R.id.game_fab_back));
        fabBack.perform(click());

//        ViewInteraction randomColorFAB2 = onView(
//                allOf(withId(R.id.game_fab_back),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.game),
//                                        3),
//                                4),
//                        isDisplayed()));
//        randomColorFAB2.perform(click());

        sleep(2);
        ViewInteraction randomColorFAB3 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.main_body),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        randomColorFAB3.perform(click());

        ViewInteraction hexaIcon3 = onView(
                allOf(withId(R.id.main_play),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        hexaIcon3.perform(click());

        ViewInteraction randomColorFAB4 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.main_body),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        randomColorFAB4.perform(click());

        ViewInteraction hexaIcon4 = onView(
                allOf(withId(R.id.main_quiz),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                1),
                        isDisplayed()));
        hexaIcon4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction randomColorFAB5 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.quiz_start),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        randomColorFAB5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction hexaIcon5 = onView(
                allOf(withId(R.id.main_score),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        hexaIcon5.perform(click());

        ViewInteraction randomColorFAB6 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.balance),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        randomColorFAB6.perform(click());

        ViewInteraction hexaIcon6 = onView(
                allOf(withId(R.id.main_play),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        hexaIcon6.perform(click());

        ViewInteraction hexaIcon7 = onView(
                allOf(withId(R.id.card_level_action),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        hexaIcon7.perform(click());

        ViewInteraction randomColorFAB7 = onView(
                allOf(withId(R.id.game_fab_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game),
                                        3),
                                0),
                        isDisplayed()));
        randomColorFAB7.perform(click());

        ViewInteraction randomColorFAB8 = onView(
                allOf(withId(R.id.game_fab_hint),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game),
                                        3),
                                1),
                        isDisplayed()));
        randomColorFAB8.perform(click());

        ViewInteraction hexaIcon8 = onView(
                allOf(withId(R.id.no_credit_action),
                        childAtPosition(
                                allOf(withId(R.id.quiz_start_icons),
                                        childAtPosition(
                                                withClassName(is("com.kidscademy.instruments.quiz.GameActivity$NoCreditDialog")),
                                                4)),
                                1),
                        isDisplayed()));
        hexaIcon8.perform(click());

        ViewInteraction randomColorFAB9 = onView(
                allOf(withId(R.id.fab_close),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz),
                                        3),
                                0),
                        isDisplayed()));
        randomColorFAB9.perform(click());

        ViewInteraction hexaIcon9 = onView(
                allOf(withId(R.id.main_score),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        hexaIcon9.perform(click());

        ViewInteraction randomColorFAB10 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                allOf(withId(R.id.balance),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        randomColorFAB10.perform(click());

        ViewInteraction hexaIcon10 = onView(
                allOf(withId(R.id.main_no_ads),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        hexaIcon10.perform(click());

        ViewInteraction randomColorFAB11 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        randomColorFAB11.perform(click());

        ViewInteraction hexaIcon11 = onView(
                allOf(withId(R.id.main_no_ads),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        hexaIcon11.perform(click());

        ViewInteraction randomColorFAB12 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        randomColorFAB12.perform(click());

        ViewInteraction hexaIcon12 = onView(
                allOf(withId(R.id.main_share),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                3),
                        isDisplayed()));
        hexaIcon12.perform(click());

        ViewInteraction randomColorFAB13 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        randomColorFAB13.perform(click());

        ViewInteraction hexaIcon13 = onView(
                allOf(withId(R.id.main_about),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                0),
                        isDisplayed()));
        hexaIcon13.perform(click());

        ViewInteraction randomColorFAB14 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        randomColorFAB14.perform(click());

        ViewInteraction hexaIcon14 = onView(
                allOf(withId(R.id.main_recommended),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                2),
                        isDisplayed()));
        hexaIcon14.perform(click());

        ViewInteraction randomColorFAB15 = onView(
                allOf(withId(R.id.fab_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        randomColorFAB15.perform(click());

    }

    private static void info(String message) {
        Log.i("OpenActivitiesStressTest", message);
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
