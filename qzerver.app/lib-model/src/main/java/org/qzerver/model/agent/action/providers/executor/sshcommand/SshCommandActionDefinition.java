package org.qzerver.model.agent.action.providers.executor.sshcommand;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;

public class SshCommandActionDefinition implements ActionDefinition, Serializable {

    public static final int MIN_PORT_NUMBER = 0;

    public static final int MAX_PORT_NUMBER = 65535;

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private static final int DEFAULT_EXPECTED_EXIT_CODE = 0;

    private static final int DEFAULT_SSH_PORT = 22;

    @Min(MIN_PORT_NUMBER)
    @Max(MAX_PORT_NUMBER)
    private int port = DEFAULT_SSH_PORT;

    @NotNull
    @NotBlank
    private String username;

    private String password;

    private String privateKeyPath;

    private String privateKeyPassphrase;

    private String knownHostPath;

    private boolean agentForwarding;

    @NotNull
    private String command;

    private Map<String, String> environmentVariables;

    private Map<String, String> sshProperties;

    @Min(0)
    private int connectionTimeoutMs;

    @Min(0)
    private int timeoutMs;

    private boolean skipStdOutput;

    private boolean skipStdError;

    private int expectedExitCode = DEFAULT_EXPECTED_EXIT_CODE;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExpectedExitCode() {
        return expectedExitCode;
    }

    public void setExpectedExitCode(int expectedExitCode) {
        this.expectedExitCode = expectedExitCode;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    public boolean isAgentForwarding() {
        return agentForwarding;
    }

    public void setAgentForwarding(boolean agentForwarding) {
        this.agentForwarding = agentForwarding;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean isSkipStdOutput() {
        return skipStdOutput;
    }

    public void setSkipStdOutput(boolean skipStdOutput) {
        this.skipStdOutput = skipStdOutput;
    }

    public boolean isSkipStdError() {
        return skipStdError;
    }

    public void setSkipStdError(boolean skipStdError) {
        this.skipStdError = skipStdError;
    }

    public String getKnownHostPath() {
        return knownHostPath;
    }

    public void setKnownHostPath(String knownHostPath) {
        this.knownHostPath = knownHostPath;
    }

    public Map<String, String> getSshProperties() {
        return sshProperties;
    }

    public void setSshProperties(Map<String, String> sshProperties) {
        this.sshProperties = sshProperties;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.SSH_COMMAND;
    }

}
