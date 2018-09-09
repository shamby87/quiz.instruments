package com.kidscademy.quiz.instruments.model;

import java.util.List;

/**
 * A quiz challenge has both the instrument that need to be identified and options list from which user
 * needs to select the right one. A quiz challenge instance can be reused. For this reason, beside constructor
 * it has {@link #init(List)} method that should be called to initialize instance with options and current
 * timestamp.
 *
 * @author Iulian Rotaru
 */
public class QuizChallenge {
    /**
     * Instrument reference.
     */
    private final Instrument instrument;

    /**
     * Challenge options list has one that is correct and couple random generated.
     */
    private List<String> options;

    /**
     * Quiz challenge state. On challenge instance initialization state is set to {@link State#NONE}. After
     * response is checked, see {@link #checkAnswer(String)} state is update accordingly.
     */
    private State state;

    /**
     * Quiz challenge initialization timestamp. It is used to compute {@link #responseTime}.
     */
    private long timestamp;

    /**
     * Cache computed response time so that it can be retrieved by {@link #getResponseTime()}. Response
     * time value is computed by {@link #checkAnswer(String)}.
     */
    private int responseTime;

    public QuizChallenge(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Initialize challenge instance with options list. Also takes care to initialize {@link #timestamp} and
     * reset state to {@link State#NONE}}. Options list contain the right answer and couple random wrong answers.
     *
     * @param options options list.
     */
    public void init(List<String> options) {
        this.options = options;
        this.state = State.NONE;
        this.timestamp = System.currentTimeMillis();
        this.responseTime = 0;
    }

    public List<String> getOptions() {
        return options;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public String getLocaleName() {
        return instrument.getLocaleName();
    }

    public String getPicturePath() {
        return instrument.getPicturePath();
    }

    public State getState() {
        return state;
    }

    public int getResponseTime() {
        return responseTime;
    }

    /**
     * Test if user selected option is the right answer. Also update response time.
     *
     * @param selectedOption user selected option.
     * @return true if user selected option is the right one.
     */
    public boolean checkAnswer(String selectedOption) {
        responseTime = (int) (System.currentTimeMillis() - timestamp);
        boolean correctAnswer = selectedOption.equals(instrument.getLocaleName());
        state = correctAnswer ? State.SOLVED : State.FAILED;
        return correctAnswer;
    }

    @Override
    public String toString() {
        return instrument.getName();
    }

    /**
     * Quiz challenge state. A challenge can be failed or solved. There is also a non determinate state to
     * signal challenge is not answered yet.
     *
     * @author Iulian Rotaru
     */
    public enum State {
        /**
         * Challenge is not answered yet.
         */
        NONE,
        /**
         * Challenge was correctly solved.
         */
        SOLVED,
        /**
         * Challenge answer was wrong.
         */
        FAILED
    }
}
