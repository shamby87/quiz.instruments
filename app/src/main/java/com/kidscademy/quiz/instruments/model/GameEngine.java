package com.kidscademy.quiz.instruments.model;

import com.kidscademy.quiz.instruments.model.AnswerBuilder;
import com.kidscademy.quiz.instruments.model.Instrument;

/**
 * Game engine model.
 *
 * @author Iulian Rotaru
 */
public interface GameEngine {
    void start(String instrumentName);

    void stop();

    void setLevelIndex(int levelIndex);

    int getLevelIndex();

    int getLevelInstrumentsCount();

    int getLevelSolvedInstrumentsCount();

    int getUnlockedLevelIndex();

    void setAnswerBuilder(AnswerBuilder answer);

    void setKeyboardControl(KeyboardControl keyboard);

    /**
     * Prepare next challenge that will be accessible via {@link #getChallengedInstrument()}} and signal if level complete.
     * If level is complete this method returns false and set internal challenged instrument to null.
     *
     * @return true if level has more challenges and false if level is complete.
     */
    boolean nextChallenge();

    void skipChallenge();

    AnswerState handleKeyboardChar(char c);

    Instrument getChallengedInstrument();

    boolean wasNextLevelUnlocked();

    boolean revealLetter();

    boolean verifyInput();

    boolean hideLetters();

    boolean playSample();

    int getScore();

    int getCredit();

    boolean hasCredit();
}
