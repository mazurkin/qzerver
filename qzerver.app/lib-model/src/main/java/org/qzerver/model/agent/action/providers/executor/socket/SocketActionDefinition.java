package org.qzerver.model.agent.action.providers.executor.socket;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;

public class SocketActionDefinition implements ActionDefinition, Serializable {

    public static final int MIN_PORT_NUMBER = 0;

    public static final int MAX_PORT_NUMBER = 65535;

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotNull
    @Size(min = 1)
    private byte[] message;

    @NotNull
    @Size(min = 1)
    private byte[] expectedResponse;

    @Min(MIN_PORT_NUMBER)
    @Max(MAX_PORT_NUMBER)
    private int port;

    @Min(0)
    private int connectionTimeoutMs;

    @Min(0)
    private int readTimeoutMs;

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionTimeoutMs() {

        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public byte[] getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(byte[] expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.SOCKET;
    }

}
