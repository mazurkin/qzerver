package org.qzerver.model.agent.action.providers.executor.http;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class HttpActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private int statusCode;

    private String reason;

    private boolean succeed;

    private HttpActionResultStatus status;

    private String exceptionClass;

    private String exceptionMessage;

    private HttpActionOutput output;

    public HttpActionOutput getOutput() {
        return output;
    }

    public void setOutput(HttpActionOutput output) {
        this.output = output;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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

    public HttpActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(HttpActionResultStatus status) {
        this.status = status;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isSucceed() {
        return succeed;
    }
}
