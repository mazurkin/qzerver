package org.qzerver.model.dao.job;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * DAO for ScheduleExecution entity.
 */
@Repository
public interface ScheduleExecutionDao {

    /**
     * Find all last executions.
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findAll(Extraction extraction);

    /**
     * Find all last closed executions.
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findFinished(Extraction extraction);

    /**
     * Find all last executions in progress.
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findEngaged(Extraction extraction);

    /**
     * Find last executions by job.
     * @param scheduleJobId Schedule job identifier
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findByJob(long scheduleJobId, Extraction extraction);

    /**
     * Delete all expired execution records.
     * @param expiration Expiration moment
     * @return Count of deleted records
     */
    int deleteExpired(Date expiration);

    /**
     * Set job reference to null for all execution with specified job.
     * @param scheduleJobId Schedule job identifier
     * @return Count of update records
     */
    int detachJob(long scheduleJobId);

}
