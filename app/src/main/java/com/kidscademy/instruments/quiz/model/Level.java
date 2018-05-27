package com.kidscademy.instruments.quiz.model;

import com.kidscademy.instruments.quiz.App;

import java.util.List;

import js.lang.BugError;

public class Level {
    /** Zero based level index. */
    private int index;
    /**
     * Array of indices to this level instruments.
     */
    private List<Integer> instrumentIndices;

    /**
     * Reference to storage instruments list.
     */
    private transient Instrument[] instruments;

    public Level() {
        this.instruments = App.storage().getInstruments();
    }

    /**
     * Create level instance.
     *
     * @param index       zero based level index,
     * @param instruments level instruments list.
     */
    public Level(int index, List<Integer> instruments) {
        this();
        this.index = index;
        this.instrumentIndices = instruments;
    }

    public int getIndex() {
        return index;
    }

    public String getImageAssetPath() {
        return getInstrument(0).getPicturePath();
    }

    public List<Integer> getInstrumentIndices() {
        return instrumentIndices;
    }

    public int getInstrumentsCount() {
        return instrumentIndices.size();
    }

    public Instrument getInstrument(int index) {
        return instruments[index];
    }

    public LevelState getState() {
        return App.storage().getLevelState(index);
    }

    public static int getTotalLevels() {
        return App.storage().getLevels().length;
    }

    public static int getUnlockedLevels() {
        int unlockedLevels = 0;
        for (LevelState levelState : App.storage().getLevelStates()) {
            if (levelState.isUnlocked()) {
                ++unlockedLevels;
            }
        }
        return unlockedLevels;
    }

    public static int getCompletedLevels() {
        int completedLevels = 0;
        for (LevelState levelState : App.storage().getLevelStates()) {
            if (levelState.isComplete()) {
                ++completedLevels;
            }
        }
        return completedLevels;
    }

    public static int getFirstUncompletedLevelIndex() {
        for (LevelState levelState : App.storage().getLevelStates()) {
            if (!levelState.isComplete()) {
                return levelState.getIndex();
            }
        }
        throw new BugError("Attempt to start quiz for completed game.");
    }

    public static boolean areAllLevelsComplete() {
        for (LevelState levelState : App.storage().getLevelStates()) {
            if (!levelState.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public static int getTotalInstruments() {
        int totalInstruments = 0;
        for (Level level : App.storage().getLevels()) {
            totalInstruments += level.getInstrumentsCount();
        }
        return totalInstruments;
    }

    public static int getSolvedInstruments() {
        int solvedInstruments = 0;
        for (LevelState level : App.storage().getLevelStates()) {
            solvedInstruments += level.getSolvedInstrumentsCount();
        }
        return solvedInstruments;
    }
}
