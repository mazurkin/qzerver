package org.qzerver.model.service.job.executor;

import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.model.service.job.executor.dto.ManualJobExecutionParameters;
import org.springframework.stereotype.Service;

/**
 * Schedule execution outer functions
 */
@Service
public interface ScheduleJobExecutorService {

    /**
     * Execute schedule job (initiated by Quartz trigger)
     * @param scheduleJobId Schedule job identifier
     * @param parameters Execution parameters
     * @return Status of execution
     */
    ScheduleExecution executeAutomaticJob(long scheduleJobId, AutomaticJobExecutionParameters parameters);

    /**
     * Execute schedule job (inititated manually)
     * @param scheduleJobId Schedule job identifier
     * @param parameters Execution parameters
     * @return Status of execution
     */
    ScheduleExecution executeManualJob(long scheduleJobId, ManualJobExecutionParameters parameters);

}
