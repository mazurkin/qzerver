package org.qzerver.model.agent.action.providers.marshaller.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.codehaus.jackson.map.ObjectMapper;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshallerException;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotNull;

import java.io.IOException;

public class ActionResultJsonMarshaller implements ActionResultMarshaller {

    @NotNull
    private ObjectMapper objectMapper;

    @Override
    public byte[] marshall(ActionResult actionResult) {
        Preconditions.checkNotNull(actionResult, "Action result is null");

        byte[] result;

        try {
            result = objectMapper.writeValueAsBytes(actionResult);
        } catch (IOException e) {
            throw new SystemIntegrityException("Fail to marshall result", e);
        }

        return result;
    }

    @Override
    public ActionResult unmarshall(Class<? extends ActionResult> actionResultClass, byte[] result)
        throws ActionResultMarshallerException
    {
        Preconditions.checkNotNull(actionResultClass, "Action result class is null");
        Preconditions.checkNotNull(result, "Result data array is null");

        ActionResult actionResult;
        try {
            actionResult = objectMapper.readValue(result, actionResultClass);
        } catch (IOException e) {
            String message = "Fail to unmarshall action result for identifier " + actionResultClass;
            throw new ActionResultMarshallerException(message, e);
        }

        return actionResult;
    }

    @Required
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
