package org.qzerver.model.service.job.executor.quartz;

import com.google.common.base.Preconditions;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.system.quartz.QzerverKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class QzerverJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(QzerverJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Preconditions.checkNotNull(context, "Context is not set");

        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();

        LOGGER.debug("Quartz job [name={}, group={}] is fired", jobKey.getName(), jobKey.getGroup());

        if (!QzerverKeyUtils.isQzerverJob(jobKey)) {
            LOGGER.warn("Unknown job [name={}, group={}] is fired", jobKey.getName(), jobKey.getGroup());
            return;
        }

        ScheduleJobExecutorService executorService =
            (ScheduleJobExecutorService) context.get(QzerverJobListener.SERVICE_NAME);
        Preconditions.checkNotNull(executorService, "Executor service is not set");

        long scheduleJobId = QzerverKeyUtils.parseJobName(jobKey);

        AutomaticJobExecutionParameters parameters = new AutomaticJobExecutionParameters();
        parameters.setFiredTime(context.getFireTime());
        parameters.setScheduledTime(context.getScheduledFireTime());
        parameters.setNextFireTime(context.getNextFireTime());

        try {
            executorService.executeAutomaticJob(scheduleJobId, parameters);
        } catch (Exception e) {
            LOGGER.error("Fail to execute job with id : " + scheduleJobId, e);
        }
    }

}
