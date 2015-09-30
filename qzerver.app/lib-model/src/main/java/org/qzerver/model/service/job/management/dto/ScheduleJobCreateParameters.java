package org.qzerver.model.service.job.management.dto;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStrategy;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.system.validation.Cron;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;

public class ScheduleJobCreateParameters implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    @Length(max = ScheduleJob.MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotBlank
    @Cron
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    @NotBlank
    @Length(max = ScheduleJob.MAX_TIMEZONE_LENGTH)
    private String timezone;

    @NotNull
    @Length(max = ScheduleAction.MAX_TYPE_LENGTH)
    private String actionIdentifier;

    @Size(max = ScheduleAction.MAX_DEFINITION_LENGTH)
    private byte[] actionDefinition;

    @NotNull
    private ScheduleExecutionStrategy strategy = ScheduleExecutionStrategy.CIRCULAR;

    private boolean notifyOnFailure;

    @Min(0)
    private int nodesLimit;

    @Min(0)
    private int timeout;

    private Long clusterGroupId;

    private long scheduleGroupId;

    private boolean enabled;

    private boolean allNodes;

    @Min(0)
    private int allNodesPool;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

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

    public Long getClusterGroupId() {
        return clusterGroupId;
    }

    public void setClusterGroupId(Long clusterGroupId) {
        this.clusterGroupId = clusterGroupId;
    }

    public long getScheduleGroupId() {
        return scheduleGroupId;
    }

    public void setScheduleGroupId(long scheduleGroupId) {
        this.scheduleGroupId = scheduleGroupId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public ScheduleExecutionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ScheduleExecutionStrategy strategy) {
        this.strategy = strategy;
    }

    public int getNodesLimit() {
        return nodesLimit;
    }

    public void setNodesLimit(int nodesLimit) {
        this.nodesLimit = nodesLimit;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getActionIdentifier() {
        return actionIdentifier;
    }

    public void setActionIdentifier(String actionIdentifier) {
        this.actionIdentifier = actionIdentifier;
    }

    public byte[] getActionDefinition() {
        return actionDefinition;
    }

    public void setActionDefinition(byte[] actionDefinition) {
        this.actionDefinition = actionDefinition;
    }

    public boolean isNotifyOnFailure() {
        return notifyOnFailure;
    }

    public void setNotifyOnFailure(boolean notifyOnFailure) {
        this.notifyOnFailure = notifyOnFailure;
    }
}

