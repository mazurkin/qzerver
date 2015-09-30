package org.qzerver.model.agent.action.providers.executor.localcommand.threads;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTimeoutThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTimeoutThread.class);

    private static final String THREAD_NAME = "Process timeout watchdog";

    private final ProcessExecutionThread watched;

    private final int timeoutMs;

    public ProcessTimeoutThread(ProcessExecutionThread watched, int timeoutMs) {
        Preconditions.checkNotNull(watched, "Watched thread is not set");
        Preconditions.checkArgument(timeoutMs > 0, "Timeout must be positive");

        this.watched = watched;
        this.timeoutMs = timeoutMs;

        setName(THREAD_NAME);
        setDaemon(true);
    }

    @Override
    public void run() {
        LOGGER.debug("Watchog thread is started");

        try {
            watched.join(timeoutMs);
        } catch (InterruptedException e) {
            throw new SystemIntegrityException("Watchdog thread is unexpectedly interrupted", e);
        }

        if (watched.isAlive()) {
            LOGGER.debug("Local process will be interrupted because of timeout");
            watched.interruptOnTimeout();
        }

        LOGGER.debug("Watchdog thread is exiting");
    }

}
