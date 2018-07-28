package com.kidscademy.quiz.instruments.model;

import com.kidscademy.quiz.instruments.App;

import java.util.List;

import js.lang.BugError;

/**
 * Level has a zero based index and a list of indices for child instruments.
 *
 * @author Iulian Rotaru
 */
public class Level {
    /**
     * Zero based level index.
     */
    private int index;

    /**
     * Array of indices to this level instruments.
     */
    private List<Integer> instrumentIndices;

    /**
     * Default constructor for storage serialization.
     */
    public Level() {
    }

    /**
     * Create level instance.
     *
     * @param index       zero based level index,
     * @param instruments level instruments indices.
     */
    public Level(int index, List<Integer> instruments) {
        this.index = index;
        this.instrumentIndices = instruments;
    }

    public int getIndex() {
        return index;
    }

    public List<Integer> getInstrumentIndices() {
        return instrumentIndices;
    }

    public int getInstrumentsCount() {
        return instrumentIndices.size();
    }
}
