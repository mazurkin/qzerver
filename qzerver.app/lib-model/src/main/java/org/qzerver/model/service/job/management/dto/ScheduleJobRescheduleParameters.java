package org.qzerver.model.service.job.management.dto;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.system.validation.Cron;

import java.io.Serializable;

public class ScheduleJobRescheduleParameters implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotBlank
    @Cron
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    @NotBlank
    @Length(max = ScheduleJob.MAX_TIMEZONE_LENGTH)
    private String timezone;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
