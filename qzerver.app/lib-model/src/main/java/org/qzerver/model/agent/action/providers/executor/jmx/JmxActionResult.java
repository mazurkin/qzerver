package org.qzerver.model.agent.action.providers.executor.jmx;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class JmxActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private String result;

    private String exceptionClass;

    private String exceptionMessage;

    private JmxActionResultStatus status;

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public JmxActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(JmxActionResultStatus status) {
        this.status = status;
    }

    @Override
    public boolean isSucceed() {
        return exceptionClass == null;
    }

}
