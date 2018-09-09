package com.kidscademy.quiz.instruments;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AppConformanceTest.class, PlayGameTest.class, PlayQuizTest.class, BadQuizTest.class})
public class BuildAcceptanceTestSuite {
}
