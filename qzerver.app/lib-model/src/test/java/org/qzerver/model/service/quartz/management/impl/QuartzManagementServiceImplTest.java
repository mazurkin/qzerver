package org.qzerver.model.service.quartz.management.impl;

import com.gainmatrix.lib.properties.PropertiesStringWriter;
import com.google.common.collect.Iterators;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.impl.matchers.GroupMatcher;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.model.service.job.executor.dto.ManualJobExecutionParameters;
import org.qzerver.model.service.job.executor.quartz.QzerverJobListener;
import org.qzerver.system.quartz.QzerverKeyUtils;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Test is quite long to execute because it depends on real-time Quartz actions
 */
public class QuartzManagementServiceImplTest {

    private static final String GLOBAL_TIMEZONE = "UTC";

    /**
     * When to plan the event to be fired in milliseconds
     */
    private static final int DEFAULT_SCHEDULE_ADVANCE_MS = 2000;

    /**
     * How long to wait for the event to be fired
     */
    private static final int DEFAULT_SCHEDULE_PAUSE_MS = 5000;

    private static final GroupMatcher<JobKey> ALL_QZERVER_JOBS =
        GroupMatcher.jobGroupEquals(QzerverKeyUtils.QZERVER_GROUP);

    private static final GroupMatcher<TriggerKey> ALL_QZERVER_TRIGGERS =
        GroupMatcher.triggerGroupEquals(QzerverKeyUtils.QZERVER_GROUP);

    private QuartzManagementServiceImpl quartzManagementService;

    private Scheduler scheduler;

    private BlockingQueue<AutomaticJobExecutionParameters> queue;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        PropertiesStringWriter.write(properties, "org.quartz.threadPool.threadCount", 1);
        PropertiesStringWriter.write(properties, "org.quartz.threadPool.threadPriority", Thread.NORM_PRIORITY);
        PropertiesStringWriter.write(properties, "org.quartz.scheduler.skipUpdateCheck", true);

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setAutoStartup(false);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.afterPropertiesSet();

        queue = new LinkedBlockingQueue<AutomaticJobExecutionParameters>();

        ScheduleJobExecutorService scheduleJobExecutorService = new ScheduleJobExecutorServiceMock(queue);

        QzerverJobListener qzerverJobListener = new QzerverJobListener();
        qzerverJobListener.setExecutorService(scheduleJobExecutorService);

        scheduler = schedulerFactoryBean.getObject();
        scheduler.getListenerManager().addJobListener(qzerverJobListener, EverythingMatcher.allJobs());

