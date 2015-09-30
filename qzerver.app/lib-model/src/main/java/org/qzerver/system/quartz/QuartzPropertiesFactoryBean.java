package org.qzerver.system.quartz;

import com.gainmatrix.lib.properties.PropertiesStringWriter;
import org.quartz.impl.jdbcjobstore.DriverDelegate;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.Properties;

public class QuartzPropertiesFactoryBean implements FactoryBean<Properties> {

    private static final long DEFAULT_MISFIRE_THRESHOLD_MS = 60000;

    private static final long DEFAULT_CLUSTER_CHECK_INTERVAL_MS = 3000;

    private static final int DEFAULT_THREAD_COUNT = 10;

    private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;

    private String schema;

    private Properties properties;

    private String instanceName = "QZERVER";

    private String instanceId = "AUTO";

    private long misfireThresholdMs = DEFAULT_MISFIRE_THRESHOLD_MS;

    private long clusterCheckinInterval = DEFAULT_CLUSTER_CHECK_INTERVAL_MS;

    private Class<? extends DriverDelegate> driverDelegateClass;

    private int threadCount = DEFAULT_THREAD_COUNT;

    private int threadPriority = DEFAULT_THREAD_PRIORITY;

    private Map<Object, Object> additionalPropertyMap;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        properties = new Properties();

        // Fixed properties
        PropertiesStringWriter.write(properties, "org.quartz.scheduler.instanceName", instanceName);
        PropertiesStringWriter.write(properties, "org.quartz.scheduler.instanceId", instanceId);
        PropertiesStringWriter.write(properties, "org.quartz.jobStore.misfireThreshold", misfireThresholdMs);
        PropertiesStringWriter.write(properties, "org.quartz.jobStore.clusterCheckinInterval", clusterCheckinInterval);
        PropertiesStringWriter.write(properties, "org.quartz.jobStore.isClustered", true);
        PropertiesStringWriter.write(properties, "org.quartz.jobStore.driverDelegateClass",
            driverDelegateClass.getCanonicalName());
        PropertiesStringWriter.write(properties, "org.quartz.threadPool.threadCount", threadCount);
        PropertiesStringWriter.write(properties, "org.quartz.threadPool.threadPriority", threadPriority);
        PropertiesStringWriter.write(properties, "org.quartz.scheduler.skipUpdateCheck", true);

        // Schema and table prefix
        String tablePrefix = schema != null ? schema + ".QRTZ_" : "QRTZ_";
        PropertiesStringWriter.write(properties, "org.quartz.jobStore.tablePrefix", tablePrefix);

        // Additional properties
        if (additionalPropertyMap != null) {
            properties.putAll(additionalPropertyMap);
        }
    }

    @Override
    public Properties getObject() throws Exception {
        return properties;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setAdditionalPropertyMap(Map<Object, Object> additionalPropertyMap) {
        this.additionalPropertyMap = additionalPropertyMap;
    }

    public void setClusterCheckinIntervalMs(long clusterCheckinInterval) {
        this.clusterCheckinInterval = clusterCheckinInterval;
    }

    public void setDriverDelegateClass(Class<? extends DriverDelegate> driverDelegateClass) {
        this.driverDelegateClass = driverDelegateClass;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setMisfireThresholdMs(long misfireThresholdMs) {
        this.misfireThresholdMs = misfireThresholdMs;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
