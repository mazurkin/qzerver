package org.qzerver.model.agent.action.providers.executor.http;

import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.util.TestUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class HttpActionExecutorTest extends AbstractModelTest {

    private static final int HTTP_SERVER_PORT = 3001;

    @Resource
    private ActionExecutor httpActionExecutor;

    @Value("${app.action.capture.size.max}")
    private long maxCaptureSize;

    private Server server;

    @Before
    public void setUp() throws Exception {
        RequestHandler requestHandler = new RequestHandler();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setHost("127.0.0.1");
        connector.setPort(HTTP_SERVER_PORT);
        connector.setName("admin");

        server = new Server(HTTP_SERVER_PORT);
        server.setHandler(requestHandler);
        server.setConnectors(new Connector[] { connector });
        server.setStopAtShutdown(true);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testNormalGet() throws Exception {
        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test?key1=value1&key2=value2");
        definition.setMethod(HttpActionMethod.GET);
        definition.setPostParams(null);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(0);
        definition.setConnectionTimeoutMs(0);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(false);
        definition.setHeaders(null);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.QUERIED, result.getStatus());
        Assert.assertEquals(200, result.getStatusCode());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.CAPTURED, output.getStatus());
        Assert.assertNotNull(output.getData());
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());

        String outputText = new String(output.getData());
        Assert.assertNotNull(outputText);

        String[] outputLines = StringUtils.split(outputText, "\r\n");
        Assert.assertTrue(outputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "START#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "ARGUMENT#key1=[value1]"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "ARGUMENT#key2=[value2]"));
    }

    @Test
    public void testNormalPost() throws Exception {
        Map<String, String> postParams = ImmutableMap.<String, String>builder()
            .put("key1", "value1")
            .put("key2", "value2")
            .build();

        Map<String, String> headers = ImmutableMap.<String, String>builder()
            .put("X-HEADER1", "value1")
            .put("X-HEADER2", "value2")
            .build();

        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test");
        definition.setMethod(HttpActionMethod.POST);
        definition.setPostParams(postParams);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(0);
        definition.setConnectionTimeoutMs(0);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(false);
        definition.setHeaders(headers);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.QUERIED, result.getStatus());
        Assert.assertEquals(200, result.getStatusCode());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.CAPTURED, output.getStatus());
        Assert.assertNotNull(output.getData());
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());

        String outputText = new String(output.getData());
        Assert.assertNotNull(outputText);

        String[] outputLines = StringUtils.split(outputText, "\r\n");
        Assert.assertTrue(outputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "START#"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "ARGUMENT#key1=[value1]"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "ARGUMENT#key2=[value2]"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "HEADER#X-HEADER1=value1"));
        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "HEADER#X-HEADER2=value2"));
    }

    @Test
    public void testSkip() throws Exception {
        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test");
        definition.setMethod(HttpActionMethod.GET);
        definition.setPostParams(null);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(0);
        definition.setConnectionTimeoutMs(0);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(true);
        definition.setHeaders(null);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.QUERIED, result.getStatus());
        Assert.assertEquals(200, result.getStatusCode());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.SKIPPED, output.getStatus());
        Assert.assertNull(output.getData());
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());
    }

    @Test
    public void testTimeout() throws Exception {
        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test?sleep=600");
        definition.setMethod(HttpActionMethod.GET);
        definition.setPostParams(null);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(3000);
        definition.setConnectionTimeoutMs(0);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(false);
        definition.setHeaders(null);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.QUERIED, result.getStatus());
        Assert.assertEquals(200, result.getStatusCode());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.TIMEOUT, output.getStatus());
        Assert.assertNotNull(output.getData());
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());

        String outputText = new String(output.getData());
        Assert.assertNotNull(outputText);

        String[] outputLines = StringUtils.split(outputText, "\r\n");
        Assert.assertTrue(outputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(outputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "SLEEPING#"));
    }

    @Test
    public void testOverflow() throws Exception {
        long lines = maxCaptureSize / 50 * 2;

        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test?lines=" + lines);
        definition.setMethod(HttpActionMethod.GET);
        definition.setPostParams(null);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(0);
        definition.setConnectionTimeoutMs(0);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(false);
        definition.setHeaders(null);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.QUERIED, result.getStatus());
        Assert.assertEquals(200, result.getStatusCode());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.OVERFLOWED, output.getStatus());
        Assert.assertNotNull(output.getData());
        Assert.assertEquals(maxCaptureSize, output.getData().length);
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());

        String outputText = new String(output.getData());
        Assert.assertNotNull(outputText);

        String[] outputLines = StringUtils.split(outputText, "\r\n");
        Assert.assertTrue(outputLines.length > 0);

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "START#"));
        Assert.assertNull(TestUtils.findLineEqualWithCase(outputLines, "FINISH#"));

        Assert.assertNotNull(TestUtils.findLineEqualWithCase(outputLines, "DUMPING#"));
    }

    @Test
    public void testException() throws Exception {
        HttpActionDefinition definition = new HttpActionDefinition();
        definition.setUrl("http://${node}:" + HTTP_SERVER_PORT + "/test");
        definition.setMethod(HttpActionMethod.GET);
        definition.setPostParams(null);
        definition.setPlainAuthUsername(null);
        definition.setPlainAuthPassword(null);
        definition.setTimeoutMs(0);
        definition.setConnectionTimeoutMs(1000);
        definition.setExpectedStatusCode(200);
        definition.setSkipOutput(false);
        definition.setHeaders(null);

        HttpActionResult result = (HttpActionResult) httpActionExecutor.execute(definition, 123L, "non-existing-host");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(HttpActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertEquals(0, result.getStatusCode());
        Assert.assertEquals("java.net.UnknownHostException", result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());

        HttpActionOutput output = result.getOutput();
        Assert.assertNotNull(output);
        Assert.assertEquals(HttpActionOutputStatus.IDLE, output.getStatus());
        Assert.assertNull(output.getData());
        Assert.assertNull(output.getExcepionClass());
        Assert.assertNull(output.getExceptionMessage());
    }

    private static class RequestHandler extends AbstractHandler {

        public static final String OPTION_SLEEP = "sleep";

        public static final String OPTION_LINES = "lines";

        @Override
        public void handle(String s, Request request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException, ServletException
        {
            httpServletResponse.setContentType("text/plain;charset=utf-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            PrintWriter printWriter = httpServletResponse.getWriter();

            try {
                handleRequest(httpServletRequest, printWriter);
            } catch (Exception e) {
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            request.setHandled(true);
        }

        private void handleRequest(HttpServletRequest httpServletRequest, PrintWriter writer)
            throws Exception
        {
            writer.printf("START#\n");

            for (Map.Entry<String, String[]> entry : httpServletRequest.getParameterMap().entrySet()) {
                writer.printf("ARGUMENT#%s=%s\n", entry.getKey(), Arrays.toString(entry.getValue()));
            }

            for (String headerName : Collections.list(httpServletRequest.getHeaderNames())) {
                writer.printf("HEADER#%s=%s\n", headerName, httpServletRequest.getHeader(headerName));
            }

            String sleep = httpServletRequest.getParameter(OPTION_SLEEP);
            if (sleep != null) {
                writer.printf("SLEEPING#\n");
                // Have to flush before sleeping because all content could be in output cache of the web container
                writer.flush();
                long sleepValueSec = Long.parseLong(sleep);
                Thread.sleep(sleepValueSec * 1000);
            }

            String lines = httpServletRequest.getParameter(OPTION_LINES);
            if (lines != null) {
                writer.printf("DUMPING#\n");
                int linesValue = Integer.parseInt(lines);
                for (int i=0; i < linesValue; i++) {
                    writer.printf("01234567890123456789012345678901234567890123456789");
                }
            }

            writer.printf("FINISH#\n");
        }
    }
}
