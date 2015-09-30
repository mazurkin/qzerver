package org.qzerver.web.controller.system;

import com.gainmatrix.lib.serialization.SerialVersionUID;

import java.io.Serializable;

public class SystemStateModel implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private boolean scheduleActive;

    public boolean isScheduleActive() {
        return scheduleActive;
    }

    public void setScheduleActive(boolean scheduleActive) {
        this.scheduleActive = scheduleActive;
    }
}
