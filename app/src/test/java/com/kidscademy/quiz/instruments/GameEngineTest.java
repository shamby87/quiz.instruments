package com.kidscademy.quiz.instruments;

import com.kidscademy.quiz.instruments.model.AnswerBuilder;
import com.kidscademy.quiz.instruments.model.AnswerState;
import com.kidscademy.quiz.instruments.model.GameEngine;
import com.kidscademy.quiz.instruments.model.GameEngineImpl;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Counters;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.KeyboardControl;
import com.kidscademy.quiz.instruments.model.Level;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.quiz.instruments.util.Audit;
import com.kidscademy.quiz.instruments.util.Storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineTest {
    private static final Instrument[] INSTRUMENTS = new Instrument[]{
            new Instrument(0, "accordion"), //
            new Instrument(1, "piano")
    };

    // keep in sync with IDs from instruments array
    private static final List<Integer> LEVEL_INSTRUMENTS = Arrays.asList(0, 1);

    @Mock
    private Storage storage;
    @Mock
    private Audit audit;
    @Mock
    private AnswerBuilder answer;
    @Mock
    private KeyboardControl keyboard;

    private GameEngine engine;

    @Before
    public void beforeTest() {
        when(storage.getLevel(0)).thenReturn(new Level(0, LEVEL_INSTRUMENTS));
        when(storage.getLevelState(0)).thenReturn(new LevelState(0, INSTRUMENTS.length));

        when(storage.getLevelInstruments(0)).thenReturn(LEVEL_INSTRUMENTS);
        when(storage.getInstruments()).thenReturn(INSTRUMENTS);

        when(storage.getCounters()).thenReturn(new Counters());
        when(storage.getBalance()).thenReturn(new Balance());

        engine = new GameEngineImpl(storage, audit, answer, keyboard);
        engine.setLevelIndex(0);
    }

    @Test
    public void start() {
        engine.start("accordion");

        Instrument instrument = engine.getCurrentChallenge();
        assertNotNull(instrument);
        assertEquals(0, instrument.getIndex());
        assertEquals("accordion", instrument.getName());
    }

    @Test
    public void nextChallenge() {
        engine.nextChallenge();
        Instrument instrument = engine.getCurrentChallenge();

        assertNotNull(instrument);
        assertEquals(0, instrument.getIndex());
        assertEquals("accordion", instrument.getName());
    }

    @Test
    public void wasNextLevelUnlocked() {
        assertFalse(engine.wasNextLevelUnlocked());
    }

    @Test
    public void getUnlockedLevelIndex() {
        final StringBuilder builder = new StringBuilder();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                builder.append(invocation.getArgument(0));
                return null;
            }
        }).when(answer).addLetter(any(Character.class));

        when(answer.hasAllLetters()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return builder.length() == 9;
            }
        });

        when(answer.getValue()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return builder.toString();
            }
        });

        when(storage.getNextLevel(0)).thenReturn(new LevelState(1, INSTRUMENTS.length));

        engine.start(null);
        Instrument instrument = engine.getCurrentChallenge();

        String instrumentName = instrument.getName();
        int i = 0;
        for (int length = instrumentName.length() - 1; i < length; ++i) {
            assertEquals(AnswerState.FILLING, engine.handleAnswerLetter(instrumentName.charAt(i)));
        }
        assertEquals(AnswerState.CORRECT, engine.handleAnswerLetter(instrumentName.charAt(i)));

        assertEquals(1, engine.getUnlockedLevelIndex());
        assertEquals(1, engine.getLevelSolvedChallengesCount());
        assertFalse(engine.wasNextLevelUnlocked());
    }

    @Test
    public void getLevelIndex() {
        assertEquals(0, engine.getLevelIndex());
    }

    @Test
    public void getLevelChallengesCount() {
        assertEquals(2, engine.getLevelChallengesCount());
    }

    @Test
    public void getLevelSolvedChallengesCount() {
        assertEquals(0, engine.getLevelSolvedChallengesCount());
    }
}
