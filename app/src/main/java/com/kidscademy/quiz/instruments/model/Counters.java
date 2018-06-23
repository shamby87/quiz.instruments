package com.kidscademy.quiz.instruments.model;

import java.util.HashMap;
import java.util.Map;

public class Counters
{
  private Map<Instrument, Value> values = new HashMap<>();

  public void plus(Instrument instrument)
  {
    value(instrument).plus();
  }

  public void minus(Instrument instrument)
  {
    value(instrument).minus();
  }

  public double getScore(Instrument instrument)
  {
    return value(instrument).getScore();
  }

  public void reset()
  {
    values.clear();
  }

  private Value value(Instrument instrument)
  {
    Value value = values.get(instrument);
    if(value == null) {
      value = new Value();
      values.put(instrument, value);
    }
    return value;
  }

  public static class Value implements Comparable<Value>
  {
    private int positive;
    private int negative;

    public void plus()
    {
      ++positive;
    }

    public void minus()
    {
      ++negative;
    }

    public double getScore()
    {
      final double total = positive + negative;
      return total != 0 ? positive / total : 0;
    }

    @Override
    public int compareTo(Value other)
    {
      return ((Double)this.getScore()).compareTo(other.getScore());
    }
  }
}
