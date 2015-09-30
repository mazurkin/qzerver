package org.qzerver.model.agent.action.providers.executor.clazz;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Executor allows to specify any user-defined class as an action. Specified class must be available in classpath and
 * must implement Callable interface. On success Callable.call() method must returns any object which is converted to a
 * string or even might return null. On error it must throw any kind of exception which would be stored as an error
 * information in ClassActionResult instance.
 * @see java.util.concurrent.Callable
 */
public class ClassActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassActionExecutor.class);

    private Validator beanValidator;

    @Override
    public ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress) {
        Preconditions.checkNotNull(actionDefinition, "Action definition is not set");
        Preconditions.checkNotNull(nodeAddress, "Node address is not set");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        ClassActionDefinition definition = (ClassActionDefinition) actionDefinition;

        LOGGER.debug("Callable instance will be executed. Class [{}] on node [{}]",
            definition.getCallableClassName(), nodeAddress);

        Object callable;

        if (definition.getCallableInstance() == null) {
            // Search for class
            Class<?> classToExecute;

            try {
                classToExecute = Class.forName(definition.getCallableClassName());
            } catch (Exception e) {
                return new ClassActionResult(e);
            }

            // Check is it an implementation of java.util.concurrent.Callable
            if (!Callable.class.isAssignableFrom(classToExecute)) {
                return new ClassActionResult(IllegalArgumentException.class.getCanonicalName(),
                    "Class must implement java.util.concurrent.Callable interface", null);
            }

            // Instantiate class object
            try {
                callable = BeanUtils.instantiate(classToExecute);
            } catch (Exception e) {
                return new ClassActionResult(e);
            }
        } else {
            callable = definition.getCallableInstance();
        }

        // Set object properties
        Map<String, String> properties = new HashMap<String, String>(definition.getParameters());
        properties.put("nodeAddress", nodeAddress);
        String scheduleExecutionIdText = Long.toString(scheduleExecutionId);
        properties.put("executionId", scheduleExecutionIdText);

        return executeCallable((Callable<?>) callable, properties);
    }

    private ActionResult executeCallable(Callable<?> callable, Map<String, String> properties) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(callable);
        PropertyValues propertyValues = new MutablePropertyValues(properties);

        try {
            beanWrapper.setPropertyValues(propertyValues, true, false);
        } catch (Exception e) {
            LOGGER.debug("Error while setting bean properties", e);
            return new ClassActionResult(e);
        }

        return executeCallableInitialized(callable);
    }

    private ActionResult executeCallableInitialized(Callable<?> callable) {
        Object resultAsObject;

        try {
            resultAsObject = callable.call();
        } catch (Exception e) {
            LOGGER.debug("Error while calling the callable", e);
            return new ClassActionResult(e);
        }

        String resultAsText = String.valueOf(resultAsObject);
        return new ClassActionResult(resultAsText);
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
