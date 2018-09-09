package com.kidscademy.quiz.instruments.model;

/**
 * Answer state.
 *
 * @author Iulian Rotaru
 */
public enum GameAnswerState {
    /**
     * Keyboard characters are adding. Answer builder capacity is not reached yet.
     */
    FILLING,

    /**
     * Answer builder is full. Answer builder capacity is set to the length of the expected challenge solution.
     */
    OVERFLOW,

    /**
     * Answer is complete and is correct. Answer is considered complete when has the same length as expected challenge solution.
     */
    CORRECT,

    /**
     * Answer is complete and is wrong. Answer is considered complete when has the same length as expected challenge solution.
     */
    WRONG
}
