package org.qzerver.model.agent.action.providers.executor.datagram;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class DatagramActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatagramActionExecutor.class);

    private Validator beanValidator;

    @Override
    public ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress) {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        DatagramActionDefinition definition = (DatagramActionDefinition) actionDefinition;

        LOGGER.debug("Socket action will be executed on node node [{}]", nodeAddress);

        try {
            return processAction(definition, nodeAddress);
        } catch (Exception e) {
            LOGGER.debug("Fail to execute datagram action", e);
            return produceExceptionalResult(e);
        }
    }

    private DatagramActionResult produceExceptionalResult(Exception e) {
        DatagramActionResult result = new DatagramActionResult();

        result.setStatus(DatagramActionResultStatus.EXCEPTION);
        result.setSucceed(false);
        result.setResponse(null);
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());

        return result;
    }

    private DatagramActionResult processAction(DatagramActionDefinition definition, String nodeAddress)
        throws Exception
    {
        InetSocketAddress socketAddress = new InetSocketAddress(nodeAddress, definition.getPort());

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(definition.getReadTimeoutMs());
        socket.setSendBufferSize(definition.getMessage().length);
        socket.setReceiveBufferSize(definition.getExpectedResponse().length);

        byte[] sendData = definition.getMessage();
        DatagramPacket sendPacket = new DatagramPacket(sendData, 0, sendData.length, socketAddress);

        try {
            socket.send(sendPacket);
            return processDatagramAnswer(definition, socket);
        } finally {
            socket.close();
        }
    }

    private DatagramActionResult processDatagramAnswer(DatagramActionDefinition definition, DatagramSocket socket)
        throws Exception
    {
        DatagramActionResult result = new DatagramActionResult();
        result.setStatus(DatagramActionResultStatus.CAPTURED);

        int readSize = definition.getExpectedResponse().length;
        byte[] readBuffer = new byte[readSize];

        DatagramPacket receivePacket = new DatagramPacket(readBuffer, 0, readSize);

        try {
            socket.receive(receivePacket);
        } catch (InterruptedIOException e) {
            result.setStatus(DatagramActionResultStatus.TIMEOUT);
        }

        if ((result.getStatus() == DatagramActionResultStatus.CAPTURED) && (receivePacket.getLength() > 0)) {
            byte[] resultData = Arrays.copyOfRange(receivePacket.getData(),
                receivePacket.getOffset(), receivePacket.getLength());
            result.setResponse(resultData);
        }

        boolean succeed = (result.getStatus() == DatagramActionResultStatus.CAPTURED) &&
            Arrays.equals(result.getResponse(), definition.getExpectedResponse());
        result.setSucceed(succeed);

        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
