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
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.quiz.instruments.Util.info;
import static com.kidscademy.quiz.instruments.Util.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayQuizTest {
    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void run() {
        App.storage().resetLevels();

        int LEVEL_SIZE = 10;

        Instrument[] instruments = App.storage().getInstruments();
        String[][] instrumentNames = new String[instruments.length / LEVEL_SIZE][LEVEL_SIZE];
        for (int i = 0; i < instruments.length; ++i) {
            instrumentNames[i / LEVEL_SIZE][i % LEVEL_SIZE] = instruments[i].getName().replace('_', ' ');
        }

        for (int i = 0; i < instrumentNames.length; ++i) {
            playQuiz(instrumentNames[i]);
        }
    }

    void playQuiz(String[] instrumentNames) {
        onView(withId(R.id.main_quiz)).perform(click());
        sleep(1000);

        onView(withTagValue(is((Object) "start-quiz"))).perform(click());
        sleep(1000);

        // quiz activity displays challenge instrument picture and couple options as buttons
        // one options has text the challenged instrument name
        // for every quiz searches for right option and click it

        for (int i = 0; i < instrumentNames.length; ++i) {
            info("Guess instrument %s", instrumentNames[i]);
            onView(withText(instrumentNames[i])).perform(click());
            sleep(2000);
        }

        // close dialog for quiz complete
        onView(allOf(withResourceName("fab_dialog_close"), isDisplayed())).perform(click());
    }
}
