package org.qzerver.model.agent.action.providers.executor.groovy;

import junit.framework.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

public class GroovyActionExecutorTest extends AbstractModelTest {

    @Resource
    private ActionExecutor groovyActionExecutor;

    @Test
    public void testNormal() throws Exception {
        GroovyActionDefinition definition = new GroovyActionDefinition();
        definition.setScript("return '[' + node + '|' + execution + ']'");

        GroovyActionResult result = (GroovyActionResult) groovyActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals("[localhost|123]", result.getResult());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
    }

    @Test
    public void testException() throws Exception {
        GroovyActionDefinition definition = new GroovyActionDefinition();
        definition.setScript("blah-bla");

        GroovyActionResult result = (GroovyActionResult) groovyActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertNull(result.getResult());
        Assert.assertNotNull(result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());
    }
}
