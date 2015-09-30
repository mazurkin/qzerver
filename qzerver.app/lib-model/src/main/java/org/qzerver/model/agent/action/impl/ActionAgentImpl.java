package org.qzerver.model.agent.action.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.agent.action.ActionAgentResult;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshallerException;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshaller;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Transactional(propagation = Propagation.NEVER)
public class ActionAgentImpl implements ActionAgent {

    @NotNull
    private ActionResultMarshaller actionResultMarshaller;

    @NotNull
    private ActionDefinitionMarshaller actionDefinitionMarshaller;

    @NotNull
    private Map<ActionIdentifier, ActionExecutor> executors;

    private final ConcurrentMap<Long, Boolean> executionTracker;

    public ActionAgentImpl() {
        this.executionTracker = new ConcurrentHashMap<Long, Boolean>();
    }

    @Override
    public ActionAgentResult executeAction(long scheduleExecutionId,
        String identifier, byte[] definition, String address)
    {
        Preconditions.checkNotNull(identifier, "Identifier is null");
        Preconditions.checkNotNull(definition, "Definition is null");
        Preconditions.checkNotNull(address, "Address is null");

        // Search for identifier
        ActionIdentifier actionIdentifier = ActionIdentifier.findByIdentifier(identifier);

        // Unmarshall action definition
        ActionDefinition actionDefinition;

        try {
            actionDefinition = actionDefinitionMarshaller.unmarshall(actionIdentifier.getActionDefinitionClass(),
                definition);
        } catch (ActionDefinitionMarshallerException e) {
            throw new SystemIntegrityException("Fail to unmarshall definition", e);
        }

        // Search for action executor
        ActionExecutor actionExecutor = executors.get(actionIdentifier);
        if (actionExecutor == null) {
            throw new NullPointerException("Executor is not found for identifier " + identifier);
        }

        // Check: is action in progress
        Boolean alreadyExists = executionTracker.putIfAbsent(scheduleExecutionId, true);
        if (alreadyExists != null) {
            throw new IllegalStateException("Action is already perfomed for execution=#" + scheduleExecutionId);
        }

        // Execute action
        ActionResult actionResult;

        try {
            actionResult = actionExecutor.execute(actionDefinition, scheduleExecutionId, address);
            if (actionResult == null) {
                String message = String.format("Action result is null for execution=#[%d] and node=[%s]",
                    scheduleExecutionId, address);
                throw new NullPointerException(message);
            }
        } finally {
            executionTracker.remove(scheduleExecutionId);
        }

        // Marshall execution result
        byte[] actionResultData = actionResultMarshaller.marshall(actionResult);

        // Compose the result record
        ActionAgentResult actionAgentResult = new ActionAgentResult();
        actionAgentResult.setSucceed(actionResult.isSucceed());
        actionAgentResult.setData(actionResultData);

        return actionAgentResult;
    }

    @Override
    public boolean checkActionExecuting(long scheduleExecutionId) {
        return executionTracker.keySet().contains(scheduleExecutionId);
    }

    @Required
    public void setActionDefinitionMarshaller(ActionDefinitionMarshaller actionDefinitionMarshaller) {
        this.actionDefinitionMarshaller = actionDefinitionMarshaller;
    }

    @Required
    public void setActionResultMarshaller(ActionResultMarshaller actionResultMarshaller) {
        this.actionResultMarshaller = actionResultMarshaller;
    }

    @Required
    public void setExecutors(Map<ActionIdentifier, ActionExecutor> executors) {
        this.executors = executors;
    }

}
