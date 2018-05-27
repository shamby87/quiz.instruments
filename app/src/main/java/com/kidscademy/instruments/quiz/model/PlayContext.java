package com.kidscademy.instruments.quiz.model;

public enum PlayContext
{
  GAME, QUIZ;

  public static int size()
  {
    return values().length;
  }
}
