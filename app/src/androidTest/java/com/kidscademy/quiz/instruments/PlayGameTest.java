package com.kidscademy.quiz.instruments;


import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.view.HexaIcon;
import com.kidscademy.quiz.instruments.view.KeyboardView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayGameTest {

    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void playGame() {
        App.storage().resetLevels();

        int LEVEL_SIZE = 10;

        Instrument[] instruments = App.storage().getInstruments();
        String[][] instrumentNames = new String[instruments.length / LEVEL_SIZE][LEVEL_SIZE];
        for (int i = 0; i < instruments.length; ++i) {
            instrumentNames[i / LEVEL_SIZE][i % LEVEL_SIZE] = instruments[i].getName().toUpperCase().replaceAll("_", "");
        }

        for (int levelIndex = 0; levelIndex < instrumentNames.length; ++levelIndex) {
            playLevel(levelIndex, instrumentNames[levelIndex]);
        }
    }

    private void playLevel(int level, String[] instrumentNames) {
        info("Play level %d", level);
        onView(withId(R.id.main_play)).perform(click());

        ViewInteraction recycle = onView(withId(R.id.levels));
        recycle.perform(RecyclerViewActions.actionOnItemAtPosition(level, click()));

        ViewInteraction levelButton = onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withText("LEVEL " + (level + 1)))));
        levelButton.perform(click());

        for (int i = 0; i < instrumentNames.length; ++i) {
            guessInstrument(instrumentNames[i]);
            sleep(2000);

            // dialog for next level unlocked is not displayed for last level, that is, 9 since in this context level is zero based
            if (level < 9 && i == 5) {
                onView(allOf(withResourceName("fab_dialog_close"), isDisplayed())).perform(click());
                sleep(2000);
            }
        }

        // level complete is signaled by opening a dialog, less last level that moves to game over activity
        if(level == 9) {
            return;
        }

        // close dialog for level complete
        onView(allOf(withResourceName("fab_dialog_close"), isDisplayed())).perform(click());
        sleep(1000);

        // at this point we are back to levels list activity
        // go back to main activity but first move to top to ensure FAB is displayed
        recycle.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(withResourceName("fab_back"), isDisplayed())).perform(click());
    }

    private static void guessInstrument(String instrumentName) {
        info("Guess instrument %s", instrumentName);
        for (int i = 0; i < instrumentName.length(); ++i) {
            onView(key(Character.toString(instrumentName.charAt(i)))).perform(click());
        }
    }

    private static Activity getActivity() {
        final Activity[] currentActivity = {null};

        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }

    private static Matcher<View> key(String key) {
        final Matcher<String> stringMatcher = is(key);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            private boolean keyfound = false;

            @Override
            public void describeTo(Description description) {
                description.appendText("with text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                if (keyfound) {
                    return false;
                }
                if (textView.getVisibility() != View.VISIBLE) {
                    return false;
                }
                ViewParent parent = textView.getParent();
                if (!(parent instanceof KeyboardView)) {
                    return false;
                }
                if (stringMatcher.matches(textView.getText().toString())) {
                    keyfound = true;
                    return true;
                }
                return false;
            }
        };
    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private static void info(String message, Object... args) {
        Log.i("PlayGameTest", String.format(message, args));
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
