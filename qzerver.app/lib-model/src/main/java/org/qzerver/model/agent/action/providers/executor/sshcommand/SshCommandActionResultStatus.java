package org.qzerver.model.agent.action.providers.executor.sshcommand;

public enum SshCommandActionResultStatus {

    /**
     * Command was successfully executed
     */
    EXECUTED,

    /**
     * There was an exception while executing
     */
    EXCEPTION,

    /**
     * Execution was interrupted because of timeout
     */
    TIMEOUT

}
