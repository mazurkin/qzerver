package org.qzerver.system.quartz;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * Some static helper function to handle Quartz keys
 */
public final class QzerverKeyUtils {

    public static final String QZERVER_GROUP = "QZERVER_GROUP";

    private QzerverKeyUtils() {
    }

    public static boolean isQzerverJob(JobKey jobKey) {
        return QZERVER_GROUP.equals(jobKey.getGroup());
    }

    /**
     * Parse Quartz job name to ScheduleJob identifier.
     * @param jobKey Quartz job key
     * @return Identifier
     */
    public static long parseJobName(JobKey jobKey) {
        return Long.parseLong(jobKey.getName());
    }

    /**
     * Format Quatz job name from identifier.
     * @param jobId Identifier
     * @return Quartz job name
     */
    public static String formatJobName(long jobId) {
        return Long.toString(jobId);
    }

    /**
     * Create Quartz trigger key from identifier
     * @param jobId Job identifier
     * @return Quartz trigger key
     */
    public static TriggerKey triggerKey(long jobId) {
        String jobName = formatJobName(jobId);
        return TriggerKey.triggerKey(jobName, QZERVER_GROUP);
    }

    /**
     * Create Quartz job key fron identifier
     * @param jobId Job identifier
     * @return Quartz job key
     */
    public static JobKey jobKey(long jobId) {
        String jobName = formatJobName(jobId);
        return JobKey.jobKey(jobName, QZERVER_GROUP);
    }

}
