package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.domain.entities.base.AbstractApplicationEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;

public class ScheduleExecutionResult extends AbstractApplicationEntity implements Serializable {

    public static final int MAX_PAYLOAD_LENGTH = 8 * 1024 * 1024;

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private Long id;

    /**
     * Order of result in execution list
     */
    @Min(0)
    private int orderIndex;

    /**
     * Parent execution entity
     */
    @NotNull
    private ScheduleExecution execution;

    /**
     * Assotiated node
     */
    @NotNull
    private ScheduleExecutionNode node;

    /**
     * Is result successful
     */
    private boolean succeed;

    /**
     * Moment when node execution is started
     */
    @NotNull
    private Date started;

    /**
     * Moment when node execution is finished
     */
    private Date finished;

    /**
     * Result description (XML or JSON)
     */
    @Size(max = MAX_PAYLOAD_LENGTH)
    private byte[] payload;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleExecution getExecution() {
        return execution;
    }

    public void setExecution(ScheduleExecution execution) {
        this.execution = execution;
    }

    public ScheduleExecutionNode getNode() {
        return node;
    }

    public void setNode(ScheduleExecutionNode node) {
        this.node = node;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date created) {
        this.started = created;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
