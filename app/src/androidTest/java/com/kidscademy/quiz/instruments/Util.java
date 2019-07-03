package com.kidscademy.quiz.instruments;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;


import com.kidscademy.quiz.view.KeyboardView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.is;

class Util {
    static Matcher<View> key(String key) {
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

    static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
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

    static Matcher<View> firstOf(final Matcher<View> parentMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with first child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }
                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(0).equals(view);

            }
        };
    }

    static ViewInteraction waitView(Matcher<View> matcher) {
        for (int j = 0; ; j++) {
            if (j == 200) {
                throw new AssertionError("View not loaded: " + matcher.toString());
            }
            try {
                return onView(matcher).check(matches(isDisplayed()));
            } catch (Throwable ignore) {
            }
            sleep(40);
        }
    }

    static void info(String message, Object... args) {
        Log.i("PlayGameTest", String.format(message, args));
    }

    static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
