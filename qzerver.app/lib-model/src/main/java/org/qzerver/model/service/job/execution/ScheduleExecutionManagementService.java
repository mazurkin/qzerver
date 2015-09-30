package org.qzerver.model.service.job.execution;

import com.gainmatrix.lib.business.exception.AbstractServiceException;
import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Schedule execution functions
 */
@Service
public interface ScheduleExecutionManagementService {

    /**
     * Register start of execution
     * @param scheduleJobId Job identifier
     * @param parameters Execution parameters
     * @return Schedule job entity
     */
    ScheduleExecution startExecution(long scheduleJobId, StartExecutionParameters parameters);

    /**
     * Register start of node execution
     * @param scheduleExecutionNodeId Schedule execution node identifier
     * @return Schedule execution entity
     * @throws AbstractServiceException Exception on error, includes:
     * <ul>
     *     <li>{@link org.qzerver.model.service.job.execution.exception.ExecutionAlreadyFinishedException} - Can't
     *     register result start on finished execution</li>
     * </ul>
     */
    ScheduleExecutionResult startExecutionResult(long scheduleExecutionNodeId) throws AbstractServiceException;

    /**
     * Register finish of node execution
     * @param scheduleExecutionResultId Schedule execution result identifier
     * @param succeed Does the action succeed
     * @param data Result
     * @return Schedule execution entity
     * @throws AbstractServiceException Exception on error, includes:
     * <ul>
     *     <li>{@link org.qzerver.model.service.job.execution.exception.ResultAlreadyFinishedException} - Can't
     *     finish result registration on already finished result</li>
     * </ul>
     */
    ScheduleExecutionResult finishExecutionResult(long scheduleExecutionResultId, boolean succeed, byte[] data)
        throws AbstractServiceException;

    /**
     * Register finish of the whole execution
     * @param scheduleExecutionId Schedule execution identifier
     * @param status Status of execution
     * @return Schedule execution entity
     * @throws AbstractServiceException Exception on error, includes:
     * <ul>
     *     <li>{@link org.qzerver.model.service.job.execution.exception.ExecutionAlreadyFinishedException} - Can't
     *     finish execution when it is already finished</li>
     * </ul>
     */
    ScheduleExecution finishExecution(long scheduleExecutionId, ScheduleExecutionStatus status)
        throws AbstractServiceException;

    /**
     * Request execution entity
     * @param scheduleExecutionId Schedule execution identifier
     * @return Schedule execution entity
     */
    ScheduleExecution findExecution(long scheduleExecutionId);

    /**
     * Set cancellation flag
     * @param scheduleExecutionId Schedule execution identifier
     * @return Schedule execution entity
     * @throws AbstractServiceException Exception on error, includes:
     * <ul>
     *     <li>{@link org.qzerver.model.service.job.execution.exception.ExecutionAlreadyFinishedException} - Can't
     *     cancel finished execution</li>
     * </ul>
    */
    ScheduleExecution cancelExecution(long scheduleExecutionId) throws AbstractServiceException;

    /**
     * Get all last executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findAll(Extraction extraction);

    /**
     * Get all last closed executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findFinished(Extraction extraction);

    /**
     * Get all last executions in progress
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findEngaged(Extraction extraction);

    /**
     * Get last executions by job
     * @param scheduleJobId Schedule job identifier
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findByJob(long scheduleJobId, Extraction extraction);

}
