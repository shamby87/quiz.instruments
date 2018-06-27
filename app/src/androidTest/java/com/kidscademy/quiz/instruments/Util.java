package com.kidscademy.quiz.instruments;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.view.KeyboardView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;

public class Util {
    public static Matcher<View> key(String key) {
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

    public static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
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

    public static void info(String message, Object... args) {
        Log.i("PlayGameTest", String.format(message, args));
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
