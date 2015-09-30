package org.qzerver.model.agent.action.impl;

import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.model.agent.action.ActionAgentResult;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionDefinition;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionResult;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshaller;

import java.util.Arrays;
import java.util.Map;

public class ActionAgentImplTest {

    private ActionAgentImpl actionAgent;

    private IMocksControl control;

    private ActionResultMarshaller actionResultMarshaller;

    private ActionDefinitionMarshaller actionDefinitionMarshaller;

    private ActionExecutor actionExecutor;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        actionResultMarshaller = control.createMock(ActionResultMarshaller.class);
        actionDefinitionMarshaller = control.createMock(ActionDefinitionMarshaller.class);
        actionExecutor = control.createMock(ActionExecutor.class);

        Map<ActionIdentifier, ActionExecutor> executors = ImmutableMap.<ActionIdentifier, ActionExecutor>builder()
            .put(ActionIdentifier.HTTP, actionExecutor)
            .put(ActionIdentifier.LOCAL_COMMAND, actionExecutor)
            .build();

        actionAgent = new ActionAgentImpl();
        actionAgent.setActionDefinitionMarshaller(actionDefinitionMarshaller);
        actionAgent.setActionResultMarshaller(actionResultMarshaller);
        actionAgent.setExecutors(executors);
    }

    @Test
    public void testSimple() throws Exception {
        HttpActionDefinition httpActionDefinition = new HttpActionDefinition();

        HttpActionResult httpActionResult = new HttpActionResult();
        httpActionResult.setStatusCode(200);
        httpActionResult.setSucceed(true);

        String definitionData = "<definition/>";
        String resultData = "<result/>";
        String address = "10.2.0.1";
        long executionId = 100L;

        control.reset();

        EasyMock.expect(actionDefinitionMarshaller.unmarshall(
            EasyMock.eq(HttpActionDefinition.class),
            EasyMock.<byte[]>anyObject()
        )).andReturn(httpActionDefinition);

        EasyMock.expect(actionExecutor.execute(
            EasyMock.same(httpActionDefinition),
            EasyMock.eq(executionId),
            EasyMock.eq(address)
        )).andReturn(httpActionResult);

        EasyMock.expect(actionResultMarshaller.marshall(
            EasyMock.same(httpActionResult)
        )).andReturn(resultData.getBytes());

        control.replay();

        ActionAgentResult actionAgentResult = actionAgent.executeAction(executionId,
            ActionIdentifier.HTTP.getIdentifier(), definitionData.getBytes(), address);

        control.verify();

        Assert.assertNotNull(actionAgentResult);
        Assert.assertTrue(actionAgentResult.isSucceed());
        Assert.assertTrue(Arrays.equals(resultData.getBytes(), actionAgentResult.getData()));
    }

}
