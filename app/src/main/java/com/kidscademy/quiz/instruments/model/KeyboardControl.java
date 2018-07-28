package com.kidscademy.quiz.instruments.model;

public interface KeyboardControl {
    char getExpectedChar(int charIndex);

    void hideUnusedLetters();
}
