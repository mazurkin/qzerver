package org.qzerver.model.service.quartz.management;

import org.springframework.stereotype.Service;

/**
 * Facade to Quartz scheduler internals
 */
@Service
public interface QuartzManagementService {

    /**
     * Check current state of Quartz engine (current node only)
     * @return true if Quartz is active
     */
    boolean isSchedulerActive();

    /**
     * Turn Quartz scheduler on (current node only)
     */
    void enableScheduler();

    /**
     * Turn Quartz scheduler off (current node only)
     */
    void disableScheduler();

    /**
     * Create quartz job
     * @param jobId Unique job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     * @param enabled Does the job should be started right after creation
     */
    void createJob(long jobId, String cron, String timeZoneId, boolean enabled);

    /**
     * Delete job
     * @param jobId Job identifier
     */
    void deleteJob(long jobId);

    /**
     * Change cron expression of the exising job
     * @param jobId Job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     */
    void rescheduleJob(long jobId, String cron, String timeZoneId);

    /**
     * Enable the existing job with cron expression (create the trigger for job)
     * @param jobId Job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     */
    void enableJob(long jobId, String cron, String timeZoneId);

    /**
     * Disable the existing job (delete all job triggers)
     * @param jobId Job identifier
     */
    void disableJob(long jobId);

    /**
     * Check is job enabled
     * @param jobId Jon identifier
     * @return Returns true if job is enabled
     */
    boolean isJobActive(long jobId);

}
