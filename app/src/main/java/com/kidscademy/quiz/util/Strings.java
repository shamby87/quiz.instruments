package com.kidscademy.quiz.util;

import org.jetbrains.annotations.NonNls;

import java.util.Locale;

public class Strings extends js.util.Strings {

    public static String toString(int value) {
        return String.format(Locale.getDefault(), "%d", value); //NON-NLS
    }

    public static String format(@NonNls String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }
}
