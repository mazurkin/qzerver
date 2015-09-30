package org.qzerver.model.service.job.execution.exception;

import com.gainmatrix.lib.business.exception.AbstractServiceException;

/**
 * Can't perform an operation on closed execution result
 */
public class ResultAlreadyFinishedException extends AbstractServiceException  {

    private final long scheduleExecutionResultId;

    public ResultAlreadyFinishedException(long scheduleExecutionResultId) {
        this.scheduleExecutionResultId = scheduleExecutionResultId;
    }

    public long getScheduleExecutionResultId() {
        return scheduleExecutionResultId;
    }
}
