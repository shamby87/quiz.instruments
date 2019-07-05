package com.kidscademy.quiz.app;

public class RemoteLogger {
    /**
     * kids (a)cademy hosts service remote logger logic.
     */
    public static final String SERVER_URL = "http://kids-cademy.com/";

    void dumpStackTrace(String packageName, String stackTrace) {
        System.out.println(stackTrace);
    }
}
