package com.kidscademy.quiz.app;

import com.kidscademy.quiz.model.Device;

public class RemoteLogger {
    /**
     * kids (a)cademy hosts service remote logger logic.
     */
    public static final String SERVER_URL = "http://kids-cademy.com/";

    void recordAuditEvent(String packageName, Device device, String name, String parameter1, String parameter2) {
    }

    void dumpStackTrace(String packageName, Device device, String stackTrace) {
        System.out.println(stackTrace);
    }
}
