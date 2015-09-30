package org.qzerver.model.agent.action.providers.executor.http.threads;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionOutput;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionOutputStatus;
import org.qzerver.model.agent.action.providers.executor.localcommand.threads.ProcessOutputThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class HttpOutputThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessOutputThread.class);

    private static final String THREAD_NAME = "Http output capturer";

    private static final int BUFFER_SIZE = 16 * 1024;

    private final HttpUriRequest request;

    private final HttpEntity entity;

    private final ByteArrayOutputStream outputStream;

    private final boolean skip;

    private final long maxCaptureSize;

    private HttpActionOutputStatus status;

    private String excepionClass;

    private String exceptionMessage;

    private volatile boolean exiting;

    public HttpOutputThread(HttpUriRequest request, HttpEntity entity, long maxCaptureSize, boolean skip) {
        this.request = request;
        this.entity = entity;
        this.maxCaptureSize = maxCaptureSize;
        this.skip = skip;
        this.status = HttpActionOutputStatus.IDLE;
        this.outputStream = new ByteArrayOutputStream();
        this.exiting = false;

        setName(THREAD_NAME);
        setDaemon(false);
    }

    @Override
    public void run() {
        LOGGER.debug("Http output copier is started");

        if (skip) {
            skipQueryOutput();
        } else {
            captureQueryOutput();
        }

        LOGGER.debug("Http output copier is finishing");
    }

    private void captureQueryOutput() {
        status = HttpActionOutputStatus.CAPTURED;

        try {
            captureQueryEntity();
        } catch (Exception e) {
            LOGGER.warn("Fail to capture query data", e);
            status = HttpActionOutputStatus.EXCEPTION;
            excepionClass = e.getClass().getCanonicalName();
            exceptionMessage = e.getLocalizedMessage();
        }
    }

    private void captureQueryEntity() throws IOException {
        InputStream inputStream = entity.getContent();
        try {
            captureQueryStream(inputStream);
        } catch (SocketException e) {
            if (exiting) {
                status = HttpActionOutputStatus.TIMEOUT;
            } else {
                throw e;
            }
        } finally {
            inputStream.close();
        }
    }

    private void captureQueryStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int readOnce;
        long readTotal = 0;

        while (true) {
            readOnce = inputStream.read(buffer, 0, BUFFER_SIZE);
            if (readOnce == -1) {
                break;
            }

            readTotal += readOnce;

            if ((maxCaptureSize > 0) && (readTotal > maxCaptureSize)) {
                status = HttpActionOutputStatus.OVERFLOWED;
                // Write remainder
                int remainder = readOnce - (int) (readTotal - maxCaptureSize);
                if (remainder > 0) {
                    outputStream.write(buffer, 0, remainder);
                }
                // Throw out of all the rest
                return;
            }

            outputStream.write(buffer, 0, readOnce);
        }
    }

    private void skipQueryOutput() {
        status = HttpActionOutputStatus.SKIPPED;

        try {
            EntityUtils.consume(entity);
        } catch (Exception e) {
            LOGGER.error("Error while skipping query data", e);
        }
    }

    public HttpActionOutput composeActionOutput() {
        byte[] data = null;

        if (status.isCaptured()) {
            byte[] capturedData = outputStream.toByteArray();
            if (ArrayUtils.isNotEmpty(capturedData)) {
                data = capturedData;
            }
        }

        HttpActionOutput output = new HttpActionOutput();
        output.setStatus(status);
        output.setData(data);
        output.setExcepionClass(excepionClass);
        output.setExceptionMessage(exceptionMessage);

        Header contentEncodingHeader = entity.getContentEncoding();
        if (contentEncodingHeader != null) {
            output.setEncoding(contentEncodingHeader.getValue());
        }

        Header contentTypeHeader = entity.getContentType();
        if (contentTypeHeader != null) {
            output.setType(contentTypeHeader.getValue());
        }

        return output;
    }

    public void interruptByTimeout() {
        // Unfortuatelly Thread.interrupt() doesn't interrupt InputStream.read() from HttpClient. So we have to
        // abort the current request which leads to SocketException. To discover that SocketException was thrown
        // because of timeout we set the flag
        this.exiting = true;
        this.request.abort();
    }

}
