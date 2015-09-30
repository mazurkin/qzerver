package org.qzerver.model.agent.action.providers.executor.groovy;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class GroovyActionDefinition implements ActionDefinition, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotNull
    @NotBlank
    private String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.GROOVY;
    }
}
