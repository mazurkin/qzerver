package org.qzerver.model.agent.action.providers.executor.localcommand;

public enum LocalCommandActionOutputStatus {

    /**
     * Output was successfully captured
     */
    CAPTURED(true, false),

    /**
     * Output exceeds allowed size
     */
    OVERFLOWED(true, true),

    /**
     * Capturing thread was terminated beceause of process termination
     */
    TIMEOUT(true, true),

    /**
     * Capturing thread hung and we can't close it in normal way
     */
    HUNG(true, true),

    /**
     * There was an exception while copying
     */
    EXCEPTION(true, true),

    /**
     * Output was skipped because of settings specified
     */
    SKIPPED(false, false),

    /**
     * There is no output because process has not been even started
     */
    IDLE(false, false);

    private final boolean captured;

    private final boolean incomplete;

    private LocalCommandActionOutputStatus(boolean captured, boolean incomplete) {
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
