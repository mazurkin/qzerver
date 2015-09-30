package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.util.TestUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class LocalCommandActionExecutorTest extends AbstractModelTest {

    @Resource
    private ActionExecutor localCommandActionExecutor;

    @Value("${app.action.capture.size.max}")
    private long maxCaptureSize;

    private String javaCommand;

    @Before
    public void setUp() throws Exception {
        javaCommand = TestUtils.findJavaCommand();
    }

    @Test
    public void testNormal() throws Exception {
        List<String> commandArguments = ImmutableList.<String>builder()
            .add("-cp").add(System.getProperty("java.class.path"))
            .add("org.qzerver.util.programs.SampleProgram")
            .add("arg1")
            .add("arg2")
            .add("${node}")
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        String workDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand(javaCommand);
        definition.setParameters(commandArguments);
        definition.setDirectory(workDirectory);
        definition.setCharset(Charset.defaultCharset().name());
        definition.setCombineOutput(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setTimeoutMs(0);
        definition.setEnvironmentInherit(true);
        definition.setEnvironmentVariables(commandEnvironments);

        LocalCommandActionResult result = (LocalCommandActionResult) localCommandActionExecutor.execute(
            definition, 123L, "127.1.2.3"
        );

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(LocalCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        LocalCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(LocalCommandActionOutputStatus.CAPTURED, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#127.1.2.3"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY1=VALUE1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY2=VALUE2"));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));

        Assert.assertNotNull(TestUtils.findLineEqualCaseless(standardOutputLines, "WORKFOLDER#" + workDirectory));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ERROR#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "SLEEPING#"));

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.CAPTURED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());
    }

    @Test
    public void testSkip() throws Exception {
        List<String> commandArguments = ImmutableList.<String>builder()
            .add("-cp").add(System.getProperty("java.class.path"))
            .add("org.qzerver.util.programs.SampleProgram")
            .add("arg1")
            .add("arg2")
            .add("${node}")
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        String workDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand(javaCommand);
        definition.setParameters(commandArguments);
        definition.setDirectory(workDirectory);
        definition.setCharset(Charset.defaultCharset().name());
        definition.setCombineOutput(false);
        definition.setSkipStdOutput(true);
        definition.setSkipStdError(true);
        definition.setExpectedExitCode(0);
        definition.setTimeoutMs(0);
        definition.setEnvironmentInherit(true);
        definition.setEnvironmentVariables(commandEnvironments);

        LocalCommandActionResult result = (LocalCommandActionResult) localCommandActionExecutor.execute(
            definition, 123L, "127.1.2.3"
        );

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(LocalCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        LocalCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(LocalCommandActionOutputStatus.SKIPPED, standardOutput.getStatus());
        Assert.assertNull(standardOutput.getData());
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.SKIPPED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());
    }

    @Test
    public void testTimeout() throws Exception {
        List<String> commandArguments = ImmutableList.<String>builder()
            .add("-cp").add(System.getProperty("java.class.path"))
            .add("org.qzerver.util.programs.SampleProgram")
            .add("arg1")
            .add("arg2")
            .add("${node}")
            .add("-s").add("600")
            .add("-e").add("some_error_text")
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        String workDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand(javaCommand);
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
        Assert.assertEquals(LocalCommandActionOutputStatus.TIMEOUT, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#127.1.2.3"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY1=VALUE1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY2=VALUE2"));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));

        Assert.assertNotNull(TestUtils.findLineEqualCaseless(standardOutputLines, "WORKFOLDER#" + workDirectory));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ERROR#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "SLEEPING#"));

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.TIMEOUT, standardError.getStatus());
        Assert.assertNotNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());

        String standardErrorText = new String(standardError.getData());
        Assert.assertNotNull(standardErrorText);
        Assert.assertEquals("some_error_text\n", standardErrorText);
    }

    @Test
    public void testOverflow() throws Exception {
        List<String> commandArguments = ImmutableList.<String>builder()
            .add("-cp").add(System.getProperty("java.class.path"))
            .add("org.qzerver.util.programs.SampleProgram")
            .add("arg1")
            .add("arg2")
            .add("${node}")
            .add("-l").add(Long.toString(maxCaptureSize / 50 * 2))
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .put("KEY1", "VALUE1")
            .put("KEY2", "VALUE2")
            .build();

        String workDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand(javaCommand);
        definition.setParameters(commandArguments);
        definition.setDirectory(workDirectory);
        definition.setCharset(Charset.defaultCharset().name());
        definition.setCombineOutput(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setTimeoutMs(0);
        definition.setEnvironmentInherit(true);
        definition.setEnvironmentVariables(commandEnvironments);

        LocalCommandActionResult result = (LocalCommandActionResult) localCommandActionExecutor.execute(
            definition, 123L, "127.1.2.3"
        );

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(LocalCommandActionResultStatus.EXECUTED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        // Check standard output
        LocalCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(LocalCommandActionOutputStatus.OVERFLOWED, standardOutput.getStatus());
        Assert.assertNotNull(standardOutput.getData());
        Assert.assertEquals(maxCaptureSize, standardOutput.getData().length);
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        String standardOutputText = new String(standardOutput.getData());
        Assert.assertNotNull(standardOutputText);

        String[] standardOutputLines = StringUtils.split(standardOutputText, "\r\n");
        Assert.assertTrue(standardOutputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_1#arg1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_2#arg2"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ARGUMENT_3#127.1.2.3"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY1=VALUE1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "ENVIRONMENT#KEY2=VALUE2"));
        Assert.assertNotNull(TestUtils.findLineByPrefix(standardOutputLines, "ENVIRONMENT#PATH="));

        Assert.assertNotNull(TestUtils.findLineEqualCaseless(standardOutputLines, "WORKFOLDER#" + workDirectory));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(standardOutputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(standardOutputLines, "DUMPING#"));

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.CAPTURED, standardError.getStatus());
        Assert.assertNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());
    }

    @Test
    public void testWrongCommand() throws Exception {
        String command = "some-wrong-command-7326732";

        List<String> commandArguments = ImmutableList.<String>builder()
            .build();

        Map<String, String> commandEnvironments = ImmutableMap.<String, String>builder()
            .build();

        LocalCommandActionDefinition definition = new LocalCommandActionDefinition();
        definition.setCommand(command);
        definition.setParameters(commandArguments);
        definition.setDirectory(SystemUtils.getJavaIoTmpDir().getAbsolutePath());
        definition.setCharset(Charset.defaultCharset().name());
        definition.setCombineOutput(false);
        definition.setSkipStdOutput(false);
        definition.setSkipStdError(false);
        definition.setExpectedExitCode(0);
        definition.setTimeoutMs(0);
        definition.setEnvironmentInherit(true);
        definition.setEnvironmentVariables(commandEnvironments);

        LocalCommandActionResult result = (LocalCommandActionResult) localCommandActionExecutor.execute(
            definition, 123L, "127.1.2.3"
        );

        // Check result
        Assert.assertNotNull(result);
        Assert.assertEquals(-1, result.getExitCode());
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(LocalCommandActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertEquals("java.io.IOException", result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());

        // Check standard output
        LocalCommandActionOutput standardOutput = result.getStdout();
        Assert.assertNotNull(standardOutput);
        Assert.assertEquals(LocalCommandActionOutputStatus.IDLE, standardOutput.getStatus());
        Assert.assertNull(standardOutput.getData());
        Assert.assertNull(standardOutput.getExceptionClass());
        Assert.assertNull(standardOutput.getExceptionMessage());

        // Check standard error
        LocalCommandActionOutput standardError = result.getStderr();
        Assert.assertNotNull(standardError);
        Assert.assertEquals(LocalCommandActionOutputStatus.IDLE, standardError.getStatus());
        Assert.assertNull(standardError.getData());
        Assert.assertNull(standardError.getExceptionClass());
        Assert.assertNull(standardError.getExceptionMessage());
    }

}
