package org.qzerver.model.agent.action.providers.executor.http;

import com.gainmatrix.lib.serialization.SerialVersionUID;

import java.io.Serializable;

public class HttpActionOutput implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private HttpActionOutputStatus status;

    private String type;

    private String encoding;

    private byte[] data;

    private String excepionClass;

    private String exceptionMessage;

    public HttpActionOutputStatus getStatus() {
        return status;
    }

    public void setStatus(HttpActionOutputStatus status) {
        this.status = status;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getExcepionClass() {
        return excepionClass;
    }

    public void setExcepionClass(String excepionClass) {
        this.excepionClass = excepionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
