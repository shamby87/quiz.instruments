package com.kidscademy.quiz.instruments.model;

public enum PlayContext
{
  GAME, QUIZ;

  public static int size()
  {
    return values().length;
  }
}
