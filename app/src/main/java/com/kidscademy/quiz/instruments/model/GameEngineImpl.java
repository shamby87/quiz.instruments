package com.kidscademy.quiz.instruments.model;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.instruments.util.Audit;
import com.kidscademy.quiz.instruments.util.Storage;

import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class GameEngineImpl implements GameEngine {
    private static final Log log = LogFactory.getLog(GameEngineImpl.class);

    private static final int NO_UNLOCK_LEVEL = -1;

    private final Storage storage;

    private final Audit audit;

    private final Counters counters;

    private final Balance balance;

    /**
     * Reference to storage instruments list.
     */
    private final Instrument[] instruments;

    private KeyboardControl keyboard;

    private AnswerBuilder answer;

    /**
     * Value of level index loaded from intent.
     */
    private int levelIndex;

    private Level level;

    private LevelState levelState;

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

    public GameEngineImpl(Storage storage, Audit audit) {
        log.trace("GameEngineImpl(int)");
        this.storage = storage;
        this.audit = audit;

        this.counters = storage.getCounters();
        this.balance = storage.getBalance();
        this.instruments = storage.getInstruments();
    }

    @Override
    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;

        level = storage.getLevel(levelIndex);
        levelState = storage.getLevelState(levelIndex);
    }

    @Override
    public int getLevelIndex() {
        return levelIndex;
    }

    @Override
    public int getLevelInstrumentsCount() {
        return level.getInstrumentsCount();
    }

    @Override
    public int getLevelSolvedInstrumentsCount() {
        return levelState.getSolvedInstrumentsCount();
    }

    @Override
    public void stop() {
    }

    @Override
    public void setAnswerBuilder(AnswerBuilder answer) {
        this.answer = answer;
    }

    @Override
    public void setKeyboardControl(KeyboardControl keyboard) {
        this.keyboard = keyboard;
    }

    /**
     * Update challenged instrument and point instrument index to next challenge.
     *
     * @param instrumentName instrument name or null for first one.
     * @return current challenge instrument.
     */
    @Override
    public void start(String instrumentName) {
        log.trace("start(String)");
        App.audit().playGameLevel(levelIndex);

        List<Integer> unsolvedInstruments = levelState.getUnsolvedInstruments(storage);
        if (unsolvedInstruments.isEmpty()) {
            return;
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
    }

    public boolean nextChallenge() {
        log.trace("nextChallenge()");
        List<Integer> unsolvedInstruments = levelState.getUnsolvedInstruments(storage);
        if (unsolvedInstruments.isEmpty()) {
            App.audit().gameClose(challengedInstrument);
            balance.plusScore(Balance.getScoreLevelCompleteBonus(levelState.getIndex()));
            challengedInstrument = null;
            return false;
        }
        if (challengedInstrumentIndex >= unsolvedInstruments.size()) {
            challengedInstrumentIndex = 0;
        }
        challengedInstrument = instruments[unsolvedInstruments.get(challengedInstrumentIndex++)];
        return true;
    }

    public Instrument getChallengedInstrument() {
        return challengedInstrument;
    }

    public void skipChallenge() {
        audit.gameSkip(challengedInstrument);
        nextChallenge();
    }

    public AnswerState handleKeyboardChar(char c) {
        if (answer.hasAllCharsFilled()) {
            return AnswerState.OVERFLOW;
        }
        answer.putChar(c);
        if (!answer.hasAllCharsFilled()) {
            return AnswerState.FILLING;
        }

        if (!checkAnswer(answer.getValue())) {
            audit.gameWrongAnswer(challengedInstrument, answer.getValue());
            return AnswerState.WRONG;
        }
        audit.gameCorrectAnswer(challengedInstrument);
        return AnswerState.CORRECT;
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

    @Override
    public boolean revealLetter() {
        if (!balance.deductRevealLetter()) {
            return false;
        }
        audit.gameHint(challengedInstrument, "REVEAL_LETTER");
        int firstMissingCharIndex = answer.getFirstMissingCharIndex();
        assert firstMissingCharIndex != -1;
        handleKeyboardChar(keyboard.getExpectedChar(firstMissingCharIndex));
        return true;
    }

    @Override
    public boolean verifyInput() {
        if (balance.deductVerifyInput()) {
            audit.gameHint(challengedInstrument, "VERIFY_INPUT");
            return true;
        }
        return false;
    }

    @Override
    public boolean hideLetters() {
        if (balance.deductHideLettersInput()) {
            audit.gameHint(challengedInstrument, "HIDE_LETTERS");
            keyboard.hideUnusedLetters();
            return true;
        }
        return false;
    }

    @Override
    public boolean playSample() {
        if (balance.deductSayName()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasCredit() {
        return balance.hasCredit();
    }

    @Override
    public int getScore() {
        return balance.getScore();
    }

    @Override
    public int getCredit() {
        return balance.getCredit();
    }
}
