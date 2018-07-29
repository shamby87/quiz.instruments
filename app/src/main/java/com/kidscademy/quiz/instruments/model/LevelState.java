package com.kidscademy.quiz.instruments.model;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.util.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Level mutable state.
 *
 * @author Iulian Rotaru
 */
public class LevelState {
    private static final float UNLOCK_TRESHOLD = 0.5F;

    private int index;
    /**
     * Level size is the number of instruments from the level.
     */
    private int size;
    private List<Integer> solvedInstruments;
    /**
     * Level accumulated points.
     */
    private int score;
    private boolean unlocked;

    /**
     * Default constructor for storage serialization.
     */
    public LevelState() {
    }

    public LevelState(int index, int size) {
        this.index = index;
        this.size = size;
        this.solvedInstruments = new ArrayList<Integer>(size);
    }

    public int getIndex() {
        return index;
    }

    /**
     * Mark instrument as solved.
     *
     * @param index global instrument index from storage.
     */
    public void solveInstrument(int index) {
        solvedInstruments.add(index);
    }

    public int getSolvedInstrumentsCount() {
        return solvedInstruments.size();
    }

    /**
     * Test if instrument from list position is marked as solved. Given instrument position is
     * translated into instrument index into master collection considering this level index.
     *
     * @param position position of the instrument on user interface list or grid.
     * @return true if requested instrument is solved for current level.
     */
    public boolean isSolvedInstrument(int position) {
        return solvedInstruments.contains(index * size + position);
    }

    public void reset() {
        solvedInstruments.clear();
        unlocked = false;
    }

    /**
     * Get the number of instruments this level has.
     *
     * @return this level size.
     * @see #size
     */
    public int getSize() {
        return size;
    }

    public List<Integer> getUnsolvedInstruments(Storage storage) {
        List<Integer> unsolvedInstruments = new ArrayList<>(storage.getLevelInstruments(index));
        unsolvedInstruments.removeAll(solvedInstruments);
        return unsolvedInstruments;
    }

    public boolean isUnlockThreshold() {
        return (float) solvedInstruments.size() / (float) size >= UNLOCK_TRESHOLD;
    }

    public void unlock() {
        unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean isComplete() {
        return solvedInstruments.size() == size;
    }

    public void forceComplete() {
        // TODO: hack currently used by quiz
        // a quiz can complete even with errors meaning that after quiz pass there may be not guessed instruments
        // for now this condition is simply ignored; maybe to add logic to re-play the quiz
        // meanwhile consider quiz complete by initializing solved brands with all level brands list
        solvedInstruments = App.storage().getLevelInstruments(index);
    }

    public void minusScore(int points) {
        score -= points;
        if (score < 0) {
            score = 0;
        }
    }

    public void plusScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }
}
