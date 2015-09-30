package org.qzerver.model.agent.action.providers.executor.http;

public enum HttpActionOutputStatus {

    CAPTURED(true, false),

    TIMEOUT(true, true),

    OVERFLOWED(true, true),

    EXCEPTION(true, true),

    SKIPPED(false, false),

    IDLE(false, false);

    private final boolean captured;

    private final boolean incomplete;

    private HttpActionOutputStatus(boolean captured, boolean incomplete) {
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
