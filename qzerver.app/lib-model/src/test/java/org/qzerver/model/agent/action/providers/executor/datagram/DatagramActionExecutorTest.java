package org.qzerver.model.agent.action.providers.executor.datagram;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class DatagramActionExecutorTest extends AbstractModelTest {

    private static final int SOCKET_SERVER_PORT = 3003;

    @Resource
    private ActionExecutor datagramActionExecutor;

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
        DatagramActionDefinition definition = new DatagramActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");

        DatagramActionResult result = (DatagramActionResult) datagramActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(DatagramActionResultStatus.CAPTURED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertTrue(Arrays.equals("response".getBytes(), result.getResponse()));
    }

    @Test
    public void testWrongMore() throws Exception {
        DatagramActionDefinition definition = new DatagramActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("blah-blah-blah"); // more than expected

        DatagramActionResult result = (DatagramActionResult) datagramActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(DatagramActionResultStatus.CAPTURED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertTrue(Arrays.equals("blah-bla".getBytes(), result.getResponse()));
    }

    @Test
    public void testWrongLess() throws Exception {
        DatagramActionDefinition definition = new DatagramActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("blah"); // less than expected

        DatagramActionResult result = (DatagramActionResult) datagramActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(DatagramActionResultStatus.CAPTURED, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertTrue(Arrays.equals("blah".getBytes(), result.getResponse()));
    }

    @Test
    public void testException() throws Exception {
        DatagramActionDefinition definition = new DatagramActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setReadTimeoutMs(0);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");

        DatagramActionResult result = (DatagramActionResult) datagramActionExecutor.execute(definition, 123L, "unknown-host-6353");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(DatagramActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertEquals("java.lang.IllegalArgumentException", result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());
        Assert.assertNull(result.getResponse());
    }

    @Test
    public void testTimeout() throws Exception {
        DatagramActionDefinition definition = new DatagramActionDefinition();
        definition.setPort(SOCKET_SERVER_PORT);
        definition.setReadTimeoutMs(3000);
        definition.setMessage("answer".getBytes());
        definition.setExpectedResponse("response".getBytes());

        socketServer.setResponse("response");
        socketServer.setSleep(true);

        DatagramActionResult result = (DatagramActionResult) datagramActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(DatagramActionResultStatus.TIMEOUT, result.getStatus());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
        Assert.assertNull(result.getResponse());
    }

    private static class SocketServer extends Thread {

        private DatagramSocket serverSocket;

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
            serverSocket = new DatagramSocket(SOCKET_SERVER_PORT);

            // Read packet
            byte[] readBuffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(readBuffer, readBuffer.length);
            serverSocket.receive(packet);

            // Sleep
            if (sleep) {
                Thread.sleep(600 * 1000);
            }

            // Send packet
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            byte[] writeBuffer = response.getBytes();
            packet = new DatagramPacket(writeBuffer, writeBuffer.length, address, port);
            serverSocket.send(packet);
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public void setSleep(boolean sleep) {
            this.sleep = sleep;
        }

    }


}
