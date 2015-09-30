package org.qzerver.model.dao.job;

import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for ScheduleJob entity
 */
@Repository
public interface ScheduleJobDao {

    /**
     * Find all jobs which are connected to the specified cluster group.
     * @param clusterGroupId Cluster group identifier
     * @return List of schedule jobs
     */
    List<ScheduleJob> findAllByClusterGroup(long clusterGroupId);

}
