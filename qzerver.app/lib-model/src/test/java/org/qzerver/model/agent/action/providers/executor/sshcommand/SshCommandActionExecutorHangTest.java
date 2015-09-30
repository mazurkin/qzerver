package org.qzerver.model.agent.action.providers.executor.sshcommand;

import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.util.ChronometerUpdateThread;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import java.util.Map;

public class SshCommandActionExecutorHangTest extends AbstractModelTest {

    @Resource
    private ActionExecutor sshCommandActionExecutor;

    @Resource
    private StubChronometer chronometer;

    @Value("${app.action.capture.size.max}")
    private long maxCaptureSize;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
    }

    @Test
    public void testTimeout() throws Exception {
        String command = "echo aaa; sleep 600; echo bbb";

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
        Assert.assertEquals("aaa\n", standardOutputText);

        // Check standard error
        SshCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(SshCommandActionOutputStatus.TIMEOUT, standardError.getStatus());
        Assert.assertNull(standardError.getData());
    }


}
