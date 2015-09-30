package org.qzerver.model.agent.action.providers.executor.localcommand.threads;

import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExecutionThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessExecutionThread.class);

    private static final String THREAD_NAME = "Local process execution";

    private static final int DEFAULT_EXIT_CODE = 0;

    private final Process process;

    private int exitCode;

    private LocalCommandActionResultStatus status;

    public ProcessExecutionThread(Process process) {
        this.process = process;
        this.exitCode = DEFAULT_EXIT_CODE;
        this.status = LocalCommandActionResultStatus.EXECUTED;

        setDaemon(false);
        setName(THREAD_NAME);
    }

    @Override
    public void run() {
        LOGGER.debug("Local process thread is started");

        // Some nice tips about process termination are noted here: http://kylecartmell.com/?p=9

        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            LOGGER.debug("Local process thread has been interrupted - process will be destroyed");
            status = LocalCommandActionResultStatus.TIMEOUT;
            exitCode = -1;
        }

        LOGGER.debug("Local process thread is finishing with exit code [{}] and status [{}]", exitCode, status);
    }

    public void interruptOnTimeout() {
        this.interrupt();
    }

    public int getExitCode() {
        return exitCode;
    }

    public LocalCommandActionResultStatus getStatus() {
        return status;
    }

}
