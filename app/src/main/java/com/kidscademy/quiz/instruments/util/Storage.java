package com.kidscademy.quiz.instruments.util;

import android.content.Context;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.Level;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.quiz.instruments.model.StorageObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import js.core.Factory;
import js.json.Json;
import js.lang.BugError;
import js.lang.GType;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.EnvironmentVariables;
import js.util.Files;
import js.util.Types;

/**
 * Application persistent storage.
 *
 * @author Iulian Rotaru
 */
public class Storage {
    /**
     * Class logger.
     */
    private static final Log log = LogFactory.getLog(Storage.class);

    private static final String INSTRUMENTS_PATH = "instruments.json"; // NON-NLS
    private static final String BALANCE_PATH = "balance.json"; // NON-NLS
    private static final String LEVELS_PATH = "levels.json"; // NON-NLS
    private static final String LEVEL_STATES_PATH = "level-states.json"; // NON-NLS

    /**
     * Application specific directory on device storage.
     */
    protected File directory;

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

        EnvironmentVariables environment = new EnvironmentVariables();
        if (EnvironmentVariables.MEDIA_MOUNTED.equals(environment.getExternalStorageState())) {
            // Context.getExternalFilesDir() returns null if external storage is not mounted
            // also be aware that Context.getExternalFilesDir() also returns null if rights are missing and directory is not
            // created: <uses-permission android:name="android.permissions.WRITE_EXTERNAL_STORAGE" />

            // Excerpt from Context#getExternalFilesDir API:
            // Unlike Environment.getExternalStoragePublicDirectory(), the directory returned here will be automatically
            // created for you.
            // it seems returned directory, if not null, it is guaranteed to be created

            directory = context.getExternalFilesDir(null);
            if (directory != null) {
                if (directory.getParentFile() == null) {
                    // under mysterious conditions returned directory is the file system root
                    // it was observed on GI-I9500_TMMARS, Android 4.1.1, SDK 16
                    // if this is the case consider storage as invalid and application abort
                    directory = null;
                }
            }
        }

        // if external storage is not found uses the internal one
        if (directory == null) {
            directory = context.getFilesDir();
        }
    }

    public boolean isValid() {
        return directory != null && directory.exists();
    }

    public void onAppCreate() throws IOException {
        log.trace("onAppCreate()"); // NON-NLS

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

    public void onAppClose() throws Exception {
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
    protected <T> T loadObject(File file, Type... types) throws IOException {
        if (!file.exists()) {
            Type type = types.length > 1 ? new GType(types[0], Arrays.copyOfRange(types, 1, types.length)) : types[0];
            if (Types.isArray(type)) {
                return (T) Array.newInstance(((Class) type).getComponentType(), 0);
            }
            return Classes.newInstance(type);
        }

        try {
            Reader reader = new FileReader(file);
            return loadObject(reader, types);
        } catch (FileNotFoundException unused) {
            throw new BugError("File not found exception on existing file |%s|.", file);
        }
    }

    protected <T> T loadObject(Reader reader, Type... types) throws IOException {
        Type type = types.length > 1 ? new GType(types[0], Arrays.copyOfRange(types, 1, types.length)) : types[0];

        try {
            Json json = Factory.getInstance(Json.class);
            T instance = json.parse(reader, type);

            if (Types.isArrayLike(instance)) {
                postProcessArray(instance);
            } else {
                // Java language instanceof operator evaluates to false if instance is null
                if (instance instanceof StorageObject) {
                    ((StorageObject) instance).onCreate(this);
                }
            }

            return instance;
        } finally {
            Files.close(reader);
        }
    }

    private void postProcessArray(Object array) {
        // do not include recursive iteration guard since is sure an object hierarchy cannot be infinite
        // in the end Object root will be reached
        // also instance is loaded from JSON that does not support circular dependencies

        for (Object object : Types.asIterable(array)) {
            if (Types.isArrayLike(object)) {
                postProcessArray(object);
                continue;
            }
            if (object instanceof StorageObject) {
                ((StorageObject) object).onCreate(this);
            }
        }
    }

    protected void saveObject(Object object, File file) throws IOException {
        Writer writer = null;
        try {
            Files.mkdirs(file);
            writer = new FileWriter(file);
            Json json = Factory.getInstance(Json.class);
            json.stringify(writer, object);
        } finally {
            Files.close(writer);
        }
    }

    private static final String LOCK_PATH = "lock";

    /**
     * Return absolute path of storage lock file. If this file exists storage is considered loaded.
     *
     * @return lock file absolute path.
     * @see #LOCK_PATH
     */
    protected File getLockFile() {
        return new File(directory, LOCK_PATH);
    }
}
