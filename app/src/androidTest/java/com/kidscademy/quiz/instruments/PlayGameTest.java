package com.kidscademy.quiz.instruments;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.view.HexaIcon;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static com.kidscademy.quiz.instruments.Util.key;
import static com.kidscademy.quiz.instruments.Util.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayGameTest {
    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void run() {
        App.storage().resetLevels();

        int LEVEL_SIZE = 10;

        Instrument[] instruments = App.storage().getInstruments();
        String[][] instrumentNames = new String[instruments.length / LEVEL_SIZE][LEVEL_SIZE];
        for (int i = 0; i < instruments.length; ++i) {
            instrumentNames[i / LEVEL_SIZE][i % LEVEL_SIZE] = instruments[i].getLocaleName().toUpperCase().replaceAll(" ", "");
        }

        for (int levelIndex = 0; levelIndex < instrumentNames.length; ++levelIndex) {
            playLevel(levelIndex, instrumentNames[levelIndex]);
            // wait for main activity to be displayed
            sleep(1000);
        }
    }

    void playLevel(int level, String[] instrumentNames) {
        info("Play level %d", level);
        onView(withId(R.id.main_play)).perform(click());
        sleep(1000);

        ViewInteraction recycle = onView(withId(R.id.levels));
        recycle.perform(RecyclerViewActions.actionOnItemAtPosition(level, click()));

        ViewInteraction levelButton = onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withTagValue(is((Object) ("level" + level))))));
        levelButton.perform(click());
        sleep(1000);

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
        if (level == 9) {
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
}
