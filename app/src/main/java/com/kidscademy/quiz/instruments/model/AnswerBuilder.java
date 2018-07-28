package com.kidscademy.quiz.instruments.model;

public interface AnswerBuilder {
    void putChar(char c);

    boolean hasAllCharsFilled();

    String getValue();

    int getFirstMissingCharIndex();
}
