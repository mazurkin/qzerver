package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.gainmatrix.lib.serialization.SerialVersionUID;

import java.io.Serializable;

public class LocalCommandActionOutput implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private LocalCommandActionOutputStatus status;

    private byte[] data;

    private String exceptionClass;

    private String exceptionMessage;

    public LocalCommandActionOutputStatus getStatus() {
        return status;
    }

    public void setStatus(LocalCommandActionOutputStatus status) {
        this.status = status;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
