package org.qzerver.model.agent.action;

import com.gainmatrix.lib.serialization.SerialVersionUID;

import java.io.Serializable;

public class ActionAgentResult implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private boolean succeed;

    private byte[] data;

    public ActionAgentResult() {
        this(false, null);
    }

    public ActionAgentResult(boolean succeed, byte[] data) {
        this.data = data;
        this.succeed = succeed;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

}
