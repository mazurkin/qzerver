package org.qzerver.model.agent.action.providers.executor.sshcommand;

public enum SshCommandActionOutputStatus {

    /**
     * Output was successfully captured
     */
    CAPTURED(true, false),

    /**
     * Output was captured but exceeds the limit
     */
    OVERFLOWED(true, true),

    /**
     * Output was partially captured but the process has been terminated
     */
    TIMEOUT(true, true),

    /**
     * Output was not captured because of the settings
     */
    SKIPPED(false, false),

    /**
     * Output was not captured because command has not been executed
     */
    IDLE(false, false);

    private final boolean captured;

    private final boolean incomplete;

    private SshCommandActionOutputStatus(boolean captured, boolean incomplete) {
        this.captured = captured;
        this.incomplete = incomplete;
    }

    public boolean isCaptured() {
        return captured;
    }

    public boolean isIncomplete() {
        return incomplete;
    }
}
