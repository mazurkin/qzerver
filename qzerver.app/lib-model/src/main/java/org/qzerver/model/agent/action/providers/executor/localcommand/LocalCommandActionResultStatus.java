package org.qzerver.model.agent.action.providers.executor.localcommand;

public enum LocalCommandActionResultStatus {

    /**
     * Command has been executed and exit code is obtained
     */
    EXECUTED,

    /**
     * Command was terminated due to timeout or manual termination
     */
    TIMEOUT,

    /**
     * Exception on executing the command
     */
    EXCEPTION

}
