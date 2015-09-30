package org.qzerver.model.agent.action.providers.executor.jmx.beans;

public interface SampleJmxServiceMBean {

    String method1(String arg1, String arg2);

    Long method2(String arg1);

    void method3() throws Exception;

    long method4(long value1, byte value2, Integer value3);

    void method5() throws Exception;

}
