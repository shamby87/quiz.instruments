package com.kidscademy.quiz.instruments;

import com.kidscademy.quiz.instruments.engine.GameEngine;
import com.kidscademy.quiz.instruments.model.Balance;
import com.kidscademy.quiz.instruments.model.Counters;
import com.kidscademy.quiz.instruments.model.Instrument;
import com.kidscademy.quiz.instruments.model.LevelState;
import com.kidscademy.quiz.instruments.util.Storage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class GameEngineTest {
    @Mock
    private Storage storage;

    private GameEngine engine;

    @Before
    public void beforeTest() {
        when(storage.getInstruments()).thenReturn(new Instrument[]{new Instrument(0, "accordion")});
        when(storage.getCounters()).thenReturn(new Counters());
        when(storage.getBalance()).thenReturn(new Balance());

        engine = new GameEngine(storage, new LevelState(0, 10));
    }

    @Test
    public void initChallenge() {
        List<Integer> instruments = new ArrayList<>(Arrays.asList(0, 1));
        when(storage.getLevelInstruments(0)).thenReturn(instruments);

        Instrument instrument = engine.initChallenge("accordion");
        assertNotNull(instrument);
        assertEquals(0, instrument.getIndex());
        assertEquals("accordion", instrument.getName());
    }

    @Test
    public void nextChallenge() {
        Instrument instrument = engine.nextChallenge();
        assertNotNull(instrument);
    }

    @Test
    public void checkAnswer() {
        assertTrue(engine.checkAnswer("accordion"));
    }

    @Test
    public void wasNextLevelUnlocked() {
        assertTrue(engine.wasNextLevelUnlocked());
    }

    @Test
    public void getUnlockedLevelIndex() {
        int index = engine.getUnlockedLevelIndex();
        assertEquals(1, index);
    }
}