        quartzManagementService = new QuartzManagementServiceImpl();
        quartzManagementService.setScheduler(scheduler);
    }

    @After
    public void tearDown() throws Exception {
        scheduler.shutdown();
    }

    @Test
    public void testActivity() throws Exception {
        Assert.assertFalse(quartzManagementService.isSchedulerActive());

        quartzManagementService.enableScheduler();
        Assert.assertTrue(quartzManagementService.isSchedulerActive());

        quartzManagementService.disableScheduler();
        Assert.assertFalse(quartzManagementService.isSchedulerActive());

        quartzManagementService.enableScheduler();
        Assert.assertTrue(quartzManagementService.isSchedulerActive());
    }

    @Test
    public void testDisabledJobCreation() throws Exception {
        quartzManagementService.enableScheduler();

        final long jobId = 1234;
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, false);

        List<String> jobGroups = scheduler.getJobGroupNames();
        Assert.assertNotNull(jobGroups);
        Assert.assertEquals(1, jobGroups.size());
        Assert.assertEquals(QzerverKeyUtils.QZERVER_GROUP, jobGroups.get(0));

        List<String> triggerGroups = scheduler.getTriggerGroupNames();
        Assert.assertNotNull(triggerGroups);
        Assert.assertEquals(0, triggerGroups.size());

        Set<JobKey> jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(1, jobKeys.size());
        Assert.assertEquals(jobKey, Iterators.getOnlyElement(jobKeys.iterator()));

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        Assert.assertNotNull(jobDetail);
    }

    @Test
    public void testEnabledJobCreation() throws Exception {
        quartzManagementService.enableScheduler();

        final long jobId = 1234;
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);
        TriggerKey triggerKey = QzerverKeyUtils.triggerKey(jobId);

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, true);

        List<String> jobGroups = scheduler.getJobGroupNames();
        Assert.assertNotNull(jobGroups);
        Assert.assertEquals(1, jobGroups.size());
        Assert.assertEquals(QzerverKeyUtils.QZERVER_GROUP, jobGroups.get(0));

        List<String> triggerGroups = scheduler.getTriggerGroupNames();
        Assert.assertNotNull(triggerGroups);
        Assert.assertEquals(1, triggerGroups.size());
        Assert.assertEquals(QzerverKeyUtils.QZERVER_GROUP, triggerGroups.get(0));

        Set<JobKey> jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(1, jobKeys.size());
        Assert.assertEquals(jobKey, Iterators.getOnlyElement(jobKeys.iterator()));

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(ALL_QZERVER_TRIGGERS);
        Assert.assertNotNull(triggerKeys);
        Assert.assertEquals(1, triggerKeys.size());
        Assert.assertEquals(triggerKey, Iterators.getOnlyElement(triggerKeys.iterator()));

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        Assert.assertNotNull(jobDetail);

        Trigger trigger = scheduler.getTrigger(triggerKey);
        Assert.assertNotNull(trigger);
        Assert.assertNotNull(trigger.getScheduleBuilder());

        Thread.sleep(DEFAULT_SCHEDULE_PAUSE_MS);

        Assert.assertEquals(1, queue.size());
    }

    @Test
    public void testReenableScheduler() throws Exception {
        quartzManagementService.enableScheduler();
        Assert.assertTrue(quartzManagementService.isSchedulerActive());

        final long jobId = 1234;

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, true);

        quartzManagementService.disableScheduler();
        Assert.assertFalse(quartzManagementService.isSchedulerActive());

        quartzManagementService.enableScheduler();
        Assert.assertTrue(quartzManagementService.isSchedulerActive());

        Thread.sleep(DEFAULT_SCHEDULE_PAUSE_MS);

        Assert.assertEquals(1, queue.size());
    }

    @Test
    public void testDeleteJob() throws Exception {
        quartzManagementService.enableScheduler();

        final long jobId = 1234;

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, true);

        quartzManagementService.deleteJob(jobId);

        Set<JobKey> jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(0, jobKeys.size());

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(ALL_QZERVER_TRIGGERS);
        Assert.assertNotNull(triggerKeys);
        Assert.assertEquals(0, triggerKeys.size());

        Thread.sleep(DEFAULT_SCHEDULE_PAUSE_MS);

        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testToggleJob() throws Exception {
        quartzManagementService.enableScheduler();

        final long jobId = 1234;
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);
        TriggerKey triggerKey = QzerverKeyUtils.triggerKey(jobId);

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, true);
        Assert.assertTrue(quartzManagementService.isJobActive(jobId));

        quartzManagementService.disableJob(jobId);
        Assert.assertFalse(quartzManagementService.isJobActive(jobId));

        Set<JobKey> jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(1, jobKeys.size());
        Assert.assertEquals(jobKey, Iterators.getOnlyElement(jobKeys.iterator()));

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(ALL_QZERVER_TRIGGERS);
        Assert.assertNotNull(triggerKeys);
        Assert.assertEquals(0, triggerKeys.size());

        quartzManagementService.enableJob(jobId, jobCron, GLOBAL_TIMEZONE);
        Assert.assertTrue(quartzManagementService.isJobActive(jobId));

        jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(1, jobKeys.size());
        Assert.assertEquals(jobKey, Iterators.getOnlyElement(jobKeys.iterator()));

        triggerKeys = scheduler.getTriggerKeys(ALL_QZERVER_TRIGGERS);
        Assert.assertNotNull(triggerKeys);
        Assert.assertEquals(1, triggerKeys.size());
        Assert.assertEquals(triggerKey, Iterators.getOnlyElement(triggerKeys.iterator()));

        Thread.sleep(DEFAULT_SCHEDULE_PAUSE_MS);

        Assert.assertEquals(1, queue.size());
    }

    @Test
    public void testRescheduleJob() throws Exception {
        quartzManagementService.enableScheduler();

        final long jobId = 1234;
        JobKey jobKey = QzerverKeyUtils.jobKey(jobId);
        TriggerKey triggerKey = QzerverKeyUtils.triggerKey(jobId);

        String jobCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS);
        quartzManagementService.createJob(jobId, jobCron, GLOBAL_TIMEZONE, true);

        String jobRescheduledCron = composeCronExpression(DEFAULT_SCHEDULE_ADVANCE_MS + 10);
        quartzManagementService.rescheduleJob(jobId, jobRescheduledCron, GLOBAL_TIMEZONE);

        Set<JobKey> jobKeys = scheduler.getJobKeys(ALL_QZERVER_JOBS);
        Assert.assertNotNull(jobKeys);
        Assert.assertEquals(1, jobKeys.size());
        Assert.assertEquals(jobKey, Iterators.getOnlyElement(jobKeys.iterator()));

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(ALL_QZERVER_TRIGGERS);
        Assert.assertNotNull(triggerKeys);
        Assert.assertEquals(1, triggerKeys.size());
        Assert.assertEquals(triggerKey, Iterators.getOnlyElement(triggerKeys.iterator()));

        Thread.sleep(DEFAULT_SCHEDULE_PAUSE_MS);

        Assert.assertEquals(1, queue.size());
    }

    private static String composeCronExpression(int advanceMilliseconds) {
        TimeZone timeZone = TimeZone.getTimeZone(GLOBAL_TIMEZONE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.add(Calendar.MILLISECOND, advanceMilliseconds);

        return String.format("%d %d %d %d %d ? %d",
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        );
    }

    private static class ScheduleJobExecutorServiceMock implements ScheduleJobExecutorService {

        private BlockingQueue<AutomaticJobExecutionParameters> queue;

        private ScheduleJobExecutorServiceMock(BlockingQueue<AutomaticJobExecutionParameters> queue) {
            this.queue = queue;
        }

        @Override
        public ScheduleExecution executeAutomaticJob(long scheduleJobId, AutomaticJobExecutionParameters parameters) {
            queue.add(parameters);

            ScheduleExecution dummyExecution = new ScheduleExecution();
            dummyExecution.setHostname("localhost");
            dummyExecution.setStatus(ScheduleExecutionStatus.SUCCEED);

            return dummyExecution;
        }

        @Override
        public ScheduleExecution executeManualJob(long scheduleJobId, ManualJobExecutionParameters parameters) {
            throw new UnsupportedOperationException("Should never happen here");
        }

    }
}
