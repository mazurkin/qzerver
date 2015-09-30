package org.qzerver.model.agent.action.providers.marshaller;

import org.qzerver.model.agent.action.providers.ActionResult;

/**
 * Marshaller for action result
 */
public interface ActionResultMarshaller {

    /**
     * Marshall action result
     * @param actionResult Action result object
     * @return Serialized data
     */
    byte[] marshall(ActionResult actionResult);

    /**
     * Unmarshall action result
     * @param actionResultClass Action result class
     * @param result Serialized data
     * @return Action result object
     * @throws ActionResultMarshallerException Exception on error
     */
    ActionResult unmarshall(Class<? extends ActionResult> actionResultClass, byte[] result)
        throws ActionResultMarshallerException;

}
