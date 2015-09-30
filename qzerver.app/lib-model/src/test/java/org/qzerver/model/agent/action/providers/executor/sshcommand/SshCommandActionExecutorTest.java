package org.qzerver.model.agent.action.providers.executor.sshcommand;

import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.util.ChronometerUpdateThread;
import org.qzerver.util.TestUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import java.util.Map;

/**
 * To pass the test the system must follow the conditions:
 * <ul>
 *     <li>OpenSSH service must be installed and operating on the current system</li>
 *     <li>Current user must have valid private key in ${user.home}/.ssh/id_rsa</li>
 *     <li>Current user must be able to login on the localhost with key authentication (use ssh-copy-id tool)</li>
 * </ul>
 */
public class SshCommandActionExecutorTest extends AbstractModelTest {

    @Resource
    private ActionExecutor sshCommandActionExecutor;

    @Resource
    private StubChronometer chronometer;

    @Value("${app.action.capture.size.max}")
    private long maxCaptureSize;

    private String javaCommand;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);

        javaCommand = TestUtils.findJavaCommand();
    }

    @Test
    public void testNormal() throws Exception {
        String command = String.format("%s -cp %s %s %s %s ${node}",
            javaCommand,
            System.getProperty("java.class.path"),
            "org.qzerver.util.programs.SampleProgram",
            "arg1",
            "arg2"
        );

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        Map<String, String> sshOptions = ImmutableMap.<String, String>builder()
            .put("StrictHostKeyChecking", "no")
            .build();

        String username = System.getProperty("user.name");

        String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";

        SshCommandActionDefinition definition = new SshCommandActionDefinition();
        definition.setCommand(command);
        definition.setEnvironmentVariables(commandEnvironments);
        definition.setAgentForwarding(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setConnectionTimeoutMs(0);
        definition.setTimeoutMs(0);
        definition.setPrivateKeyPath(privateKeyPath);
        definition.setPrivateKeyPassphrase(null);
        definition.setSshProperties(sshOptions);
        definition.setPassword(null);
        definition.setPort(22);
        definition.setKnownHostPath(null);
        definition.setUsername(username);

        SshCommandActionResult result = (SshCommandActionResult) sshCommandActionExecutor.execute(definition,
            123L, "localhost");

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(SshCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        SshCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(SshCommandActionOutputStatus.CAPTURED, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#localhost"));

        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#SSH_CLIENT="));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ERROR#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "SLEEPING#"));

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.CAPTURED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
    }

    @Test
    public void testSkip() throws Exception {
        String command = String.format("%s -cp %s %s %s %s ${node}",
            javaCommand,
            System.getProperty("java.class.path"),
            "org.qzerver.util.programs.SampleProgram",
            "arg1",
            "arg2"
        );

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        Map<String, String> sshOptions = ImmutableMap.<String, String>builder()
            .put("StrictHostKeyChecking", "no")
            .build();

        String username = System.getProperty("user.name");

        String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";

        SshCommandActionDefinition definition = new SshCommandActionDefinition();
        definition.setCommand(command);
        definition.setEnvironmentVariables(commandEnvironments);
        definition.setAgentForwarding(false);
        definition.setSkipStdOutput(true);
        definition.setSkipStdError(true);
        definition.setExpectedExitCode(0);
        definition.setConnectionTimeoutMs(0);
        definition.setTimeoutMs(0);
        definition.setPrivateKeyPath(privateKeyPath);
        definition.setPrivateKeyPassphrase(null);
        definition.setSshProperties(sshOptions);
        definition.setPassword(null);
        definition.setPort(22);
        definition.setKnownHostPath(null);
        definition.setUsername(username);

        SshCommandActionResult result = (SshCommandActionResult) sshCommandActionExecutor.execute(definition,
            123L, "localhost");

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(SshCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        SshCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(SshCommandActionOutputStatus.SKIPPED, standardOutput.getStatus());
        Assert.assertNull(standardOutput.getData());

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.SKIPPED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
    }

    @Test
    public void testTimeout() throws Exception {
        String command = String.format("%s -cp %s %s %s %s ${node} -s 600 -e some_error_text",
            javaCommand,
            System.getProperty("java.class.path"),
            "org.qzerver.util.programs.SampleProgram",
            "arg1",
            "arg2"
        );

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        Map<String, String> sshOptions = ImmutableMap.<String, String>builder()
            .put("StrictHostKeyChecking", "no")
            .build();

        String username = System.getProperty("user.name");

        String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";

        SshCommandActionDefinition definition = new SshCommandActionDefinition();
        definition.setCommand(command);
        definition.setEnvironmentVariables(commandEnvironments);
        definition.setAgentForwarding(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setConnectionTimeoutMs(0);
        definition.setTimeoutMs(3000);
        definition.setPrivateKeyPath(privateKeyPath);
        definition.setPrivateKeyPassphrase(null);
        definition.setSshProperties(sshOptions);
        definition.setPassword(null);
        definition.setPort(22);
        definition.setKnownHostPath(null);
        definition.setUsername(username);

        ChronometerUpdateThread.start(chronometer, 5000, 3100);

        SshCommandActionResult result = (SshCommandActionResult) sshCommandActionExecutor.execute(definition,
            123L, "localhost");

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(SshCommandActionResultStatus.TIMEOUT, result.getStatus());
        Assert.assertEquals(-1, result.getExitCode());
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        SshCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(SshCommandActionOutputStatus.TIMEOUT, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#localhost"));

        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#SSH_CLIENT="));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ERROR#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "SLEEPING#"));

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.TIMEOUT, standardError.getStatus());
        Assert.assertNotNull(standardError.getData());

        String standardErrorText = new String(standardError.getData());
        Assert.assertNotNull(standardErrorText);
        Assert.assertEquals("some_error_text\n", standardErrorText);
    }

    @Test
    public void testOverflow() throws Exception {
        String command = String.format("%s -cp %s %s %s %s ${node} -l %d",
            javaCommand,
            System.getProperty("java.class.path"),
            "org.qzerver.util.programs.SampleProgram",
            "arg1",
            "arg2",
            maxCaptureSize / 50 * 2
        );

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        Map<String, String> sshOptions = ImmutableMap.<String, String>builder()
            .put("StrictHostKeyChecking", "no")
            .build();

        String username = System.getProperty("user.name");

        String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";

        SshCommandActionDefinition definition = new SshCommandActionDefinition();
        definition.setCommand(command);
        definition.setEnvironmentVariables(commandEnvironments);
        definition.setAgentForwarding(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setConnectionTimeoutMs(0);
        definition.setTimeoutMs(0);
        definition.setPrivateKeyPath(privateKeyPath);
        definition.setPrivateKeyPassphrase(null);
        definition.setSshProperties(sshOptions);
        definition.setPassword(null);
        definition.setPort(22);
        definition.setKnownHostPath(null);
        definition.setUsername(username);

        SshCommandActionResult result = (SshCommandActionResult) sshCommandActionExecutor.execute(definition,
            123L, "localhost");

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(SshCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        SshCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(SshCommandActionOutputStatus.OVERFLOWED, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());
        Assert.assertEquals(maxCaptureSize, standardOutput.getData().length);

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#localhost"));

        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#SSH_CLIENT="));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ERROR#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "SLEEPING#"));

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.CAPTURED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
    }

    @Test
    public void testWrongCommand() throws Exception {
        String command = "env";

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        Map<String, String> sshOptions = ImmutableMap.<String, String>builder()
            .put("StrictHostKeyChecking", "no")
            .build();

        String username = System.getProperty("user.name");

        String privateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";

        SshCommandActionDefinition definition = new SshCommandActionDefinition();
        definition.setCommand(command);
        definition.setEnvironmentVariables(commandEnvironments);
        definition.setAgentForwarding(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setConnectionTimeoutMs(0);
        definition.setTimeoutMs(0);
        definition.setPrivateKeyPath(privateKeyPath);
        definition.setPrivateKeyPassphrase(null);
        definition.setSshProperties(sshOptions);
        definition.setPassword(null);
        definition.setPort(22);
        definition.setKnownHostPath(null);
        definition.setUsername(username);

        SshCommandActionResult result = (SshCommandActionResult) sshCommandActionExecutor.execute(definition,
            123L, "some-unknown-host-8267283");

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(-1, result.getExitCode());
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertNotNull(result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());

        // Check standard output
        SshCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(SshCommandActionOutputStatus.IDLE, standardOutput.getStatus());
        Assert.assertNull(standardOutput.getData());

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.IDLE, standardError.getStatus());
        Assert.assertNull(standardError.getData());
    }

}
