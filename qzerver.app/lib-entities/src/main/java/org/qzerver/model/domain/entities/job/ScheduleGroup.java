package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.base.AbstractApplicationEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ScheduleGroup extends AbstractApplicationEntity implements Serializable {

    public static final int MAX_NAME_LENGTH = 128;

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private Long id;

    /**
     * Visible group name
     */
    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    /**
     * Jobs list (unsorted)
     */
    private Set<ScheduleJob> jobs;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScheduleJob> getJobs() {
        if (jobs == null) {
            jobs = new HashSet<ScheduleJob>();
        }
        return jobs;
    }

    public void setJobs(Set<ScheduleJob> jobs) {
        this.jobs = jobs;
    }
}

