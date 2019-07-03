package com.kidscademy.quiz.instruments;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kidscademy.quiz.activity.MainActivity;
import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Storage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.quiz.instruments.Util.info;
import static com.kidscademy.quiz.instruments.Util.sleep;
import static com.kidscademy.quiz.instruments.Util.waitView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayQuizTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void run() {
        Storage storage = App.instance().storage();
        storage.resetLevels();

        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < instruments.length; i += 10) {
            playQuiz(Arrays.copyOfRange(instruments, i, i + 10, Instrument[].class));
            sleep(1000);
        }
    }

    void playQuiz(Instrument[] instruments) {
        // click on quiz action then press on quiz start
        onView(withId(R.id.main_quiz)).perform(click());
        waitView(withTagValue(is((Object) "start-quiz"))).perform(click());

        // quiz activity displays challenge instrument picture and couple options as buttons
        // one options has text the challenged instrument name
        // for every quiz searches for right option and click it

        for (int i = 0; i < 10; ++i) {
            final Instrument instrument = instruments[i];
            info("Guess instrument %s", instrument.getName());

            // wait for instrument picture to be displayed signaling that quiz is loaded
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            // check solved challenges count
            onView(withId(R.id.quiz_solved)).check(matches(withText(Integer.toString(i))));

            // click on option that matches instrument name
            onView(withText(instrument.getLocaleName())).perform(click());

            // check selected option is displayed on quiz name view
            onView(withId(R.id.quiz_name)).check(matches(withText(instrument.getLocaleName())));
        }

        // close dialog for quiz complete and wait for main menu activity to load
        waitView(withResourceName("fab_dialog_close")).perform(click());
        waitView(withId(R.id.main_quiz)).check(matches(isDisplayed()));
    }
}
