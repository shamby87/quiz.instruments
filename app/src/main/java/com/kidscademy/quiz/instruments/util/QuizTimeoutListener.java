package com.kidscademy.quiz.instruments.util;

/**
 * Event fired when no response for a quiz in certain time.
 *
 * @author Iulian Rotaru
 */
public interface QuizTimeoutListener {
    void onQuizTimeout();
}
