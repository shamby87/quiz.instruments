package com.kidscademy.quiz.instruments.model;

import com.kidscademy.quiz.instruments.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import js.log.Log;
import js.log.LogFactory;

/**
 * Quiz engine implementation.
 *
 * @author Iulian Rotaru
 */
public class QuizEngineImpl implements QuizEngine {
    private static final Log log = LogFactory.getLog(QuizEngineImpl.class);

    private static final int QUIZ_COUNT = 10;
    private static final int OPTIONS_COUNT = 6;
    private static final int MAX_TRIES = 3;
    private static final int QUIZ_TIMEOUT = 8000;

    private final Listener listener;

    private final Balance balance;
    private final Timeout timeout;

    private final QuizChallenge[] challenges;
    private QuizChallenge currentChallenge;
    private int nextChallengeIndex;

    private int leftTries;
    private int collectedCredits;

    public QuizEngineImpl(Listener listener) {
        log.trace("QuizEngine(Listener)"); // NON-NLS

        this.listener = listener;
        this.timeout = new Timeout();

        List<Instrument> instruments = new ArrayList<>(Arrays.asList(App.storage().getInstruments()));
        Collections.sort(instruments, new Comparator<Instrument>() {
            @SuppressWarnings("UseCompareMethod")
            @Override
            public int compare(Instrument left, Instrument right) {
                if (left.getQuizCounter() == right.getQuizCounter()) {
                    return ((Integer) right.getRank()).compareTo(left.getRank());
                }
                return ((Integer) left.getQuizCounter()).compareTo(right.getQuizCounter());
            }
        });

        challenges = new QuizChallenge[Math.min(QUIZ_COUNT, instruments.size())];
        for (int i = 0; i < challenges.length; ++i) {
            challenges[i] = new QuizChallenge(instruments.get(i));
        }

        this.balance = App.storage().getBalance();
        this.leftTries = MAX_TRIES;
    }

    @Override
    public QuizChallenge nextChallenge() {
        if (nextChallengeIndex == challenges.length) {
            balance.plusCredit(collectedCredits);
            return null;
        }

        timeout.start();
        currentChallenge = challenges[nextChallengeIndex++];

        // options list contains both positive and negative options
        // first takes care to add expected, that is challenge car
        List<String> options = new ArrayList<>();
        options.add(currentChallenge.getInstrument().getLocaleName());

        // init negative options list with all car options less expected car
        // takes care to not include a similar option many time; e.g. many cars can have the same country
        List<String> negativeOptions = new ArrayList<>();
        for (Instrument instrument : App.storage().getInstruments()) {
            final String option = instrument.getLocaleName();
            if (options.contains(option)) {
                continue;
            }
            if (negativeOptions.contains(option)) {
                continue;
            }
            negativeOptions.add(option);
        }
        Collections.shuffle(negativeOptions);

        // add negative options till options list is full, i.e. reaches requested options counter argument
        options.addAll(negativeOptions.subList(0, OPTIONS_COUNT - 1));
        Collections.shuffle(options);
        currentChallenge.init(options);

        return currentChallenge;
    }

    @Override
    public boolean checkAnswer(String option) {
        timeout.stop();

        if (currentChallenge.checkAnswer(option)) {
            double speedFactor = 3 - (2 * currentChallenge.getResponseTime() / QUIZ_TIMEOUT);
            int credits = (int) (Balance.getQuizDifficultyFactor() * Balance.getQuizIncrement() * speedFactor);
            collectedCredits += credits;
            currentChallenge.getInstrument().incrementQuizCounter();
            return true;
        }

        --leftTries;
        return false;
    }

    @Override
    public void cancelChallenge() {
        timeout.stop();
    }

    @Override
    public int getResponseTime() {
        return currentChallenge.getResponseTime();
    }

    @Override
    public int getLeftTries() {
        return leftTries;
    }

    @Override
    public int getCollectedCredits() {
        return collectedCredits;
    }

    @Override
    public int getSolvedChallengesCount() {
        return nextChallengeIndex;
    }

    @Override
    public int getTotalChallengesCount() {
        return QUIZ_COUNT;
    }

    @Override
    public int getAverageResponseTime() {
        long totalResponsesTime = 0;
        int totalDone = 0;
        for (QuizChallenge quizChallenge : challenges) {
            if (quizChallenge.getState() == QuizChallenge.State.SOLVED) {
                totalDone += 1;
                totalResponsesTime += quizChallenge.getResponseTime();
            }
        }
        return (int) (totalResponsesTime / totalDone);
    }

    /**
     * Quiz challenge timeout.
     *
     * @author Iulian Rotaru
     */
    private class Timeout {
        private Timer timer;
        private TimerTask task;
        private long startTimestamp;

        void start() {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        int elapsedTime = (int) (System.currentTimeMillis() - startTimestamp);
                        QuizEngineImpl.this.listener.onQuizProgress(100 * elapsedTime / QUIZ_TIMEOUT);
                        if (elapsedTime >= QUIZ_TIMEOUT) {
                            Timeout.this.stop();
                            --QuizEngineImpl.this.leftTries;
                            QuizEngineImpl.this.listener.onQuizTimeout();
                        }
                    } catch (Throwable t) {
                        Timeout.this.stop();
                        log.error(t);
                    }
                }
            };

            startTimestamp = System.currentTimeMillis();
            timer = new Timer();
            timer.schedule(task, 0, 40);
        }

        /**
         * Cancel this timer.
         */
        void stop() {
            // do no use timer.cancel() because timer cannot be reused after cancel
            timer.cancel();
            timer.purge();
        }
    }
}
