package org.qzerver.model.agent.action.providers.executor.groovy;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionPlaceholders;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

public class GroovyActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyActionExecutor.class);

    private Validator beanValidator;

    @Override
    public ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress) {
        Preconditions.checkNotNull(actionDefinition, "Action definition is not set");
        Preconditions.checkNotNull(nodeAddress, "Node address is not set");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        GroovyActionDefinition definition = (GroovyActionDefinition) actionDefinition;

        LOGGER.debug("Groovy action will be executed on node node [{}]", nodeAddress);

        Binding binding = new Binding();
        binding.setVariable(ActionPlaceholders.NODE_VARIABLE, nodeAddress);
        binding.setVariable(ActionPlaceholders.EXECUTION_VARIABLE, scheduleExecutionId);

        GroovyShell shell = new GroovyShell(binding);

        try {
            Object value = shell.evaluate(definition.getScript());
            return produceSuccessResult(value);
        } catch (Exception e) {
            LOGGER.debug("Fail to execute groovy script", e);
            return produceExceptionalResult(e);
        }
    }

    private GroovyActionResult produceExceptionalResult(Exception e) {
        GroovyActionResult result = new GroovyActionResult();
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        return result;
    }

    private GroovyActionResult produceSuccessResult(Object value) {
        String valueAsText = String.valueOf(value);

        GroovyActionResult result = new GroovyActionResult();
        result.setResult(valueAsText);
        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

}
