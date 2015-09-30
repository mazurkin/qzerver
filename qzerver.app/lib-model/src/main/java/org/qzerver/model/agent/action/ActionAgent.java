package org.qzerver.model.agent.action;

import com.gainmatrix.lib.beans.ID;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.springframework.stereotype.Service;

/**
 * Action executor
 */
@Service
public interface ActionAgent {

    /**
     * Executes an action with specified parameters and returns result
     * @param scheduleExecutionId Schedule execution identifier (can be used as an unique parameter for each query)
     * @param identifier Action type
     * @param definition Definition of the action
     * @param address Address where to execute
     * @return Result of the action
     */
    ActionAgentResult executeAction(ID<Long, ScheduleExecution> scheduleExecutionId,
            String identifier, byte[] definition, String address);

    /**
     * Check is specified execution is perfomed right now
     * @param scheduleExecutionId Schedule execution identifier
     * @return Returns true if action is executing
     */
    boolean checkActionExecuting(long scheduleExecutionId);

}
