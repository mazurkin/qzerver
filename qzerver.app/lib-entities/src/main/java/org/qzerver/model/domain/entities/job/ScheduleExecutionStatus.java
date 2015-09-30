package org.qzerver.model.domain.entities.job;

import com.google.common.base.Preconditions;

public enum ScheduleExecutionStatus {

    /**
     * Execution is created and in progress
     */
    INPROGRESS(0),

    /**
     * Execution succeed
     */
    SUCCEED(1),

    /**
     * All nodes failed
     */
    FAILED(2),

    /**
     * Execution was cancelled
     */
    CANCELED(3),

    /**
     * Unexpected exception was thrown (internal error)
     */
    EXCEPTION(4),

    /**
     * Duration of execution was limited and failed
     */
    TIMEOUT(5),

    /**
     * No nodes available (node list is empty because cluster is empty or all nodes are disabled)
     */
    EMPTYNODES(6);

    ScheduleExecutionStatus(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

}
