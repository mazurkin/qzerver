package org.qzerver.model.agent.action.providers.marshaller.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.codehaus.jackson.map.ObjectMapper;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshallerException;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotNull;

import java.io.IOException;

public class ActionDefinitionJsonMarshaller implements ActionDefinitionMarshaller {

    @NotNull
    private ObjectMapper objectMapper;

    @Override
    public byte[] marshall(ActionDefinition actionDefinition) {
        Preconditions.checkNotNull(actionDefinition, "Action definition is null");

        byte[] result;

        try {
            result = objectMapper.writeValueAsBytes(actionDefinition);
        } catch (IOException e) {
            String message = "Fail to marshall definition for identifier " + actionDefinition;
            throw new SystemIntegrityException(message, e);
        }

        return result;
    }

    @Override
    public ActionDefinition unmarshall(Class<? extends ActionDefinition> actionDefinitionClass, byte[] definition)
        throws ActionDefinitionMarshallerException
    {
        Preconditions.checkNotNull(actionDefinitionClass, "Action definition class is null");
        Preconditions.checkNotNull(definition, "Definition data array is null");

        ActionDefinition actionDefinition;
        try {
            actionDefinition = objectMapper.readValue(definition, actionDefinitionClass);
        } catch (IOException e) {
            String message = "Fail to unmarshall action definition for class " + actionDefinitionClass;
            throw new ActionDefinitionMarshallerException(message, e);
        }

        return actionDefinition;
    }

    @Required
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
