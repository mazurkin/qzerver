package org.qzerver.model.agent.action.providers.executor.clazz;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;

public class ClassActionDefinition implements ActionDefinition, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    /**
     * Class name of the agent which implements java.util.concurrent.Callable interface
     */
    @NotNull
    @NotBlank
    private String callableClassName;

    /**
     * Properties of the agent to be set
     */
    private Map<String, String> parameters;

    /**
     * A real instance of Callable - this property is not used on production and used in tests only. If this property
     * is set then callableClassName is ignored and new bean is not instantiated
     */
    private transient Callable<?> callableInstance;

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.CLASS;
    }

    public String getCallableClassName() {
        return callableClassName;
    }

    public void setCallableClassName(String callableClassName) {
        this.callableClassName = callableClassName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Callable<?> getCallableInstance() {
        return callableInstance;
    }

    public void setCallableInstance(Callable<?> callableInstance) {
        this.callableInstance = callableInstance;
    }
}
