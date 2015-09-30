package org.qzerver.model.agent.action.providers.executor.clazz;

import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

public class ClassActionExecutorTest extends AbstractModelTest {

    @Resource
    private ActionExecutor classActionExecutor;

    @Test
    public void testMissingClass() throws Exception {
        ClassActionDefinition definition = new ClassActionDefinition();
        definition.setCallableClassName("org.nonexisting.class");
        definition.setParameters(ImmutableMap.of("key1", "value1", "key2", "value2"));

        ClassActionResult result = (ClassActionResult) classActionExecutor.execute(definition, 12, "127.1.2.3");
        Assert.assertNotNull(result);
        Assert.assertEquals("java.lang.ClassNotFoundException", result.getExceptionClass());
    }

    @Test
    public void testWithoutCallable() throws Exception {
        ClassActionDefinition definition = new ClassActionDefinition();
        definition.setCallableClassName(Date.class.getCanonicalName());
        definition.setParameters(ImmutableMap.of("key1", "value1", "key2", "value2"));

        ClassActionResult result = (ClassActionResult) classActionExecutor.execute(definition, 12, "127.1.2.3");
        Assert.assertNotNull(result);
        Assert.assertEquals("java.lang.IllegalArgumentException", result.getExceptionClass());
    }

    @Test
    public void testCallableSucceed() throws Exception {
        SomeCallable someCallable = new SomeCallable(true);

        Map<String, String> parameters = ImmutableMap.<String, String>builder()
            .put("key1", "value1")
            .put("key2", "132")
            .put("key3", "Europe/Moscow")
            .put("key4", "en_GB")
            .put("key5", "true")
            .put("key9", "there is no such a key")
            .build();

        ClassActionDefinition definition = new ClassActionDefinition();
        definition.setCallableClassName("dummy - live instance is provided");
        definition.setCallableInstance(someCallable);
        definition.setParameters(parameters);

        ClassActionResult result = (ClassActionResult) classActionExecutor.execute(definition, 12, "127.1.2.3");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals("72632", result.getResult());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNull(result.getExceptionStacktrace());

        Assert.assertEquals("value1", someCallable.getKey1());
        Assert.assertEquals(132, someCallable.getKey2());
        Assert.assertEquals(TimeZone.getTimeZone("Europe/Moscow"), someCallable.getKey3());
        Assert.assertEquals(new Locale("en", "GB"), someCallable.getKey4());
        Assert.assertEquals(true, someCallable.isKey5());
        Assert.assertNull(someCallable.getKey6());
        Assert.assertEquals("127.1.2.3", someCallable.getNodeAddress());
    }

    @Test
    public void testCallableFailed() throws Exception {
        SomeCallable someCallable = new SomeCallable(false);

        Map<String, String> parameters = ImmutableMap.<String, String>builder()
            .put("key1", "value1")
            .put("key2", "132")
            .put("key3", "Europe/Moscow")
            .put("key4", "en_GB")
            .put("key5", "true")
            .put("key9", "there is no such a key")
            .build();

        ClassActionDefinition definition = new ClassActionDefinition();
        definition.setCallableClassName("dummy - live instance is provided");
        definition.setCallableInstance(someCallable);
        definition.setParameters(parameters);

        ClassActionResult result = (ClassActionResult) classActionExecutor.execute(definition, 12, "127.1.2.3");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertNull(result.getResult());
        Assert.assertEquals("java.lang.IllegalStateException", result.getExceptionClass());
        Assert.assertEquals("Some error happened", result.getExceptionMessage());
        Assert.assertNotNull(result.getExceptionStacktrace());

        Assert.assertEquals("value1", someCallable.getKey1());
        Assert.assertEquals(132, someCallable.getKey2());
        Assert.assertEquals(TimeZone.getTimeZone("Europe/Moscow"), someCallable.getKey3());
        Assert.assertEquals(new Locale("en", "GB"), someCallable.getKey4());
        Assert.assertEquals(true, someCallable.isKey5());
        Assert.assertNull(someCallable.getKey6());
        Assert.assertEquals("127.1.2.3", someCallable.getNodeAddress());
    }

    public static class SomeCallable implements Callable<Long> {

        private String key1;

        private long key2;

        private TimeZone key3;

        private Locale key4;

        private boolean key5;

        private String key6;

        private String nodeAddress;

        private boolean success;

        public SomeCallable(boolean success) {
            this.success = success;
        }

        @Override
        public Long call() throws Exception {
            if (success) {
                return 72632L;
            } else {
                throw new IllegalStateException("Some error happened");
            }
        }

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public long getKey2() {
            return key2;
        }

        public void setKey2(long key2) {
            this.key2 = key2;
        }

        public TimeZone getKey3() {
            return key3;
        }

        public void setKey3(TimeZone key3) {
            this.key3 = key3;
        }

        public Locale getKey4() {
            return key4;
        }

        public void setKey4(Locale key4) {
            this.key4 = key4;
        }

        public boolean isKey5() {
            return key5;
        }

        public void setKey5(boolean key5) {
            this.key5 = key5;
        }

        public String getKey6() {
            return key6;
        }

        public void setKey6(String key6) {
            this.key6 = key6;
        }

        public String getNodeAddress() {
            return nodeAddress;
        }

        public void setNodeAddress(String nodeAddress) {
            this.nodeAddress = nodeAddress;
        }

    }

}
