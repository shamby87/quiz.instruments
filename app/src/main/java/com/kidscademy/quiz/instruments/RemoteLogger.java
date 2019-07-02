package com.kidscademy.quiz.instruments;

import com.kidscademy.quiz.instruments.model.Device;

public class RemoteLogger {
    /**
     * kids (a)cademy sync hosts service remoteLogger logic.
     */
    public static final String SERVER_URL = "http://kids-cademy.com/";

    public void recordAuditEvent(String packageName, Device device, String name, String parameter1, String parameter2) {
    }

    void dumpStackTrace(String packageName, Device device, String stackTrace) {
        System.out.println(stackTrace);
    }
}
