package org.qzerver.model.service.job.executor.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.listeners.JobListenerSupport;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.springframework.beans.factory.annotation.Required;

public class QzerverJobListener extends JobListenerSupport {

    protected static final String SERVICE_NAME = "ScheduleJobExecutorService";

    private static final String LISTENER_NAME = "QZERVER job listener";

    private ScheduleJobExecutorService executorService;

    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.put(SERVICE_NAME, executorService);
    }

    @Required
    public void setExecutorService(ScheduleJobExecutorService executorService) {
        this.executorService = executorService;
    }

}
