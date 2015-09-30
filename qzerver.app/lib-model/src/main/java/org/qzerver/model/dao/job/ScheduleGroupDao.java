package org.qzerver.model.dao.job;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.job.ScheduleGroup;

import java.util.List;

/**
 * DAO for ScheduleGroup entity.
 */
public interface ScheduleGroupDao {

    /**
     * Find all schedule groups sorted by name
     * @param extraction Extraction
     * @return List of schedule groups
     */
    List<ScheduleGroup> findAll(Extraction extraction);

}
