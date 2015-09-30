package org.qzerver.model.agent.action.providers.executor.sshcommand;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

public class SshCommandActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private int exitCode;

    private SshCommandActionOutput stdout;

    private SshCommandActionOutput stderr;

    private SshCommandActionResultStatus status = SshCommandActionResultStatus.EXECUTED;

    private String exceptionClass;

    private String exceptionMessage;

    private boolean succeed;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public SshCommandActionOutput getStderr() {
        return stderr;
    }

    public void setStderr(SshCommandActionOutput stderr) {
        this.stderr = stderr;
    }

    public SshCommandActionOutput getStdout() {
        return stdout;
    }

    public void setStdout(SshCommandActionOutput stdout) {
        this.stdout = stdout;
    }

    public SshCommandActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(SshCommandActionResultStatus status) {
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
