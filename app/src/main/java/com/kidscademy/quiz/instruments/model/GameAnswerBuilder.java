package com.kidscademy.quiz.instruments.model;

/**
 * Game answer builder adds letters and returns aggregated value.
 *
 * @author Iulian Rotaru
 */
public interface GameAnswerBuilder {
    /**
     * Add letter to this answer builder.
     *
     * @param letter letter to add.
     */
    void addLetter(char letter);

    /**
     * Test if answer has all letters.
     *
     * @return true if answer has all letters.
     */
    boolean hasAllLetters();

    /**
     * Get the index of the first letter missing from answer. Returns -1 if answer builder is complete.
     *
     * @return index of the first missing letter or -1.
     */
    int getFirstMissingLetterIndex();

    /**
     * Get this builder value, that is, the answer.
     *
     * @return answer value.
     */
    String getValue();
}
