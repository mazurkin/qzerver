package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.Min;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class LocalCommandActionDefinition implements ActionDefinition, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private static final int DEFAULT_EXPECTED_EXIT_CODE = 0;

    /**
     * Command to execute
     */
    @NotBlank
    private String command;

    /**
     * Additional parameters for command
     */
    private List<String> parameters;

    /**
     * Working folder
     */
    private String directory;

    /**
     * Additional environment variables
     */
    private Map<String, String> environmentVariables;

    /**
     * Wether to inherit current environment variable
     */
    private boolean environmentInherit;

    /**
     * Do we need to capture standard output ('true' means we would skip output)
     */
    private boolean skipStdOutput;

    /**
     * Do we need to capture error output ('true' means we would skip output)
     */
    private boolean skipStdError;

    /**
     * If 'true' then error ouput will be redirected to standard output
     */
    private boolean combineOutput;

    /**
     * Timeout for command. If execution time of command exceeds this timeout the command will be forcibly terminated.
     * Value 0 means no timeout at all.
     */
    @Min(0)
    private int timeoutMs;

    /**
     * Expected 'success' exit code
     */
    private int expectedExitCode = DEFAULT_EXPECTED_EXIT_CODE;

    /**
     * Charset of standard and error output. Value 'null' means that both outputs are binary.
     */
    private String charset;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public boolean isEnvironmentInherit() {
        return environmentInherit;
    }

    public void setEnvironmentInherit(boolean environmentInherit) {
        this.environmentInherit = environmentInherit;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean isSkipStdError() {
        return skipStdError;
    }

    public void setSkipStdError(boolean skipStdError) {
        this.skipStdError = skipStdError;
    }

    public boolean isSkipStdOutput() {
        return skipStdOutput;
    }

    public void setSkipStdOutput(boolean skipStdOutput) {
        this.skipStdOutput = skipStdOutput;
    }

    public boolean isCombineOutput() {
        return combineOutput;
    }

    public void setCombineOutput(boolean combineOutput) {
        this.combineOutput = combineOutput;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getExpectedExitCode() {
        return expectedExitCode;
    }

    public void setExpectedExitCode(int expectedExitCode) {
        this.expectedExitCode = expectedExitCode;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.LOCAL_COMMAND;
    }

}
