package com.kidscademy.quiz.instruments.engine;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Counters;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.quiz.instruments.util.Storage;

import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class GameEngine {
    private static final Log log = LogFactory.getLog(GameEngine.class);

    private static final int NO_UNLOCK_LEVEL = -1;

    private final Storage storage;
    /**
     * Reference to storage instruments list.
     */
    private final Instrument[] instruments;

    private LevelState levelState;
    private Counters counters;

    /**
     * Instrument currently displayed as challenge.
     */
    private Instrument challengedInstrument;

    /**
     * Current challenged instrument index, relative to level not solved instruments collection.
     */
    private int challengedInstrumentIndex;

    /**
     * If current level unlock threshold was reached this field contains the index of the next level.
     */
    private int unlockedLevelIndex = NO_UNLOCK_LEVEL;

    private Balance balance;

    public GameEngine(Storage storage, LevelState levelState) {
        log.trace("GameEngine(int)");
        this.storage = storage;
        this.instruments = storage.getInstruments();
        this.levelState = levelState;
        this.counters = storage.getCounters();
        this.balance = storage.getBalance();
    }

    /**
     * Update challenged instrument and point instrument index to next challenge.
     *
     * @param instrumentName instrument name or null for first one.
     * @return current challenge instrument.
     */
    public Instrument initChallenge(String instrumentName) {
        log.trace("initChallenge(String)");
        List<Integer> unsolvedInstruments = levelState.getUnsolvedInstruments(storage);
        if (unsolvedInstruments.isEmpty()) {
            return null;
        }
        if (instrumentName == null) {
            challengedInstrument = instruments[unsolvedInstruments.get(0)];
            challengedInstrumentIndex = 1;
        } else {
            for (challengedInstrumentIndex = 0; challengedInstrumentIndex < unsolvedInstruments.size(); ) {
                challengedInstrument = instruments[unsolvedInstruments.get(challengedInstrumentIndex++)];
                if (challengedInstrument.getLocaleName().equals(instrumentName)) {
                    break;
                }
            }
        }
        return challengedInstrument;
    }

    public Instrument nextChallenge() {
        log.trace("nextChallenge()");
        List<Integer> unsolvedInstruments = levelState.getUnsolvedInstruments(storage);
        if (unsolvedInstruments.isEmpty()) {
            balance.plusScore(Balance.getScoreLevelCompleteBonus(levelState.getIndex()));
            return null;
        }
        if (challengedInstrumentIndex >= unsolvedInstruments.size()) {
            challengedInstrumentIndex = 0;
        }
        challengedInstrument = instruments[unsolvedInstruments.get(challengedInstrumentIndex++)];
        return challengedInstrument;
    }

    public boolean checkAnswer(String answer) {
        assert challengedInstrument != null;

        if (!challengedInstrument.getLocaleName().equals(answer)) {
            counters.minus(challengedInstrument);
            final int penalty = Balance.getScorePenalty();
            balance.minusScore(penalty);
            levelState.minusScore(penalty);
            return false;
        }

        counters.plus(challengedInstrument);
        final int points = Balance.getScoreIncrement(levelState.getIndex());
        balance.plusScore(points);
        levelState.plusScore(points);
        levelState.solveInstrument(challengedInstrument.getIndex());
        // decrement brand index to compensate for solved brand that will not be part of unsolved brands on next challenge
        --challengedInstrumentIndex;

        // logic to unlock next level
        // store next level index for unlocking only if next level is not already enabled

        LevelState nextLevel = App.storage().getNextLevel(levelState.getIndex());
        if (nextLevel != null && !nextLevel.isUnlocked() && levelState.isUnlockThreshold()) {
            nextLevel.unlock();
            unlockedLevelIndex = nextLevel.getIndex();
            final int bonus = Balance.getScoreLevelUnlockBonus(levelState.getIndex());
            balance.plusScore(bonus);
            levelState.plusScore(bonus);
        }
        return true;
    }

    public boolean wasNextLevelUnlocked() {
        return unlockedLevelIndex != NO_UNLOCK_LEVEL;
    }

    /**
     * Get unlocked level index. This getter returns meaningful value only if {@link #wasNextLevelUnlocked()} return true;
     * otherwise behavior is not specified. Returned value is usable only once, that is, this method reset internal value
     * before returning.
     *
     * @return unlocked level index.
     */
    public int getUnlockedLevelIndex() {
        assert unlockedLevelIndex != NO_UNLOCK_LEVEL;
        int i = unlockedLevelIndex;
        unlockedLevelIndex = NO_UNLOCK_LEVEL;
        return i;
    }
}
