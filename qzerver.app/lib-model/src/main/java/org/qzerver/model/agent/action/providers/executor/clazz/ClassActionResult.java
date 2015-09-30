package org.qzerver.model.agent.action.providers.executor.clazz;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class ClassActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private String result;

    private String exceptionClass;

    private String exceptionMessage;

    private String exceptionStacktrace;

    public ClassActionResult() {
    }

    public ClassActionResult(String result) {
        this.result = result;
    }

    public ClassActionResult(String exceptionClass, String exceptionMessage, String exceptionStacktrace) {
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStacktrace = exceptionStacktrace;
    }

    public ClassActionResult(Exception e) {
        this(e.getClass().getCanonicalName(), e.getLocalizedMessage(), ExceptionUtils.getStackTrace(e));
    }

    @Override
    public boolean isSucceed() {
        return exceptionClass == null;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public void setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }
}
