package org.qzerver.model.service.job.management;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.dto.ScheduleJobActionParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Schedule job management
 */
@Service
public interface ScheduleJobManagementService {

    /**
     * Create schedule job group
     * @param name Name of the group
     * @return Group entity
     */
    ScheduleGroup createGroup(String name);

    /**
     * Delete job group
     * @param scheduleGroupId Job group v
     */
    void deleteGroup(long scheduleGroupId);

    /**
     * Find group by identifier
     * @param scheduleGroupId Group identifier
     * @return Group entity
     */
    ScheduleGroup findGroup(long scheduleGroupId);

    /**
     * Find all groups
     * @param extraction Extraction
     * @return List of all groups
     */
    List<ScheduleGroup> findAllGroups(Extraction extraction);

    /**
     * Modify group
     * @param scheduleGroupId Group identifier
     * @param name Name
     * @return Group entity
     */
    ScheduleGroup modifyGroup(long scheduleGroupId, String name);

    /**
     * Create new scheduler job
     * @param parameters Job creation parameters
     * @return Job entity
     */
    ScheduleJob createJob(ScheduleJobCreateParameters parameters);

    /**
     * Modify existing job
     * @param scheduleJobId Job identifier
     * @param parameters Job modification parameters
     * @return Job entity
     */
    ScheduleJob modifyJob(long scheduleJobId, ScheduleJobModifyParameters parameters);

    /**
     * Change the action for the job
     * @param scheduleJobId Job identifier
     * @param parameters Action parameters
     * @return Job entity
     */
    ScheduleJob changeJobAction(long scheduleJobId, ScheduleJobActionParameters parameters);

    /**
     * Move the job to other group
     * @param scheduleJobId Job identifier
     * @param scheduleGroupId Schedule group id
     * @return Job entity
     */
    ScheduleJob changeJobGroup(long scheduleJobId, long scheduleGroupId);

    /**
     * Reschedule job
     * @param scheduleJobId Job identifier
     * @param parameters Job reschedule parameters
     * @return Job entity
     */
    ScheduleJob rescheduleJob(long scheduleJobId, ScheduleJobRescheduleParameters parameters);

    /**
     * Enable job
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob enableJob(long scheduleJobId);

    /**
     * Disable job
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob disableJob(long scheduleJobId);

    /**
     * Get job description
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob findJob(long scheduleJobId);

    /**
     * Delete job
     * @param scheduleJobId Job identifier
     */
    void deleteJob(long scheduleJobId);

}
