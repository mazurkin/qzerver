package org.qzerver.model.agent.action.providers.executor.jdbc;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class JdbcActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private int modified;

    private boolean succeed;

    private String exceptionClass;

    private String exceptionMessage;

    public int getModified() {
        return modified;
    }

    public void setModified(int modified) {
        this.modified = modified;
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
