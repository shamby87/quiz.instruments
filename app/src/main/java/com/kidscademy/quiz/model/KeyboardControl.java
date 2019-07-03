package com.kidscademy.quiz.model;

/**
 * Keyboard control for game answer input.
 *
 * @author Iulian Rotaru
 */
public interface KeyboardControl {
    /**
     * Get character from expected answer at requested index.
     *
     * @param charIndex expected character index.
     * @return character from expected answer.
     */
    char getExpectedChar(int charIndex);

    /**
     * Keyboard contains all expected answer letter and additional letter to increase difficulty. This
     * method hides unused letters.
     */
    void hideUnusedLetters();
}
