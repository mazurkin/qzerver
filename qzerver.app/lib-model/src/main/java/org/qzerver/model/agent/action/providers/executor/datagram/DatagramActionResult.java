package org.qzerver.model.agent.action.providers.executor.datagram;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class DatagramActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private byte[] response;

    private boolean succeed;

    private DatagramActionResultStatus status;

    private String exceptionClass;

    private String exceptionMessage;

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public DatagramActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(DatagramActionResultStatus status) {
        this.status = status;
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

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public boolean isSucceed() {
        return succeed;
    }
}
