package org.qzerver.util;

import com.gainmatrix.lib.time.impl.StubChronometer;

public class ChronometerUpdateThread extends Thread {

    private final StubChronometer chronometer;

    private final long sleepMs;

    private final long deltaMs;

    private ChronometerUpdateThread(StubChronometer chronometer, long sleepMs, long deltaMs) {
        this.chronometer = chronometer;
        this.sleepMs = sleepMs;
        this.deltaMs = deltaMs;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            // do nothing
        }

        chronometer.shiftTick(deltaMs);
    }

    public static void start(StubChronometer chronometer, long sleepMs, long deltaMs) {
        Thread thread = new ChronometerUpdateThread(chronometer, sleepMs, deltaMs);
        thread.setDaemon(true);
        thread.setName("Test thread for chronometer updating");
        thread.start();
    }

}

