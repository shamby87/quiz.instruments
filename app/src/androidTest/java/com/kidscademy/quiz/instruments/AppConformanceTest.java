package com.kidscademy.quiz.instruments;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.view.HexaIcon;

import org.junit.Before;
import org.junit.BeforeClass;
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
import static com.kidscademy.quiz.instruments.Util.waitView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * User interface tests for overall activities navigation, play first level game and first quiz.
 *
 * @author Iulian Rotaru
 */
@SuppressWarnings("HardCodedStringLiteral")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppConformanceTest {
    private static final int LEVEL_SIZE = 10;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context context = InstrumentationRegistry.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            return intent;
        }
    };

    @Before
    public void beforeTest() {
        // ensure that always start with the first level
        App.storage().resetLevels();
    }

    @Test
    public void openActivities() {
        // ------------------------------------------------
        // game activity

        // open levels list
        onView(withId(R.id.main_play)).perform(click());

        // go back to main activity directly from levels list, that is, abort game
        sleep(200);
        onView(withId(R.id.fab_back)).perform(click());

        // open levels list in order to go to game activity
        onView(withId(R.id.main_play)).perform(click());

        // levels list is opened; click on first level
        onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withTagValue(is((Object) "level0"))))).perform(click());

        // game activity is opened: click on FAB menu to open its mini FAB items
        onView(withId(R.id.game_fab_menu)).perform(click());

        // FAB menu items are opend: click to open hint dialog
        onView(withId(R.id.game_fab_hint)).perform(click());

        // close hint dialog and reveal game activity with FAB menu opened
        onView(withId(R.id.fab_dialog_close)).perform(click());

        // FAB menu items are opend: click to open instruments grid activity
        sleep(200);
        onView(withId(R.id.game_fab_view_grid)).perform(click());

        // close instruments grid and go back to game activity
        sleep(200); // grid activity takes longer to display
        onView(withId(R.id.fab_back)).perform(click());

        // FAB menu items are opend: skip to next instrument; FAB menu remains opened
        onView(withId(R.id.game_fab_skip_next)).perform(click());

        // FAB menu items are opend: click on back
        onView(withId(R.id.game_fab_back)).perform(click());

        // close levels list and go back to main activity
        waitView(withResourceName("fab_back")).perform(click());

        // ------------------------------------------------
        // quiz activity

        // main activit is opened: click to open quiz start activity
        onView(withId(R.id.main_quiz)).perform(click());

        // go back to main activity directly from quiz start, that is, abort quiz
        waitView(withResourceName("fab_back")).perform(click());

        // main activit is opened: click to open quiz start activity
        onView(withId(R.id.main_quiz)).perform(click());

        // quiz start is opened: locate hexagon with play icon and click it to start quiz
        onView(withTagValue(is((Object) "start-quiz"))).perform(click());

        // close quiz and go back to main activity
        waitView(withResourceName("fab_close")).perform(click());

        // ------------------------------------------------
        // balance activity

        // main activit is opened: click to open balance activity
        onView(withId(R.id.main_balance)).perform(click());

        // close balance and go back to main activity
        waitView(withResourceName("fab_back")).perform(click());

        // ------------------------------------------------
        // auxiliare activities

        // main activit is opened: click to open about activity
        onView(withId(R.id.main_about)).perform(click());

        // close about activity and go back to main activity
        waitView(withResourceName("fab_back")).perform(click());

        // main activit is opened: click to open no ads activity
        onView(withId(R.id.main_no_ads)).perform(click());

        // close no ads activity and go back to main activity
        waitView(withResourceName("fab_back")).perform(click());

        // main activit is opened: click to open share activity
        onView(withId(R.id.main_share)).perform(click());

        // close share activity and go back to main activity
        waitView(withResourceName("fab_back")).perform(click());

        // main activity is opened: click to open recommended application activity
//        onView(withId(R.id.main_recommended)).perform(click());

        // close recommended application activity and go back to main activity
//        waitView(withResourceName("fab_back")).perform(click());

        // back to main activity: end test case
    }

    @Test
    public void playGameLevelOne() {
        // click on game play action from main menu then select first level
        onView(allOf(withId(R.id.main_play), isDisplayed())).perform(click());
        onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withTagValue(is((Object) "level0"))))).perform(click());

        Instrument[] instruments = App.storage().getInstruments();
        for (int n = 0; n < LEVEL_SIZE; ++n) {
            final Instrument instrument = instruments[n];

            // wait for instrument picture to be displayed signaling that quiz is loaded
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            // enter all letters from instrument name, one by one
            String instrumentName = instrument.getLocaleName().toUpperCase().replaceAll(" ", "");
            for (int c = 0; c < instrumentName.length(); ++c) {
                onView(key(Character.toString(instrumentName.charAt(c)))).perform(click());
            }

            // next level unlocked dialog is displayed after 5 instruments resolved, that is, index 4
            if (n == 4) {
                // close dialog for level 2 unlocked
                waitView(withResourceName("fab_dialog_close")).perform(click());
            }
        }

        // close dialog for level complete then back to main menu activity
        waitView(withResourceName("fab_dialog_close")).perform(click());
        waitView(withResourceName("fab_back")).perform(click());
    }

    @Test
    public void playFirstQuiz() {
        // click on quiz action from main menu activity then start quiz session
        onView(withId(R.id.main_quiz)).perform(click());
        onView(withTagValue(is((Object) "start-quiz"))).perform(click());

        // quiz activity displays challenge instrument picture and couple options as buttons
        // one options has text the challenged instrument name
        // for every quiz searches for right option and click it

        Instrument[] instruments = App.storage().getInstruments();
        for (int i = 0; i < LEVEL_SIZE; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));
            onView(withText(instrument.getLocaleName().toLowerCase())).perform(click());
        }

        // close dialog for quiz complete
        waitView(withResourceName("fab_dialog_close")).perform(click());
    }
}
