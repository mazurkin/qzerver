package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.base.AbstractApplicationEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleExecution extends AbstractApplicationEntity implements Serializable {

    public static final int MAX_NODE_LENGTH = 128;

    public static final int MAX_COMMENT_LENGTH = 1024;

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private Long id;

    /**
     * Job reference
     */
    private ScheduleJob job;

    /**
     * Job name (copied from schedule job)
     */
    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    /**
     * Cron expression (copied from schedule job)
     */
    @NotBlank
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    /**
     * Nodes list
     */
    private List<ScheduleExecutionNode> nodes;

    /**
     * Result list
     */
    private List<ScheduleExecutionResult> results;

    /**
     * Moment when execution should be started (from Quartz)
     */
    @NotNull
    private Date scheduled;

    /**
     * Moment when execution has been actually started (from Quartz)
     */
    @NotNull
    private Date fired;

    /**
     * Moment when execution is registered
     */
    @NotNull
    private Date started;

    /**
     * Moment when execution is finished
     */
    private Date finished;

    /**
     * Whether the execution was started manually
     */
    private boolean forced;

    /**
     * Timeout for execution in milliseconds (copied from cluster). Value 0 means no timeout
     */
    @Min(0)
    private long timeout;

    /**
     * Status of finished execution
     */
    @NotNull
    private ScheduleExecutionStatus status = ScheduleExecutionStatus.INPROGRESS;

    /**
     * Flag indicates that execution must cancel all other pending nodes
     */
    private boolean cancelled;

    /**
     * Execute action on all nodes
     */
    private boolean allNodes;

    /**
     * How many threads use to execute all nodes (value 0 means that execution is sequential with timeout
     * and cancelation)
     */
    @Min(0)
    private int allNodesPool;

    /**
     * Action to execute (copied from schedule job)
     */
    @NotNull
    private ScheduleAction action;

    /**
     * Strategy which was used to select nodes from cluster
     */
    @NotNull
    private ScheduleExecutionStrategy strategy;

    /**
     * Name of host where the execution happened
     */
    @NotBlank
    @Length(max = MAX_NODE_LENGTH)
    private String hostname;

    /**
     * Some comment on an execution (specially for manual execution)
     */
    @Length(max = MAX_COMMENT_LENGTH)
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public ScheduleJob getJob() {
        return job;
    }

    public void setJob(ScheduleJob job) {
        this.job = job;
    }

    public List<ScheduleExecutionNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<ScheduleExecutionNode>();
        }
        return nodes;
    }

    public void setNodes(List<ScheduleExecutionNode> nodes) {
        this.nodes = nodes;
    }

    public List<ScheduleExecutionResult> getResults() {
        if (results == null) {
            results = new ArrayList<ScheduleExecutionResult>();
        }
        return results;
    }

    public void setResults(List<ScheduleExecutionResult> results) {
        this.results = results;
    }

    public Date getFired() {
        return fired;
    }

    public void setFired(Date fired) {
        this.fired = fired;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public ScheduleExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleExecutionStatus status) {
        this.status = status;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public void setAction(ScheduleAction action) {
        this.action = action;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

    public ScheduleExecutionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ScheduleExecutionStrategy strategy) {
        this.strategy = strategy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
