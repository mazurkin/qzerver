package org.qzerver.model.agent.action.providers.marshaller;

import org.qzerver.model.agent.action.providers.ActionDefinition;

/**
 * Marshaller for action definition
 */
public interface ActionDefinitionMarshaller {

    /**
     * Marshall action definition
     * @param actionDefinition Action definition object
     * @return Serialized data
     */
    byte[] marshall(ActionDefinition actionDefinition);

    /**
     * Unmarshall action definition
     * @param actionDefinitionClass Action definition class
     * @param definition Seriailized data
     * @return Action definition object
     * @throws ActionDefinitionMarshallerException Exception on error
     */
    ActionDefinition unmarshall(Class<? extends ActionDefinition> actionDefinitionClass, byte[] definition)
        throws ActionDefinitionMarshallerException;

}
