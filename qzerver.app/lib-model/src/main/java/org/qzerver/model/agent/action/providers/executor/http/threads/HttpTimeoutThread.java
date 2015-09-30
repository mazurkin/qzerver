package org.qzerver.model.agent.action.providers.executor.http.threads;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTimeoutThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTimeoutThread.class);

    private static final String THREAD_NAME = "Http capture timeout watchdog";

    private final HttpOutputThread watched;

    private final int timeoutMs;

    public HttpTimeoutThread(HttpOutputThread watched, int timeoutMs) {
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
            LOGGER.debug("Http capturing will be interrupted because of timeout");
            watched.interruptByTimeout();
        }

        LOGGER.debug("Watchdog thread is exiting");
    }

}
