package org.qzerver.model.agent.action.providers.executor.socket;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class SocketActionExecutorTest extends AbstractModelTest {

    private static final int SOCKET_SERVER_PORT = 3002;

    @Resource
    private ActionExecutor socketActionExecutor;

    private SocketServer socketServer;

    @Before
    public void setUp() throws Exception {
        socketServer = new SocketServer();
        socketServer.start();
    }

    @After
    public void tearDown() throws Exception {
        socketServer.shutdown();
    }

    @Test
    public void testNormal() throws Exception {
        SocketActionDefinition definition = new SocketActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setConnectionTimeoutMs(0);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");

        SocketActionResult result = (SocketActionResult) socketActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(SocketActionResultStatus.CAPTURED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertTrue(Arrays.equals("response".getBytes(), result.getResponse()));
    }

    @Test
    public void testWrong() throws Exception {
        SocketActionDefinition definition = new SocketActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setConnectionTimeoutMs(0);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("blah-blah-blah");

        SocketActionResult result = (SocketActionResult) socketActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(SocketActionResultStatus.CAPTURED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertTrue(Arrays.equals("blah-bla".getBytes(), result.getResponse()));
    }

    @Test
    public void testException() throws Exception {
        SocketActionDefinition definition = new SocketActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setConnectionTimeoutMs(0);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");

        SocketActionResult result = (SocketActionResult) socketActionExecutor.execute(definition, 123L, "unknown-host-6353");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(SocketActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertEquals("java.net.UnknownHostException", result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());
        Assert.assertNull(result.getResponse());
    }

    @Test
    public void testTimeout() throws Exception {
        SocketActionDefinition definition = new SocketActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setConnectionTimeoutMs(0);
        definition.setReadTimeoutMs(3000);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");
        socketServer.setSleep(true);

        SocketActionResult result = (SocketActionResult) socketActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(SocketActionResultStatus.TIMEOUT, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNull(result.getResponse());
    }

    private static class SocketServer extends Thread {

        private ServerSocket serverSocket;

        private volatile String response = "0123456789";

        private volatile boolean sleep = false;

        @Override
        public void run() {
            try {
                processConnections();
            } catch (Exception e) {
                // do nothing
            }
        }

        public void shutdown() throws Exception {
            this.serverSocket.close();
            this.interrupt();
            this.join();
        }

        private void processConnections() throws Exception {
            serverSocket = new ServerSocket(SOCKET_SERVER_PORT);

            Socket clientSocket = serverSocket.accept();

            InputStream inputStream = clientSocket.getInputStream();
            try {
                OutputStream outputStream = clientSocket.getOutputStream();
                try {
                    processStreams(inputStream, outputStream);
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        }

        private void processStreams(InputStream inputStream, OutputStream outputStream) throws Exception {
            // Sleep
            if (sleep) {
                Thread.sleep(600 * 1000);
            }

            // first print the output
            byte[] writeData = response.getBytes();
            outputStream.write(writeData, 0, writeData.length);

            // consume the input
            byte[] readData = new byte[1024];
            while (inputStream.read(readData, 0, readData.length) != -1) {
                // do nothing
            }
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public void setSleep(boolean sleep) {
            this.sleep = sleep;
        }

    }
}
