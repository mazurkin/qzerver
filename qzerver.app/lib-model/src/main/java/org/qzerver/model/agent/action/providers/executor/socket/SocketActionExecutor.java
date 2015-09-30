package org.qzerver.model.agent.action.providers.executor.socket;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class SocketActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketActionExecutor.class);

    private Validator beanValidator;

    @Override
    public SocketActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        SocketActionDefinition definition = (SocketActionDefinition) actionDefinition;

        LOGGER.debug("Socket action will be executed on node node [{}]", nodeAddress);

        try {
            return processAction(definition, nodeAddress);
        } catch (Exception e) {
            LOGGER.debug("Fail to execute socket action", e);
            return produceExceptionalResult(e);
        }
    }

    private SocketActionResult produceExceptionalResult(Exception e) {
        SocketActionResult result = new SocketActionResult();

        result.setStatus(SocketActionResultStatus.EXCEPTION);
        result.setSucceed(false);
        result.setResponse(null);
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());

        return result;
    }

    private SocketActionResult processAction(SocketActionDefinition definition, String nodeAddress) throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(nodeAddress, definition.getPort());

        Socket socket = new Socket();
        socket.setSoTimeout(definition.getReadTimeoutMs());
        socket.setSendBufferSize(definition.getMessage().length);
        socket.setReceiveBufferSize(definition.getExpectedResponse().length);

        socket.connect(socketAddress, definition.getConnectionTimeoutMs());
        try {
            return processSocket(definition, socket);
        } finally {
            socket.close();
        }
    }

    private SocketActionResult processSocket(SocketActionDefinition definition, Socket socket) throws Exception {
        OutputStream outputStream = socket.getOutputStream();
        try {
            outputStream.write(definition.getMessage());
            return processSocketAnswer(definition, socket);
        } finally {
            outputStream.close();
        }
    }

    private SocketActionResult processSocketAnswer(SocketActionDefinition definition, Socket socket) throws Exception {
        SocketActionResult result = new SocketActionResult();
        result.setStatus(SocketActionResultStatus.CAPTURED);

        int readSize = definition.getExpectedResponse().length;
        int readOnce;
        byte[] readBuffer = new byte[readSize];

        InputStream inputStream = socket.getInputStream();
        try {
            readOnce = inputStream.read(readBuffer, 0, readSize);
        } catch (InterruptedIOException e) {
            result.setStatus(SocketActionResultStatus.TIMEOUT);
            readOnce = e.bytesTransferred;
        } finally {
            inputStream.close();
        }

        if (readOnce > 0) {
            byte[] resultData = Arrays.copyOfRange(readBuffer, 0, readOnce);
            result.setResponse(resultData);
        }

        boolean succeed = (result.getStatus() == SocketActionResultStatus.CAPTURED) &&
            Arrays.equals(result.getResponse(), definition.getExpectedResponse());
        result.setSucceed(succeed);

        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

}
