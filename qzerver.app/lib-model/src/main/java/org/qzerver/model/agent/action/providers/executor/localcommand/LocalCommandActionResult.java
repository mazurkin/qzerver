package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.agent.action.providers.ActionResult;

import java.io.Serializable;

/**
 * Result from local command action
 */
public class LocalCommandActionResult implements ActionResult, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    /**
     * Is operation succeeded
     */
    private boolean succeed;

    /**
     * Actual exit code we get from a program
     */
    private int exitCode;

    /**
     * What we got from standard output (or both from standard output and error output). May be null if output is
     * not required
     */
    private LocalCommandActionOutput stdout;

    /**
     * What we got from error output. May be null if output is not required.
     */
    private LocalCommandActionOutput stderr;

    /**
     * Status of execution
     */
    private LocalCommandActionResultStatus status = LocalCommandActionResultStatus.EXECUTED;

    /**
     * Class of the exception (when the status is EXCEPTION)
     */
    private String exceptionClass;

    /**
     * Message of the exception (when the status is EXCEPTION)
     */
    private String exceptionMessage;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public LocalCommandActionOutput getStderr() {
        return stderr;
    }

    public void setStderr(LocalCommandActionOutput stderr) {
        this.stderr = stderr;
    }

    public LocalCommandActionOutput getStdout() {
        return stdout;
    }

    public void setStdout(LocalCommandActionOutput stdout) {
        this.stdout = stdout;
    }

    public LocalCommandActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(LocalCommandActionResultStatus status) {
        this.status = status;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public boolean isSucceed() {
        return succeed;
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
