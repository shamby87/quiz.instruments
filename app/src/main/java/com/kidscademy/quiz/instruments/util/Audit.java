package com.kidscademy.quiz.instruments.util;

import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.QuizChallenge;
import com.kidscademy.util.AuditBase;

/**
 * Record application events.
 *
 * @author Iulian Rotaru
 */
public class Audit extends AuditBase {
    private static enum Event {
        PLAY_LEVEL, GAME_OK, GAME_BAD, GAME_HINT, GAME_SKIP, GAME_CLOSE, PLAY_QUIZ, QUIZ_OK, QUIZ_BAD, QUIZ_TIMEOUT, QUIZ_ABORT, VIEW_BALANCE, RESET_SCORE
    }

    public void playGameLevel(int levelIndex) {
        if (enabled) {
            send(Event.PLAY_LEVEL, (levelIndex + 1));
        }
    }

    public void gameCorrectAnswer(Instrument instrument) {
        if (enabled) {
            send(Event.GAME_OK, instrument.getName());
        }
    }

    public void gameWrongAnswer(Instrument instrument, String answer) {
        if (enabled) {
            send(Event.GAME_BAD, instrument.getName(), answer);
        }
    }

    public void gameHint(Instrument instrument, String hintType) {
        if (enabled) {
            send(Event.GAME_HINT, instrument.getName(), hintType);
        }
    }

    public void gameSkip(Instrument instrument) {
        if (enabled) {
            send(Event.GAME_SKIP, instrument.getName());
        }
    }

    public void gameClose(Instrument instrument) {
        if (enabled) {
            send(Event.GAME_CLOSE, instrument.getName());
        }
    }

    public void playQuiz() {
        if (enabled) {
            send(Event.PLAY_QUIZ);
        }
    }

    public void quizCorrectAnswer(QuizChallenge challenge) {
        if (enabled) {
            send(Event.QUIZ_OK, challenge.getInstrument().getName());
        }
    }

    public void quizWrongAnswer(QuizChallenge challenge, String answer) {
        if (enabled) {
            send(Event.QUIZ_BAD, challenge.getInstrument().getName(), answer);
        }
    }

    public void quizTimeout(QuizChallenge challenge) {
        if (enabled) {
            send(Event.QUIZ_TIMEOUT, challenge.getInstrument().getName());
        }
    }

    public void quizAbort(QuizChallenge challenge) {
        if (enabled) {
            send(Event.QUIZ_ABORT, challenge.getInstrument().getName());
        }
    }

    public void viewBalance() {
        if (enabled) {
            send(Event.VIEW_BALANCE);
        }
    }

    public void resetScore() {
        if (enabled) {
            send(Event.RESET_SCORE);
        }
    }
}
