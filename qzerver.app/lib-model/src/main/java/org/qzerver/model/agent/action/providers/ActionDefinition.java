package org.qzerver.model.agent.action.providers;

/**
 * Definition of the action
 */
public interface ActionDefinition {

    /**
     * Return the unique string which describes this type of action
     * @return Unique type identifier
     */
    ActionIdentifier getIdentifier();

}
