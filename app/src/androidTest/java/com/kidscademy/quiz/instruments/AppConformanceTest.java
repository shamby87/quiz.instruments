package com.kidscademy.quiz.instruments;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
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

import js.lang.Callback;

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

/**
 * User interface tests for overall activities navigation, play first level game and first quiz.
 *
 * @author Iulian Rotaru
 */
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

    @BeforeClass
    public static void beforeClass() {
        //MainActivity.start(App.context());
    }

    @Before
    public void beforeTest() {
        // should ensure that always start with the first level
        App.storage().resetLevels();
    }

    @Test
    public void openActivities() {
        // allow some time for main activity to start
        sleep(1000);

        // ------------------------------------------------
        // game activity

        // open levels list
        onView(withId(R.id.main_play)).perform(click());
        sleep(1000);

        // go back to main activity directly from levels list, that is, abort game
        onView(withId(R.id.fab_back)).perform(click());
        sleep(1000);

        // open levels list in order to go to game activity
        onView(withId(R.id.main_play)).perform(click());
        sleep(1000);

        // levels list is opened; click on first level
        onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withText("LEVEL 1")))).perform(click());
        sleep(1000);

        // game activity is opened: click on FAB menu to open its mini FAB items
        onView(withId(R.id.game_fab_menu)).perform(click());
        sleep(1000);

        // FAB menu items are opend: click to open hint dialog
        onView(withId(R.id.game_fab_hint)).perform(click());
        sleep(1000);

        // close hint dialog and reveal game activity with FAB menu opened
        onView(withId(R.id.fab_dialog_close)).perform(click());
        sleep(1000);

        // FAB menu items are opend: click to open instruments grid activity
        onView(withId(R.id.game_fab_view_grid)).perform(click());
        sleep(1000);

        // close instruments grid and go back to game activity
        onView(withId(R.id.fab_back)).perform(click());
        sleep(1000);

        // FAB menu items are opend: skip to next instrument; FAB menu remains opened
        onView(withId(R.id.game_fab_skip_next)).perform(click());
        sleep(1000);

        // FAB menu items are opend: click on back
        onView(withId(R.id.game_fab_back)).perform(click());
        sleep(1000);

        // close levels list and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // ------------------------------------------------
        // quiz activity

        // main activit is opened: click to open quiz start activity
        onView(withId(R.id.main_quiz)).perform(click());
        sleep(1000);

        // go back to main activity directly from quiz start, that is, abort quiz
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // main activit is opened: click to open quiz start activity
        onView(withId(R.id.main_quiz)).perform(click());
        sleep(1000);

        // quiz start is opened: locate hexagon with play icon and click it to start quiz
        onView(withTagValue(is((Object) "start-quiz"))).perform(click());
        sleep(1000);

        // close quiz and go back to main activity
        onView(withResourceName("fab_close")).perform(click());
        sleep(1000);

        // ------------------------------------------------
        // balance activity

        // main activit is opened: click to open balance activity
        onView(withId(R.id.main_balance)).perform(click());
        sleep(1000);

        // close balance and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // ------------------------------------------------
        // auxiliare activities

        // main activit is opened: click to open about activity
        onView(withId(R.id.main_about)).perform(click());
        sleep(1000);

        // close about activity and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // main activit is opened: click to open no ads activity
        onView(withId(R.id.main_no_ads)).perform(click());
        sleep(1000);

        // close no ads activity and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // main activit is opened: click to open share activity
        onView(withId(R.id.main_share)).perform(click());
        sleep(1000);

        // close share activity and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // main activit is opened: click to open recommended application activity
        onView(withId(R.id.main_recommended)).perform(click());
        sleep(1000);

        // close recommended application activity and go back to main activity
        onView(withResourceName("fab_back")).perform(click());
        sleep(1000);

        // back to main activity: end test case
        sleep(1000);
    }

    @Test
    public void playGameLevelOne() {
        String[] instrumentNames = getInstrumentNames(new NameTransform() {
            @Override
            public String transform(String name) {
                return name.toUpperCase().replaceAll("_", "");
            }
        });

        // allow some time for main activity to start and click on game play button
        sleep(1000);
        onView(allOf(withId(R.id.main_play), isDisplayed())).perform(click());
        sleep(1000);

        // levels list is opened; click on first level
        ViewInteraction levelButton = onView(allOf(withClassName(is(HexaIcon.class.getName())), hasSibling(withText("LEVEL 1"))));
        levelButton.perform(click());
        sleep(1000);

        // traverse all instruments from current level
        for (int n = 0; n < instrumentNames.length; ++n) {
            // enter all letters from instrument name, one by one
            String instrumentName = instrumentNames[n];
            for (int c = 0; c < instrumentName.length(); ++c) {
                onView(key(Character.toString(instrumentName.charAt(c)))).perform(click());
            }
            sleep(2000);

            // level 2 unlocked dialog is displayed after 5 instruments resolved
            if (n == 5) {
                // close dialog for level 2 unlocked
                onView(allOf(withResourceName("fab_dialog_close"), isDisplayed())).perform(click());
                sleep(2000);
            }
        }

        // close dialog for level complete
        onView(allOf(withResourceName("fab_dialog_close"), isDisplayed())).perform(click());
        sleep(1000);

        // close levels list and go back to main activity
        onView(allOf(withResourceName("fab_back"), isDisplayed())).perform(click());
        sleep(1000);
    }

    @Test
    public void playFirstQuiz() {
        String[] instrumentNames = getInstrumentNames(new NameTransform() {
            @Override
            public String transform(String name) {
                return name.toLowerCase().replaceAll("_", " ");
            }
        });

        // allow some time for main activity to start and click on quiz play button
        sleep(1000);
        onView(allOf(withId(R.id.main_quiz), isDisplayed())).perform(click());
        sleep(1000);

        // locate hexagon with play icon and click it
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
        sleep(1000);
    }

    private static String[] getInstrumentNames(NameTransform nameTransform) {
        Instrument[] instruments = App.storage().getInstruments();
        String[] instrumentNames = new String[LEVEL_SIZE];
        for (int i = 0; i < LEVEL_SIZE; ++i) {
            instrumentNames[i] = nameTransform.transform(instruments[i].getName());
        }
        return instrumentNames;
    }

    private static interface NameTransform {
        String transform(String name);
    }
}
