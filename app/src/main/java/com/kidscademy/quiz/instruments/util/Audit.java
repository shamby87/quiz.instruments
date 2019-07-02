package com.kidscademy.quiz.instruments.util;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.instruments.RemoteLogger;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.QuizChallenge;

import java.util.Locale;

import js.converter.Converter;
import js.converter.ConverterRegistry;
import js.util.Net;

/**
 * Record application events.
 *
 * @author Iulian Rotaru
 */
public class Audit {
    private enum Event {
        APP_LOAD, APP_ACTIVE, PREF_CHANGE, OPEN_MARKET, OPEN_RATE, OPEN_RECOMMENDED, OPEN_SHARE, OPEN_ABOUT, OPEN_NO_ADS, APP_SHARE, PLAY_LEVEL, GAME_OK, GAME_BAD, GAME_HINT, GAME_SKIP, GAME_CLOSE, PLAY_QUIZ, QUIZ_OK, QUIZ_BAD, QUIZ_TIMEOUT, QUIZ_ABORT, VIEW_BALANCE, RESET_SCORE
    }

    private final Converter converter;
    private final RemoteLogger remoteLogger;
    private boolean enabled;
    private long timestamp;

    public Audit(RemoteLogger remoteLogger) {
        this.converter = ConverterRegistry.getConverter();
        this.remoteLogger = remoteLogger;
    }

    public void openApplication() {
        if (enabled) {
            timestamp = System.currentTimeMillis();
        }
    }

    public void closeApplication() {
        if (enabled) {
            send(Event.APP_ACTIVE, System.currentTimeMillis() - timestamp);
        }
    }

    public void preferenceChanged(String key, Object valueObject) {
        String value = valueObject instanceof String ? (String) valueObject : valueObject.toString();

        if (App.context().getString(R.string.pref_developer_data_key).equals(key)) {
            if ((boolean) valueObject) {
                enabled = true;
            }
            send(Event.PREF_CHANGE, key, value);
            if (!(boolean) valueObject) {
                enabled = false;
            }
            return;
        }

        if (enabled) {
            send(Event.PREF_CHANGE, key, value);
        }
    }

    public void openMarket() {
        if (enabled) {
            send(Event.OPEN_MARKET);
        }
    }

    public void openRate() {
        if (enabled) {
            send(Event.OPEN_RATE);
        }
    }

    public void openRecommended() {
        if (enabled) {
            send(Event.OPEN_RECOMMENDED);
        }
    }

    public void openShare() {
        if (enabled) {
            send(Event.OPEN_SHARE);
        }
    }

    public void openAbout() {
        if (enabled) {
            send(Event.OPEN_ABOUT);
        }
    }

    public void openNoAdsManifest() {
        if (enabled) {
            send(Event.OPEN_NO_ADS);
        }
    }

    public void shareApp(String sharingMedia) {
        if (enabled) {
            send(Event.APP_SHARE, sharingMedia.toLowerCase(Locale.getDefault()));
        }
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

    protected void send(Enum<?> event, Object... args) {
        switch (Net.getConnectionType(App.context())) {
            case WIFI:
                break;

            case MOBILE:
                if (event.name().equals(Event.APP_LOAD.name())) {
                    break;
                }
                // fall trough default

            default:
                return;
        }
        remoteLogger.recordAuditEvent(App.PROJECT_NAME, App.instance().device(), event.name(), parameter(args, 0), parameter(args, 1));
    }

    /**
     * Stringify argument identified by index.
     *
     * @param args  arguments list,
     * @param index desired argument index.
     * @return string representation of indexed argument or null if missing.
     */
    private String parameter(Object[] args, int index) {
        return args.length > index ? converter.asString(args[index]) : null;
    }
}
