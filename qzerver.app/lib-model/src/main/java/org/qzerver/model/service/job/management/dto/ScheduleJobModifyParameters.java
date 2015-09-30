package org.qzerver.model.service.job.management.dto;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleJob;

import javax.validation.constraints.Min;

import java.io.Serializable;

public class ScheduleJobModifyParameters implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    @Length(max = ScheduleJob.MAX_DESCRIPTION_LENGTH)
    private String description;

    private boolean notifyOnFailure;

    @Min(0)
    private int nodesLimit;

    @Min(0)
    private int timeout;

    private boolean allNodes;

    @Min(0)
    private int allNodesPool;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotifyOnFailure() {
        return notifyOnFailure;
    }

    public void setNotifyOnFailure(boolean notifyOnFailure) {
        this.notifyOnFailure = notifyOnFailure;
    }

    public int getNodesLimit() {
        return nodesLimit;
    }

    public void setNodesLimit(int nodesLimit) {
        this.nodesLimit = nodesLimit;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAllNodes() {
        return allNodes;
    }

    public void setAllNodes(boolean allNodes) {
        this.allNodes = allNodes;
    }

    public int getAllNodesPool() {
        return allNodesPool;
    }

    public void setAllNodesPool(int allNodesPool) {
        this.allNodesPool = allNodesPool;
    }
}
