package org.qzerver.model.agent.action.providers;

/**
 * Abstract action. Action should consume any checked exceptions and return correct result.
 */
public interface ActionExecutor {

    /**
     * Execute action. Method should catch any exception occured and return a correct result descriptor
     * @param actionDefinition Definition of an action
     * @param scheduleExecutionId Execution identifier
     * @param nodeAddress Node address
     * @return Result of an action
     */
    ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress);

}
