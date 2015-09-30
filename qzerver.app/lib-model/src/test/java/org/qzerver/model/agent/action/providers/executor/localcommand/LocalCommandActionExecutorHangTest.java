package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * For some unknown reason I found out that undex Linux capturing thread hangs and nothing can terminate the IO block
 * on InputStream.read() in case when "sleep" command is used
 */
public class LocalCommandActionExecutorHangTest extends AbstractModelTest {

    @Resource
    private ActionExecutor localCommandActionExecutor;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
    }

    @Test
    public void testHanging() throws Exception {
        List<String> commandArguments = ImmutableList.<String>builder()
            .add("-c")
            .add("echo aaa; sleep 600; echo bbb")
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        String workDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand("/bin/sh");
        definition.setParameters(commandArguments);
        definition.setDirectory(workDirectory);
        definition.setCharset(Charset.defaultCharset().name());
        definition.setCombineOutput(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setTimeoutMs(3000);
        definition.setEnvironmentInherit(true);
        definition.setEnvironmentVariables(commandEnvironments);

        LocalCommandActionResult result = (LocalCommandActionResult) localCommandActionExecutor.execute(
            definition, 123L, "127.1.2.3"
        );

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(-1, result.getExitCode());
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(LocalCommandActionResultStatus.TIMEOUT, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        LocalCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(LocalCommandActionOutputStatus.HUNG, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertEquals("aaa\n", standardOutputText);

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.HUNG, standardError.getStatus());
        Assert.assertNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());
    }

}
