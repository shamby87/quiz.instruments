package com.kidscademy.quiz.instruments.model;

/**
 * Game engine model. A game engine instance is always created for a level and it takes care to run throw level
 * challenges till level complete or user exit. After game engine instance creation, is mandatory to call
 * {@link #setLevelIndex(int)} and initialize the level.
 * <p>
 * After game engine creation and level initialization we are ready to start. We can start from first
 * available challenge or we can start with a particular one, identified by its name, see {@link #start(String)}.
 * Engine start prepares internal challenge so that {@link #getCurrentChallenge()} can return it.
 * <p>
 * Once current challenge initialized, engine is prepared to process answer - see {@link #handleAnswerLetter(char)}.
 * Answer is processed letter by letter till engine detects correct answer or answer builder is filled.
 * When answer is complete engine check if correct and if so is ready to prepare next challenge, see {@link #nextChallenge()}.
 * Process is repeated till no more challenges in which case next challenge returns false.
 *
 * @author Iulian Rotaru
 */
public interface GameEngine {
    /**
     * Initialize game engine level. This method should be first called on game engine instance.
     *
     * @param levelIndex zero based level index.
     */
    void setLevelIndex(int levelIndex);

    /**
     * Start game engine with optional instrument name. Prepare internal challenged instrument to requested
     * instrument, if instrument name is not null or first level instrument if provided instrument name
     * is null.
     *
     * @param challengeName optional challenge name, null for first one.
     */
    void start(String challengeName);

    /**
     * Prepare next challenge that will be accessible via {@link #getCurrentChallenge()}} and signal if level complete.
     * If level is complete this method returns false and set internal challenged instrument to null.
     *
     * @return true if level has more challenges and false if level is complete.
     */
    boolean nextChallenge();

    /**
     * Get current challenge. This method should be called after {@link #start(String)} or {@link #nextChallenge()}
     * methods.
     *
     * @param <T> auto-cast to right side variable.
     * @return current challenge.
     */
    <T> T getCurrentChallenge();

    /**
     * Handle answer letter.
     *
     * @param letter answer letter.
     * @return current challenge answer state.
     */
    AnswerState handleAnswerLetter(char letter);

    /**
     * Get zero based index of level wrapped by this engine instance.
     *
     * @return this engine level index.
     */
    int getLevelIndex();

    /**
     * Get total challenges count of this game engine level.
     *
     * @return total level challenges count.
     */
    int getLevelChallengesCount();

    /**
     * Get solved challenges count of this game engine level.
     *
     * @return level solved challenges count.
     */
    int getLevelSolvedChallengesCount();

    /**
     * Get unlocked level index.
     *
     * @return unlocked level index.
     */
    int getUnlockedLevelIndex();

    /**
     * Test if next level was unlocked.
     *
     * @return true if next level was unlocked.
     */
    boolean wasNextLevelUnlocked();

    /**
     * Skip current challenge and prepare another one. If there is only one single challenge not solved
     * yet, keep it.
     */
    void skipChallenge();

    /**
     * Reveal first missing letter from current challenge answer. This operation requires <code>credits</code>. If
     * user has not enough credits accumulated, letter reveal operation is not executed and this method returns false.
     *
     * @return true if letter reveal was performed.
     */
    boolean revealLetter();

    /**
     * Test if user has enough credits for input verify operation.
     *
     * @return true if input verify operation is allowed.
     */
    boolean isInputVerifyAllowed();

    /**
     * Hide unused letters from keyboard. This operation is performed only if user has enough credits.
     *
     * @return true if keyboard unused letters was hidden.
     */
    boolean hideLetters();

    /**
     * Experimental. Not implemented yet.
     *
     * @return always false.
     */
    boolean playSample();

    /**
     * Get user balance score.
     *
     * @return balance score.
     */
    int getScore();

    /**
     * Get user balance credits.
     *
     * @return balance credits.
     */
    int getCredit();

    /**
     * Test if this user has credits.
     *
     * @return true if user has credits.
     */
    boolean hasCredit();
}
