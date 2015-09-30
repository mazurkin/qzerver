package org.qzerver.model.service.quartz.management.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.qzerver.model.service.job.executor.quartz.QzerverJob;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.qzerver.system.quartz.QzerverKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.TimeZone;

@Transactional(propagation = Propagation.REQUIRED)
public class QuartzManagementServiceImpl implements QuartzManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManagementServiceImpl.class);

    @NotNull
    private Scheduler scheduler;

    @Override
    public boolean isSchedulerActive() {
        try {
            return !scheduler.isInStandbyMode();
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to check quartz activity", e);
        }
    }

    @Override
    public void enableScheduler() {
        try {
            if (scheduler.isInStandbyMode()) {
                LOGGER.debug("Turn Quartz on");
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to start quartz", e);
        }
    }

    @Override
    public void disableScheduler() {
        try {
            if (!scheduler.isInStandbyMode()) {
                LOGGER.debug("Turn Quartz off");
                scheduler.standby();
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to stop quartz", e);
        }
    }

    @Override
    public void createJob(long jobId, String cron, String timeZoneId, boolean enabled) {
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);

        JobDetail jobDetail = JobBuilder.newJob()
                .withIdentity(jobKey)
                .storeDurably(true)
                .requestRecovery(false)
                .ofType(QzerverJob.class)
                .build();

        try {
            scheduler.addJob(jobDetail, false);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to create quartz job", e);
        }

        if (enabled) {
            Trigger trigger = createJobCronTrigger(jobId, cron, timeZoneId);

            try {
                scheduler.scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to schedule quartz job on job creation", e);
            }
        }
    }

    @Override
    public void deleteJob(long jobId) {
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);

        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to delete quartz job", e);
        }
    }

    @Override
    public void rescheduleJob(long jobId, String cron, String timeZoneId) {
        Trigger trigger = createJobCronTrigger(jobId, cron, timeZoneId);

        try {
            scheduler.rescheduleJob(trigger.getKey(), trigger);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to replace quartz trigger on reschedule", e);
        }
    }

    @Override
    public boolean isJobActive(long jobId) {
        TriggerKey triggerKey = QzerverKeyUtils.triggerKey(jobId);

        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                return true;
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to get trigger by key", e);
        }

        return false;
    }

    @Override
    public void disableJob(long jobId) {
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);

        List<? extends Trigger> triggers;
        try {
            triggers = scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to get triggers list", e);
        }

        for (Trigger trigger : triggers) {
            try {
                scheduler.unscheduleJob(trigger.getKey());
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to unschedule quartz trigger", e);
            }
        }
    }

    @Override
    public void enableJob(long jobId, String cron, String timeZoneId) {
        if (isJobActive(jobId)) {
            return;
        }

        Trigger trigger = createJobCronTrigger(jobId, cron, timeZoneId);

        try {
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to schedule quartz job on job enabling", e);
        }
    }

    private Trigger createJobCronTrigger(long jobId, String cron, String timeZoneId) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
            .withMisfireHandlingInstructionDoNothing()
            .inTimeZone(timeZone);

        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);
        TriggerKey triggerKey = QzerverKeyUtils.triggerKey(jobId);

        return TriggerBuilder.newTrigger()
            .forJob(jobKey)
            .withIdentity(triggerKey)
            .withSchedule(scheduleBuilder)
            .startNow()
            .build();
    }

    @Required
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
