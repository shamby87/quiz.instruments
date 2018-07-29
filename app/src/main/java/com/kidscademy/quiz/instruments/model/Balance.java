package com.kidscademy.quiz.instruments.model;

/**
 * Balance class keeps user score points and earned credits.
 *
 * @author Iulian Rotaru
 */
public class Balance {
    private static final int SCORE_INCREMENT_BASE = 10;
    private static final int SCORE_PENALTY = 1;
    private static final int SCORE_LEVEL_UNLOCK_BONUS = 50;
    private static final int SCORE_LEVEL_COMPLETE_BONUS = 100;

    private static final int QUIZ_INCREMENT_BASE = 3;

    // keep in sync minimum credit with lowest deduction value
    private static final int MINIMUM_CREDIT = 10;
    private static final int REVEAL_LETTER_DEDUCTION = 10;
    private static final int VERIFY_INPUT_DEDUCTION = 20;
    private static final int HIDE_LETTERS_DEDUCTION = 30;
    private static final int SAY_NAME_DEDUCTION = 40;

    public static int getScoreIncrement(int levelIndex) {
        return SCORE_INCREMENT_BASE + levelIndex;
    }

    public static int getScoreLevelCompleteBonus(int levelIndex) {
        return SCORE_LEVEL_COMPLETE_BONUS;
    }

    public static int getScoreLevelUnlockBonus(int levelIndex) {
        return SCORE_LEVEL_UNLOCK_BONUS;
    }

    public static int getScorePenalty() {
        return SCORE_PENALTY;
    }

    public static int getQuizDifficultyFactor() {
        return 1;
    }

    public static int getQuizIncrement() {
        return QUIZ_INCREMENT_BASE;
    }

    public static int getRevealLetterDeduction() {
        return REVEAL_LETTER_DEDUCTION;
    }

    public static int getVerifyInputDeduction() {
        return VERIFY_INPUT_DEDUCTION;
    }

    public static int getHideLettersDeduction() {
        return HIDE_LETTERS_DEDUCTION;
    }

    public static int getSayNameDeduction() {
        return SAY_NAME_DEDUCTION;
    }

    private int score;
    private int credit;
    private int minResponseTime = Integer.MAX_VALUE;
    private int maxResponseTime = Integer.MIN_VALUE;

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

    public void minusCredit(int units) {
        credit -= units;
        if (credit < 0) {
            credit = 0;
        }
    }

    public void plusCredit(int units) {
        credit += units;
    }

    public int getCredit() {
        return credit;
    }

    public boolean hasCredit() {
        return credit >= MINIMUM_CREDIT;
    }

    public boolean deductRevealLetter() {
        if (credit < REVEAL_LETTER_DEDUCTION) {
            return false;
        }
        credit -= REVEAL_LETTER_DEDUCTION;
        return true;
    }

    public boolean deductVerifyInput() {
        if (credit < VERIFY_INPUT_DEDUCTION) {
            return false;
        }
        credit -= VERIFY_INPUT_DEDUCTION;
        return true;
    }

    public boolean deductHideLettersInput() {
        if (credit < HIDE_LETTERS_DEDUCTION) {
            return false;
        }
        credit -= HIDE_LETTERS_DEDUCTION;
        return true;
    }

    public boolean deductSayName() {
        if (credit < SAY_NAME_DEDUCTION) {
            return false;
        }
        credit -= SAY_NAME_DEDUCTION;
        return true;
    }

    public void updateResponseTime(int responseTime) {
        if (minResponseTime > responseTime) {
            minResponseTime = responseTime;
        }
        if (maxResponseTime < responseTime) {
            maxResponseTime = responseTime;
        }
    }

    public boolean hasResponseTime() {
        return minResponseTime != Integer.MAX_VALUE;
    }

    public int getMinResponseTime() {
        return minResponseTime;
    }

    public int getMaxResponseTime() {
        return maxResponseTime;
    }

    public void reset() {
        score = 0;
        credit = 0;
        minResponseTime = Integer.MAX_VALUE;
        maxResponseTime = Integer.MIN_VALUE;
    }
}
