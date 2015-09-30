package org.qzerver.model.service.job.execution.dto;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.qzerver.model.domain.entities.job.ScheduleExecution;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StartExecutionParameters implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    /**
     * When the execution was exactly scheduled on
     */
    @NotNull
    private Date scheduled;

    /**
     * When the execution was started
     */
    @NotNull
    private Date fired;

    /**
     * Is the execution forced by an operator
     */
    private boolean manual;

    /**
     * Some comment for the execution
     */
    @Length(max = ScheduleExecution.MAX_COMMENT_LENGTH)
    private String comment;

    /**
     * Force to use this address instead of cluster group or localhost
     */
    private List<String> addresses;

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

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
}
