package org.qzerver.model.service.job.execution.exception;

import com.gainmatrix.lib.business.exception.AbstractServiceException;

/**
 * Operation can't be executed because execution is finished
 */
public class ExecutionAlreadyFinishedException extends AbstractServiceException  {

    private final long schedulerExecutionId;

    public ExecutionAlreadyFinishedException(long schedulerExecutionId) {
        this.schedulerExecutionId = schedulerExecutionId;
    }

    public long getSchedulerExecutionId() {
        return schedulerExecutionId;
    }

}
