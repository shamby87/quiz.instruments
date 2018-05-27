package com.kidscademy.instruments.quiz.util;

import com.kidscademy.util.AuditBase;

public final class Audit extends AuditBase
{
  private static enum Event
  {
    PLAY_GAME, PLAY_QUIZ, VIEW_BALANCE, RESET_SCORE
  }

  public void playGame(int levelIndex)
  {
    if(enabled) {
      String levelName = "Level " + (levelIndex + 1);
      send(Event.PLAY_GAME, levelName);
    }
  }

  public void playQuiz()
  {
    if(enabled) {
      send(Event.PLAY_QUIZ);
    }
  }

  public void viewBalance()
  {
    if(enabled) {
      send(Event.VIEW_BALANCE);
    }
  }

  public void resetScore()
  {
    if(enabled) {
      send(Event.RESET_SCORE);
    }
  }
}
