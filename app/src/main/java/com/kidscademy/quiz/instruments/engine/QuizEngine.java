package com.kidscademy.quiz.instruments.engine;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Counters;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.util.Storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class QuizEngine {
    private static final Log log = LogFactory.getLog(QuizEngine.class);

    public static final int WRONG_ANSWER_TIMEOUT = 3000;

    private static final int QUIZ_COUNT = 10;
    private static final int MAX_TRIES = 3;

    private final Storage storage;
    private final Challenge[] challenges;
    private Challenge currentChallenge;
    private long currentChallengeTimestamp;
    private int nextChallengeIndex;

    private Counters counters;
    private Balance balance;
    private int leftTries;
    private int collectedCredits;

    public QuizEngine() {
        log.trace("QuizEngine()");
        this.storage = App.storage();

        List<Instrument> instruments = new ArrayList<>(Arrays.asList(this.storage.getInstruments()));
        Collections.sort(instruments, new Comparator<Instrument>() {
            @Override
            public int compare(Instrument left, Instrument right) {
                if (left.getQuizCounter() == right.getQuizCounter()) {
                    return ((Integer) right.getRank()).compareTo(left.getRank());
                }
                return ((Integer) left.getQuizCounter()).compareTo(right.getQuizCounter());
            }
        });

        challenges = new Challenge[Math.min(QUIZ_COUNT, instruments.size())];
        for (int i = 0; i < challenges.length; ++i) {
            challenges[i] = new Challenge(instruments.get(i));
        }

        this.counters = storage.getCounters();
        this.balance = storage.getBalance();
        this.leftTries = MAX_TRIES;
    }

    public Instrument nextChallenge() {
        if (nextChallengeIndex == challenges.length) {
            balance.plusCredit(collectedCredits);
            return null;
        }
        currentChallenge = challenges[nextChallengeIndex++];
        currentChallengeTimestamp = System.currentTimeMillis();
        return currentChallenge.instrument;
    }

    /**
     * This method assume it is called after {@link #nextChallenge()} that proper initialize {@link #currentChallenge}
     * instance field.
     *
     * @param optionsCount
     * @return
     */
    public List<String> getOptions(int optionsCount) {
        // options list contains both positive and negative options
        // first takes care to add expected, that is challenge car
        List<String> options = new ArrayList<String>();
        options.add(currentChallenge.instrument.getDisplay());

        // create negative options list with all car options less expected car
        // takes care to not include a similar option many time; e.g. many cars can have the same country
        List<String> negativeOptions = new ArrayList<String>();
        for (Instrument instrument : App.storage().getInstruments()) {
            final String option = instrument.getDisplay();
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
        options.addAll(negativeOptions.subList(0, optionsCount - 1));

        Collections.shuffle(options);
        return options;
    }

    public boolean checkAnswer(String selectedOption, double speedFactor) {
        if (currentChallenge.checkAnswer(selectedOption)) {
            currentChallenge.responseTime = (int) (System.currentTimeMillis() - currentChallengeTimestamp);
            int credits = (int) (Balance.getQuizDifficultyFactor() * Balance.getQuizIncrement() * speedFactor);
            collectedCredits += credits;
            counters.plus(currentChallenge.instrument);
            currentChallenge.instrument.incrementQuizCounter();
            return true;
        }
        counters.minus(currentChallenge.instrument);
        --leftTries;
        return false;
    }

    public void onAswerTimeout() {
        App.audit().quizTimeout(currentChallenge.instrument);
        counters.minus(currentChallenge.instrument);
        --leftTries;
    }

    public int getLeftTries() {
        return leftTries;
    }

    public boolean noMoreTries() {
        return leftTries <= 0;
    }

    public int getCollectedCredits() {
        return collectedCredits;
    }

    public int getSolvedCount() {
        return nextChallengeIndex;
    }

    public int getQuizCount() {
        return QUIZ_COUNT;
    }

    public int getAverageResponseTime() {
        long totalResponsesTime = 0;
        int totalDone = 0;
        for (Challenge challenge : challenges) {
            if (challenge.state == Challenge.State.DONE) {
                totalDone += 1;
                totalResponsesTime += challenge.responseTime;
            }
        }
        return (int) (totalResponsesTime / totalDone);
    }

    private static class Challenge {
        private Instrument instrument;
        private State state;
        private int responseTime;

        public Challenge(Instrument instrument) {
            this.instrument = instrument;
            this.state = State.NONE;
        }

        public boolean checkAnswer(String selectedOption) {
            boolean correctAnswer = selectedOption.equals(instrument.getDisplay());
            state = correctAnswer ? State.DONE : State.FAILED;
            return correctAnswer;
        }

        private enum State {
            NONE, FAILED, DONE
        }
    }
}
