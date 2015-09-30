package org.qzerver.model.dao.job;

import org.springframework.stereotype.Repository;

/**
 * DAO for ScheduleAction entity
 */
@Repository
public interface ScheduleActionDao {

    /**
     * Remove all orphaned actions (i.e all action with archived=true and no executions connected in)
     * @return Number of deleted records
     */
    int deleteOrphaned();

}
