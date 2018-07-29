package com.kidscademy.quiz.instruments.util;

import android.content.Context;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Config;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.Level;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.util.StorageBase;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;

/**
 * Application persistent storage.
 *
 * @author Iulian Rotaru
 */
public class Storage extends StorageBase {
    /**
     * Class logger.
     */
    private static final Log log = LogFactory.getLog(Storage.class);

    private static final String INSTRUMENTS_PATH = "instruments.json"; // NON-NLS
    private static final String BALANCE_PATH = "balance.json"; // NON-NLS
    private static final String LEVELS_PATH = "levels.json"; // NON-NLS
    private static final String LEVEL_STATES_PATH = "level-states.json"; // NON-NLS

    /**
     * Instruments collection sorted descendant by rank. It is the order of instruments per levels.
     */
    private static Instrument[] instruments;
    /**
     * User balance keeps score points and earned credits.
     */
    private Balance balance;
    /**
     * Immutable game levels. Level mutable state is held by level state class.
     */
    private Level[] levels;
    /**
     * Game level states keeps level mutable state.
     */
    private LevelState[] levelStates;

    /**
     * Create storage instance.
     *
     * @param context runtime context provided by framework.
     */
    public Storage(Context context) {
        super(context);
    }

    @Override
    public void onAppCreate() {
        log.trace("onAppCreate()"); // NON-NLS

        config = loadObject(getConfigFile(), Config.class);
        balance = loadObject(getBalanceFile(), Balance.class);
        instruments = loadObject(getInstrumentsFile(), Instrument[].class);
        levels = loadObject(getLevelsFile(), Level[].class);
        levelStates = loadObject(getLevelStatesFile(), LevelState[].class);

        if (!isStorageValid()) {
            initLevels();
        }
    }

    private boolean isStorageValid() {
        if (instruments.length < 100) {
            return false;
        }
        for (Instrument instrument : instruments) {
            if (instrument == null) {
                return false;
            }
        }
        if (levels.length < 10) {
            return false;
        }
        for (Level level : levels) {
            if (level == null) {
                return false;
            }
        }
        if (levelStates.length < 10) {
            return false;
        }
        for (LevelState levelState : levelStates) {
            if (levelState == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onAppClose() throws Exception {
        super.onAppClose();

        // levels are immutable and need not to be saved

        saveObject(balance, getBalanceFile());
        saveObject(instruments, getInstrumentsFile());
        saveObject(levelStates, getLevelStatesFile());
    }

    public Instrument[] getInstruments() {
        return instruments;
    }

    public Instrument getInstrument(int index) {
        return instruments[index];
    }

    public Balance getBalance() {
        return balance;
    }

    public List<Integer> getLevelInstruments(int levelIndex) {
        return levels[levelIndex].getInstrumentIndices();
    }

    public Level[] getLevels() {
        return levels;
    }

    public Level getLevel(int index) {
        return levels[index];
    }

    public void resetLevels() {
        initLevels();
        try {
            saveObject(levels, getLevelsFile());
            saveObject(balance, getBalanceFile());
            saveObject(levelStates, getLevelStatesFile());
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Returns the state of the level after current level or null if current level is last.
     *
     * @param currentIndex current level index.
     * @return next level or null.
     */
    public LevelState getNextLevel(int currentIndex) {
        return currentIndex < (levelStates.length - 1) ? levelStates[currentIndex + 1] : null;
    }

    public LevelState[] getLevelStates() {
        return levelStates;
    }

    public LevelState getLevelState(int levelIndex) {
        return levelStates[levelIndex];
    }

    private File getInstrumentsFile() {
        return new File(directory, INSTRUMENTS_PATH);
    }

    private File getBalanceFile() {
        return new File(directory, BALANCE_PATH);
    }

    private File getLevelsFile() {
        return new File(directory, LEVELS_PATH);
    }

    private File getLevelStatesFile() {
        return new File(directory, LEVEL_STATES_PATH);
    }

    // ------------------------------------------------------

    private static final int INSTRUMENTS_PER_LEVEL = 10;

    private void initLevels() {
        try {
            Reader reader = new InputStreamReader(App.context().getAssets().open(INSTRUMENTS_PATH));
            instruments = loadObject(reader, Instrument[].class);
        } catch (IOException e) {
            throw new BugError(e);
        }
        if (instruments.length < INSTRUMENTS_PER_LEVEL) {
            throw new BugError("Invalid instruments list size |%d|.", instruments.length);
        }

        // Java numbers narrowing is using round-toward-zero
        levels = new Level[instruments.length / INSTRUMENTS_PER_LEVEL];

        for (int levelIndex = 0, instrumentIndex = 0; levelIndex < levels.length; ++levelIndex) {
            final List<Integer> levelInstruments = new ArrayList<>(INSTRUMENTS_PER_LEVEL);
            for (int i = 0; i < INSTRUMENTS_PER_LEVEL; ++i, ++instrumentIndex) {
                levelInstruments.add(instruments[instrumentIndex].getIndex());
            }
            levels[levelIndex] = new Level(levelIndex, levelInstruments);
        }

        Flags.setCurrentLevel(0);

        levelStates = new LevelState[levels.length];
        levelStates[0] = new LevelState(0, INSTRUMENTS_PER_LEVEL);
        levelStates[0].unlock();
        for (int i = 1; i < levelStates.length; ++i) {
            levelStates[i] = new LevelState(i, INSTRUMENTS_PER_LEVEL);
        }

        try {
            saveObject(instruments, getInstrumentsFile());
            saveObject(levels, getLevelsFile());
            saveObject(levelStates, getLevelStatesFile());
        } catch (IOException e) {
            throw new BugError(e);
        }
    }
}
