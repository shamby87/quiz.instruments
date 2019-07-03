package com.kidscademy.quiz.instruments;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.kidscademy.quiz.activity.MainActivity;
import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.app.Storage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.quiz.instruments.Util.waitView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Play quiz with wrong answers and timeout.
 *
 * @author Iulian Rotaru
 */
@SuppressWarnings("HardCodedStringLiteral")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class BadQuizTest {
    private static final int LEVEL_SIZE = 10;
    private static final int PROGRESS_THRESHOLD = 96;

    @Rule
    public ActivityTestRule<MainActivity> activity = new ActivityTestRule<>(MainActivity.class);

    private Storage storage;

    @Before
    public void beforeTest() {
        storage = App.instance().storage();
        storage.resetLevels();

        // allow some time for main activity to start and click on quiz play button
        onView(withId(R.id.main_quiz)).perform(click());

        // locate hexagon with play icon and click it
        onView(withTagValue(is((Object) "start-quiz"))).perform(click());
    }

    @After
    public void afterTest() {
        // close dialog for quiz complete and wait for main menu to be displayed
        waitView(withResourceName("fab_dialog_close")).perform(click());
        waitView(withId(R.id.main_quiz)).check(matches(isDisplayed()));
    }

    @Test
    public void playWrongAnswer() {
        // quiz activity displays challenge instrument picture and couple options as buttons
        // one options has as text the challenged instrument name
        // for every quiz searches for first button that has not the correct option name

        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 3; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));
            onView(withoutText(instrument.getLocaleName())).perform(click());
        }
    }

    @Test
    public void multipleBadQuizOptionClicks() {
        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 3; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            for (int j = 0; j < 4; ++j) {
                onView(withoutText(instrument.getLocaleName())).perform(click());
            }
        }
    }

    @Test
    public void multipleCorrectQuizOptionClicks() {
        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 10; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            ViewInteraction interaction = onView(allOf(instanceOf(Button.class), withText(instrument.getLocaleName())));
            interaction.perform(click(), click());
        }
    }

    @Test
    public void timeoutOnMissingAnswer() {
        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 3; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));
            onView(allOf(instanceOf(Button.class), withText(instrument.getLocaleName()))).check(matches(isDisplayed()));
        }
    }

    /**
     * Wait for new challenge to be displayed and do not answer immediately but wait for progress bar to almost fill.
     * Current progress fill threshold is heuristically fixed to 96.
     */
    @Test
    public void timeoutRaceCondition() {
        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 10; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            waitView(progressComplete(PROGRESS_THRESHOLD));
            onView(withText(instrument.getLocaleName())).perform(click());
        }
    }

    @Test
    public void timeoutRaceConditionOnBadResponse() {
        Instrument[] instruments = storage.getInstruments();
        for (int i = 0; i < 3; ++i) {
            final Instrument instrument = instruments[i];
            waitView(withTagValue(is((Object) instrument.getPicturePath())));

            if (i == 2) {
                waitView(progressComplete(PROGRESS_THRESHOLD));
            }
            onView(withoutText(instrument.getLocaleName())).perform(click());
        }
    }

    private static Matcher<View> progressComplete(final int progress) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof ProgressBar) {
                    return ((ProgressBar) view).getProgress() > progress;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("progress complete");
            }
        };
    }

    private static String[] getInstrumentNames() {
        Instrument[] instruments = App.instance().storage().getInstruments();
        String[] instrumentNames = new String[LEVEL_SIZE];
        for (int i = 0; i < LEVEL_SIZE; ++i) {
            instrumentNames[i] = instruments[i].getLocaleName().toLowerCase();
        }
        return instrumentNames;
    }

    /**
     * Get first option button that does not have given text as button name.
     *
     * @param text text used as button name.
     * @return first button with name different from given text.
     */
    public static Matcher<View> withoutText(final CharSequence text) {
        return new TypeSafeMatcher<View>() {
            private boolean first = true;

            @Override
            public void describeTo(Description description) {
                description.appendText("first button without text");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (first && view instanceof Button) {
                    boolean found = !((Button) view).getText().equals(text);
                    if (found) {
                        first = false;
                    }
                    return found;
                }
                return false;
            }
        };
    }
}
