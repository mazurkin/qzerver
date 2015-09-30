package org.qzerver.model.service.job.management.dto;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.qzerver.model.domain.entities.job.ScheduleAction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;

public class ScheduleJobActionParameters implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotNull
    @Length(max = ScheduleAction.MAX_TYPE_LENGTH)
    private String identifier;

    @Size(max = ScheduleAction.MAX_DEFINITION_LENGTH)
    private byte[] definition;

    public byte[] getDefinition() {
        return definition;
    }

    public void setDefinition(byte[] definition) {
        this.definition = definition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
