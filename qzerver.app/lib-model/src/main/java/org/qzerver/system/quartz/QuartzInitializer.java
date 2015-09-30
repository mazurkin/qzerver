package org.qzerver.system.quartz;

import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;
import org.quartz.impl.matchers.EverythingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

public class QuartzInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzInitializer.class);

    @NotNull
    private Scheduler scheduler;

    private JobListener[] jobListeners;

    private TriggerListener[] triggerListeners;

    private SchedulerListener[] schedulerListeners;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        // Register job listeners
        if (jobListeners != null) {
            for (JobListener listener : jobListeners) {
                LOGGER.info("Register quartz job listener [{}]", listener.getName());
                scheduler.getListenerManager().addJobListener(listener, EverythingMatcher.allJobs());
            }
        }

        // Register trigger listeners
        if (triggerListeners != null) {
            for (TriggerListener listener : triggerListeners) {
                LOGGER.info("Register quartz trigger listener [{}]", listener.getName());
                scheduler.getListenerManager().addTriggerListener(listener, EverythingMatcher.allTriggers());
            }
        }

        // Register scheduler listeners
        if (schedulerListeners != null) {
            for (SchedulerListener listener : schedulerListeners) {
                LOGGER.info("Register quartz scheduler listener [{}]", listener.getClass().getCanonicalName());
                scheduler.getListenerManager().addSchedulerListener(listener);
            }
        }
    }

    public void setJobListeners(JobListener[] jobListeners) {
        this.jobListeners = jobListeners;
    }

    public void setSchedulerListeners(SchedulerListener[] schedulerListeners) {
        this.schedulerListeners = schedulerListeners;
    }

    public void setTriggerListeners(TriggerListener[] triggerListeners) {
        this.triggerListeners = triggerListeners;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
